import de.fgoetze.punch.model.PCLayout
import de.fgoetze.punch.svg.renderSVG
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlin.io.path.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.inputStream

@ExperimentalSerializationApi
fun main() {
    val file = Path("./data/chemnitzer_grobstich.json")
    val pc = Json.decodeFromStream<PCLayout>(file.inputStream().buffered())
    println(
        Path("/home/fgoetze/tmp/rendered.svg").bufferedWriter().use {
            pc.renderSVG(
                it, false
            )
        }
    )
}