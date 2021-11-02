package de.fgoetze.punch.model

import kotlinx.serialization.Serializable

/*
 * General layout of a Punch Card.
 */

@Serializable
data class PCLayout(
    var name: String,
    var width: Double,
    var height: Double,
    val dataGrids: MutableList<PCHoleGrid> = mutableListOf(),
    val structuralHoles: MutableList<PCVHole> = mutableListOf(),
    val textBoxes: MutableList<PCLTextBox> = mutableListOf()
)

@Serializable
data class PCHoleGrid(
    /** Location and size of the left-top hole in the grid. */
    var firstHole: PCVHole,
    var xSpacing: Double,
    var ySpacing: Double,
    var xHoleCount: Int,
    var yHoleCount: Int
)

enum class PCLTextType {
    /** Text, which is always the same. */
    STATIC,

    /** The number of the current card within the project. */
    NUMBER,

    /** The total number of cards within the project. */
    TOTAL_NUMBER,

    /** The number and total number. I.e. "42 / 666". */
    NUMBER_AND_TOTAL_NUMBER,

    /** The projects name. */
    PROJECT_NAME
}

@Serializable
data class PCLTextBox(
    var type: PCLTextType,
    override var x: Double,
    override var y: Double,
    override var width: Double,
    override var height: Double,
    override var align: PCVTextAlign = PCVTextAlign.START,
    override var rotate: Double = 0.0,
) : PCVTextBox