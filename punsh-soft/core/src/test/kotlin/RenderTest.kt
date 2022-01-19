import de.fgoetze.punch.bmp.PunchCardBitmap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.io.path.Path
import kotlin.io.path.inputStream

@ExperimentalSerializationApi
fun main() {
    val file = Path("./data/2688_1291.bmp")
    val bitmap = PunchCardBitmap.read(file.inputStream())
//
//    println("c=${bitmap.columns}, r=${bitmap.rows}, c=${bitmap.colors}")
//    bitmap.parsedData.forEachIndexed { index, ints ->
//        println("$index: $ints")
//    }
}