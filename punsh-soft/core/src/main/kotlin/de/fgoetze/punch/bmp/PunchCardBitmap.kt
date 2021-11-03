package de.fgoetze.punch.bmp

import de.fgoetze.punch.model.BitRows
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO

/**
 * Loads a bitmap into a data structure which is suited to be converted into a punch card representation.
 */
class PunchCardBitmap private constructor(
    inputStream: InputStream
) {

    /** Count of columns in the bitmap file. */
    val columns: Int

    /** Count of rows in the bitmap file. */
    val rows: Int

    /** All colors found in the bitmap file. */
    val colors: List<BitmapColor>

    /** Mappings from all [colors], where [BitmapColor.rgb] is mapped to the corresponding [BitmapColor]. */
    val rgb2color: Map<Int, BitmapColor>;

    private val pixels: IntArray

    init {
        val loadedBufImg = ImageIO.read(inputStream)

        // Convert loaded bitmap to a well known pixel format.
        // This is because some bitmaps come with an indexed format.
        val bufImg = BufferedImage(loadedBufImg.width, loadedBufImg.height, BufferedImage.TYPE_3BYTE_BGR)
        bufImg.graphics.also { g ->
            g.drawImage(loadedBufImg, 0, 0, null)
            g.dispose()
        }

        this.columns = bufImg.width
        this.rows = bufImg.height
        this.pixels = IntArray(columns * rows)

        val intColor2count = mutableMapOf<Int, Long>()
        for (y in 0 until rows) {
            for (x in 0 until columns) {
                val pixel = bufImg.getRGB(x, y)
                pixels[y * columns + x] = pixel
                intColor2count.merge(pixel, 1L) { i, _ -> i + 1 }
            }
        }

        this.colors = intColor2count.entries
            .map { (ic, count) ->
                val r = (ic and 0xFF0000) ushr 16
                val g = (ic and 0xFF00) ushr 8
                val b = (ic and 0xFF)

                BitmapColor(ic, String.format("#%02X%02X%02X", r, g, b), r, g, b, count)
            }
            .sortedBy { -it.count }

        this.rgb2color = colors.associateBy { it.rgb }
    }

    fun getPixel(x: Int, y: Int): Int {
        require(x in 0 until columns && y in 0 until rows)
        return pixels[y * columns + x]
    }

    companion object {
        /** Reads a [PunchCardBitmap] from an input stream.
         *
         * @throws java.io.IOException
         */
        fun read(inputStream: InputStream) = PunchCardBitmap(inputStream)
    }
}

/** A color which occurs on a parsed bitmap file. */
data class BitmapColor(
    /** RGB integer value. */
    val rgb: Int,
    /** I.e. `#FE0EA4`'. */
    val cssColor: String,
    val r: Int, val g: Int, val b: Int,
    /** How often it occurs in the bitmap. */
    val count: Long
)

/**
 * Creates [BitRows] out of a bitmap.
 *
 * **The bitmap must consist of exactly two colors.**
 *
 * @param trueColor The color which will be interpreted as `1` or `true`.
 * @param reverseRows If `true`, the first row of the result will be the last line of the bitmap.
 */
fun PunchCardBitmap.createBitRows(
    trueColor: Int,
    reverseRows: Boolean = true
): BitRows {
    require(columns > 0)
    require(rows > 0)
    require(colors.size == 2)
    require(rgb2color.containsKey(trueColor))

    val r = BitRows(columns)

    for (y in (0 until rows).let { if (reverseRows) it.reversed() else it }) {
        val bitRow = r.addRow()
        for (x in 0 until columns) {
            bitRow[x] = getPixel(x, y) == trueColor
        }
    }

    return r
}