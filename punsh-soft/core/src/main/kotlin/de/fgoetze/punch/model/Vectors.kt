package de.fgoetze.punch.model

import kotlinx.serialization.Serializable

/*
 * Vector graphics API for Punch Cards.
 *
 * Can be used to generate SVG, GCode and more.
 *
 * All dimensions are in mm and relative to the top-left of the punch card.
 */

/** A simple 2D point coordinate. */
interface PCVPoint {
    /** X Position in mm relative to the left side of the punch card. */
    var x: Double

    /** Y Position in mm relative to the top side of the punch card. */
    var y: Double
}

/** Width and height of a 2D object. */
interface PCVSize {
    /** Width in mm. */
    var width: Double

    /** Height in mm. */
    var height: Double
}

enum class PCVTextAlign { START, CENTER, END }

/** Point combined with size and angle. */
interface PCVTextBox : PCVPoint, PCVSize {
    /** The alignment of the text within the text-box. */
    var align: PCVTextAlign

    /** Rotation of the text box in degree. Center of rotation is the left bottom corner. Rotation is clockwise. */
    var rotate: Double
}

/** Punch card vector graphic data structure. */
@Serializable
data class PCVectors(
    override var width: Double,
    override var height: Double,
    /** List of all holes. */
    val holes: MutableList<PCVHole> = mutableListOf(),
    /** List of all texts. */
    val texts: MutableList<PCVText> = mutableListOf()
) : PCVSize

@Serializable
data class PCVHole(
    /** Diameter of the hole in mm. */
    var diameter: Double,
    override var x: Double,
    override var y: Double
) : PCVPoint

@Serializable
data class PCVText(
    var text: String,
    override var x: Double,
    override var y: Double,
    override var width: Double,
    override var height: Double,
    override var align: PCVTextAlign = PCVTextAlign.START,
    override var rotate: Double = 0.0
) : PCVTextBox


