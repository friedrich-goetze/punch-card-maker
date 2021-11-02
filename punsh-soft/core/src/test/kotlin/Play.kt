import de.fgoetze.punch.model.*
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
        PCLayout("Grobstich", 500.0, 80.0).apply {
            dataGrids += PCHoleGrid(
                PCVHole(4.1, 42.0, 10.0),
                9.0, 9.0,
                40,
                10
            )
            structuralHoles += PCVHole(11.0, 31.0, 12.0)
        },
        bits
    )

    println(json.encodeToString(project.layout))

    val j = json.encodeToString(project)
    val p2 = json.decodeFromString<PCProject>(j)

    println("EQUAL ${project == p2}")
}