package de.fgoetze.punch.svg

import de.fgoetze.punch.model.PCLayout
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.xml

private const val defaultStrokeColor = "#000000"
private const val dataHoleStrokeColor = "#FFAA00"
private const val structHoleStrokeColor = "#00AAFF"

fun PCLayout.renderSVG(appendable: Appendable, colors: Boolean = true) {
    fun Node.applyStyle(strokeColor: String = defaultStrokeColor, strokeWidth: Double = 0.4) {
        attribute("style", "fill:none;stroke:${if (colors) strokeColor else "#000000"};stroke-width:${strokeWidth}mm")
    }

    xml("svg", "UTF-8") {
        attribute("width", "${width}mm")
        attribute("height", "${height}mm")
//        attribute("viewBox", "0 0 $width $height")
        attribute("version", "1.1")
        attribute("id", "punch-card-layout-${hashCode()}")
        "g" {
            // Punch Card Rect.
            "rect" {
                applyStyle(strokeWidth = .6)
                attribute("width", "${width}mm")
                attribute("height", "${height}mm")
                attribute("x", "0.0mm")
                attribute("y", "0.0mm")
            }

            // Data holes
            var holeNum = 0
            dataGrids.forEach { grid ->
                for (x in 0 until grid.xHoleCount) {
                    for (y in 0 until grid.yHoleCount) {
                        "g" {
                            val holeX = grid.firstHole.x + (x.toDouble() * grid.xSpacing)
                            val holeY = grid.firstHole.y + (y.toDouble() * grid.ySpacing)
                            val holeR = grid.firstHole.diameter / 2.0
                            "ellipse" {
                                applyStyle(strokeColor = dataHoleStrokeColor)
                                attribute("id", "data-hole-$holeNum")
                                attribute("cx", "${holeX}mm")
                                attribute("cy", "${holeY}mm")
                                attribute("rx", "${holeR}mm")
                                attribute("ry", "${holeR}mm")
                            }

                            "text" {
                                attribute(
                                    "style",
                                    "font-style:normal;font-variant:normal;font-weight:normal;font-stretch:normal;font-family:sans-serif;font-size:${.9 * holeR}mm"
                                )
                                attribute("x", "${holeX}mm")
                                attribute("y", "${holeY}mm")
                                attribute("dominant-baseline", "middle")
                                attribute("text-anchor", "middle")
                                -"$holeNum"
                            }
                        }
                        holeNum++
                    }
                }
            }

            structuralHoles.forEachIndexed { holeIndex, hole ->
                "g" {
                    "ellipse" {
                        applyStyle(strokeColor = structHoleStrokeColor)
                        attribute("id", "struct-hole-$holeIndex")
                        attribute("cx", "${hole.x}mm")
                        attribute("cy", "${hole.y}mm")
                        attribute("rx", "${hole.diameter / 2.0}mm")
                        attribute("ry", "${hole.diameter / 2.0}mm")
                    }
                }
            }

        }
    }.writeTo(appendable)
}

fun PCLayout.renderSVG(colors: Boolean = true): String = StringBuilder().also { renderSVG(it, colors) }.toString()