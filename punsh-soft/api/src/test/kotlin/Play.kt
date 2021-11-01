import de.fgoetze.punch.api.BitRows

fun main() {
    val bits = BitRows(100)
    for (i in 0..99) {
        bits.addRow()[i] = true
    }

    bits.forEachIndexed { index, row ->
        println("$index: $row")
    }
    println(bits.getRow(98)[97])
    println(bits.getRow(98)[98])
    println(bits.getRow(98)[99])
    bits.set(98, 97, true)
    bits.set(98, 98, false)
    bits.set(98, 99, true)
    println(bits.getRow(98))
    println(bits.getRow(98))
    println(bits.getRow(98))
}