import de.fgoetze.punch.api.BitRows
import de.fgoetze.punch.api.PCLayout
import de.fgoetze.punch.api.PCProject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { prettyPrint = true }

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

    val project = PCProject(
        "Foo Project",
        PCLayout("Grobstich", 500.0, 80.0),
        bits
    )

    val j = json.encodeToString(project)
    val p2 = json.decodeFromString<PCProject>(j)

    println("EQUAL ${project == p2}")
}