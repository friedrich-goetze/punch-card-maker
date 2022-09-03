package de.fgoetze.mcugraph

import javafx.geometry.Point2D
import javafx.scene.chart.LineChart
import javafx.scene.chart.ValueAxis
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseDragEvent
import javafx.scene.layout.Region
import kotlin.math.*

class NumberAxis2(
    lowerBound: Double = -10.0,
    upperBound: Double = 10.0,
    val visibleTicks: Int = 20
) : ValueAxis<Number>(lowerBound, upperBound) {

    private var format = "%.1f"

    override fun setRange(range: Any?, animate: Boolean) {
        val rangeProps = range as Array<Any>
        val lowerBound = rangeProps[0] as Double
        val upperBound = rangeProps[1] as Double
        val scale = rangeProps[3] as Double
        setLowerBound(lowerBound)
        setUpperBound(upperBound)
        currentLowerBound.set(lowerBound)
        setScale(scale)
    }

    override fun getRange(): Any {
        return arrayOf<Any?>(
            lowerBound,
            upperBound,
            1,
            scale,
            null
        )
    }

    override fun calculateTickValues(length: Double, range: Any?): MutableList<Number> {
        range as Array<Any>
        val a = range[0] as Double
        val b = range[1] as Double
        val min = min(a, b)
        val max = max(a, b)
        val len = max - min
        val basicTickWidth = len / visibleTicks
        val precision = ceil(log10(basicTickWidth) - .3)
        val iPrecision = precision.roundToInt()
        format = "%.${if (iPrecision < 0) -iPrecision else 0}f"

        val tickWidth = 10.0.pow(precision)

        val startI = (min / tickWidth).roundToInt()
        val endI = (max / tickWidth).roundToInt()
        val r = mutableListOf<Number>()
        for (i in startI..endI) {
            val v = i.toDouble() * tickWidth
            if (v in min..max) r += v
        }
        return r
    }

    override fun getTickMarkLabel(value: Number?): String {
        return String.format(format, value)
    }

    override fun calculateMinorTickMarks(): MutableList<Number> {
        return mutableListOf()
    }
}


fun <X, Y> LineChart<X, Y>.addMouseNavigation(zoomFactor: Double = 1.1) {
    val xValAxis = xAxis as? ValueAxis<Number> ?: throw IllegalArgumentException("xAxis must be ValueAxis.")
    val yValAxis = yAxis as? ValueAxis<Number> ?: throw IllegalArgumentException("yAxis must be ValueAxis.")

    xValAxis.isAutoRanging = false

    val chartBG = lookup(".chart-plot-background") as Region

    var dragStartPos: Point2D? = null
    var dragStartLowerBound = 0.0
    var dragStartUpperBound = 0.0
    var dragZoomFactor = 0.0

    setOnScroll { e ->
        dragStartPos = null
        if (e.deltaY.absoluteValue < .01 || xValAxis.lowerBound >= xValAxis.upperBound) return@setOnScroll // omit nonsense scroll events (which appear a lot)
        val xRange = xValAxis.upperBound - xValAxis.lowerBound
        val xMid = xValAxis.lowerBound + .5 * xRange
        val newXRange = if (e.deltaY > 0) (xRange / zoomFactor) else (xRange * zoomFactor)
        xValAxis.lowerBound = xMid - .5 * newXRange
        xValAxis.upperBound = xMid + .5 * newXRange

        e.consume()
    }
    setOnMousePressed { e ->
        if (e.button === MouseButton.PRIMARY) {
            dragStartPos = Point2D(e.x, e.y)
            dragStartLowerBound = xValAxis.lowerBound
            dragStartUpperBound = xValAxis.upperBound
            dragZoomFactor = (xValAxis.upperBound - xValAxis.lowerBound) / chartBG.width

            e.consume()
        }
    }
    setOnMouseReleased { dragStartPos = null }
    setOnMouseExited { dragStartPos = null }
    addEventHandler(MouseDragEvent.MOUSE_DRAGGED) { e ->
        val x = dragStartPos?.x ?: return@addEventHandler
        val diff = (x - e.x) * dragZoomFactor
        xValAxis.lowerBound = dragStartLowerBound + diff
        xValAxis.upperBound = dragStartUpperBound + diff

        println("${xValAxis.lowerBound} - ${xValAxis.upperBound}")
        e.consume()
    }
}