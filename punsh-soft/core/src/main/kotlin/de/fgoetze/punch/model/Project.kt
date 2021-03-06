package de.fgoetze.punch.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class PCProject(
    /** Project name. */
    var name: String,
    /** Punch card layout. */
    var layout: PCLayout,
    /** Maps the index of each element to a hole from the layout. */
    val dataToLayoutMapping: MutableList<Int>,
    /** The actual data. One row for each punch card. */
    val data: BitRows
)

/**
 * Efficient data structure for many rows of bit-data.
 */
@Serializable(with = BitRowsSerializer::class)
class BitRows(
    val columns: Int
) : Iterable<MutableList<Boolean>> {

    init {
        require(columns > 0)
    }

    private val longsPerRow = columns / 64 + if (columns % 64 > 0) 1 else 0
    private val rows = mutableListOf<LongArray>()

    val size: Int get() = rows.size
    val lastIndex: Int get() = rows.lastIndex
    val indices: IntRange get() = rows.indices

    /**
     * Returns a view on a single rows. Elements can be set. You cannot add or remove bits.
     */
    fun getRow(row: Int): MutableList<Boolean> {
        require(row in rows.indices)
        return BitsView(row)
    }

    /**
     * Checks if a certain bit is set.
     */
    fun get(row: Int, col: Int): Boolean {
        requireCoords(row, col)
        return BitsArrayOps.getBit(rows[row], col)
    }

    /**
     * Sets a certain bit.
     */
    fun set(row: Int, col: Int, value: Boolean): Boolean {
        requireCoords(row, col)
        return BitsArrayOps.setBit(rows[row], col, value)
    }

    private fun requireCoords(row: Int, col: Int) {
        require(row in rows.indices)
        require(col in 0 until columns)
    }

    /**
     * Adds an empty row at the given index.
     *
     * @param insertAt insert index. `-1` to add a row to the end.
     */
    fun addRow(insertAt: Int = -1): MutableList<Boolean> {
        require(insertAt == -1 || insertAt in 0..rows.size)

        val newRow = LongArray(longsPerRow)
        val insertIndex = if (insertAt < 0) rows.size else insertAt
        rows.add(insertIndex, newRow)

        return BitsView(insertIndex)
    }

    /**
     * Removes a row at the given index.
     */
    fun removeRow(index: Int) {
        require(index in rows.indices)
        rows.removeAt(index)
    }

    override fun iterator(): Iterator<MutableList<Boolean>> {
        var i = 0
        return object : Iterator<MutableList<Boolean>> {
            override fun hasNext(): Boolean = i < size

            override fun next(): MutableList<Boolean> {
                val r = getRow(i)
                i++
                return r
            }
        }
    }

    override fun hashCode(): Int = rows.fold(0) { h, arr -> h xor arr.sum().toInt() }

    override fun equals(other: Any?): Boolean = other is BitRows
            && other.rows.size == rows.size
            && other.rows.asSequence().zip(rows.asSequence()).all { it.first.contentEquals(it.second) }

    override fun toString(): String = "BitRows(rows: $size, columns: $columns)"

    private inner class BitsView(val row: Int) : AbstractMutableList<Boolean>() {

        private val longs = rows[row]

        override val size: Int = columns

        override fun add(index: Int, element: Boolean) {
            throw UnsupportedOperationException()
        }

        override fun removeAt(index: Int): Boolean {
            throw UnsupportedOperationException()
        }

        override fun set(index: Int, element: Boolean): Boolean {
            require(index in 0 until columns)
            return BitsArrayOps.setBit(longs, index, element)
        }

        override fun get(index: Int): Boolean {
            require(index in 0 until columns)
            return BitsArrayOps.getBit(longs, index)
        }

        override fun hashCode(): Int = longs.sum().toInt()

        override fun equals(other: Any?): Boolean = other is BitsView && other.longs.contentEquals(this.longs)

        override fun toString() = String(CharArray(columns) { if (get(it)) '1' else '0' })

    }
}

class BitRowsSerializer : KSerializer<BitRows> {
    override val descriptor: SerialDescriptor = RowsSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: BitRows) {
        val surrogate = RowsSurrogate(
            value.columns,
            (0 until value.size).map { value.getRow(it).toString() }
        )
        encoder.encodeSerializableValue(RowsSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): BitRows {
        val surrogate = decoder.decodeSerializableValue(RowsSurrogate.serializer())
        val r = BitRows(surrogate.columns)
        surrogate.rows.forEach { rowString ->
            val row = r.addRow()
            rowString.forEachIndexed { index, c ->
                row[index] = c != '0'
            }
        }
        return r
    }

}

@Serializable
@SerialName("BitRows")
private data class RowsSurrogate(
    val columns: Int,
    val rows: List<String>
)

private object BitsArrayOps {
    fun getBit(array: LongArray, index: Int): Boolean {
        val iArray = index / 64
        val iOff = index % 64
        val l = array[iArray]
        val m = 1L shl iOff
        return l and m != 0L
    }

    fun setBit(array: LongArray, index: Int, value: Boolean): Boolean {
        val iArray = index / 64
        val iOff = index % 64
        val l = array[iArray]
        val m = 1L shl iOff
        val old = l and m != 0L
        if (old == value) {
            return old // Nothing has changed.
        }
        array[iArray] =
            if (value) l or m
            else l and m.inv()
        return old
    }
}