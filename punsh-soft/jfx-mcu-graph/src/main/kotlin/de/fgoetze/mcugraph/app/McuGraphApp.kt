package de.fgoetze.mcugraph.app

import javafx.application.Application
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.ValueAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseDragEvent
import javafx.scene.layout.Region
import javafx.stage.Stage
import kotlin.math.*

class CustomAxis(
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

class McuGraphApp : Application() {
    override fun start(primaryStage: Stage?) {
        primaryStage!!
        primaryStage.scene = Scene(
            ScrollPane().apply {
                isFitToWidth = true
                val xAxis = CustomAxis().apply {
                    isAutoRanging = false
                    minorTickCount = 1
                    isAutoRanging = false
                }
                val yAxis = NumberAxis(-2.0, 2.0, .1)
                content = LineChart(xAxis, yAxis).apply {
                    val chartBG = lookup(".chart-plot-background") as Region
                    var dragStartPos: Point2D? = null
                    var dragStartLowerBound = 0.0
                    var dragStartUpperBound = 0.0
                    var dragZoomFactor = 0.0
                    setOnScroll { e ->
                        val zoomFactor = 1.1
                        if (e.deltaY.absoluteValue < .01 || dragStartPos != null) return@setOnScroll // omit nonsense scroll events (which appear a lot)
                        val xRange = xAxis.upperBound - xAxis.lowerBound
                        val xMid = xAxis.lowerBound + .5 * xRange
                        val newXRange = if (e.deltaY > 0) (xRange / zoomFactor) else (xRange * zoomFactor)
                        xAxis.lowerBound = xMid - .5 * newXRange
                        xAxis.upperBound = xMid + .5 * newXRange
                    }
                    setOnMousePressed { e ->
                        if (e.button === MouseButton.PRIMARY) {
                            dragStartPos = Point2D(e.x, e.y)
                            dragStartLowerBound = xAxis.lowerBound
                            dragStartUpperBound = xAxis.upperBound
                            dragZoomFactor = (xAxis.upperBound - xAxis.lowerBound) / chartBG.width
                            println(dragZoomFactor)
                        }
                    }
                    setOnMouseReleased { dragStartPos = null }
                    setOnMouseExited { dragStartPos = null }
                    addEventHandler(MouseDragEvent.MOUSE_DRAGGED) { e ->
                        println(dragStartPos)
                        val x = dragStartPos?.x ?: return@addEventHandler
                        val diff = (x - e.x) * dragZoomFactor
                        xAxis.lowerBound = dragStartLowerBound + diff
                        xAxis.upperBound = dragStartUpperBound + diff
                    }
                    title = "Mein erster Test"
                    isLegendVisible = true
                    data += XYChart.Series<Number, Number>().apply {
                        name = "Cool"
                        var x = -10.0
                        while (x <= 10.0) {
                            data += XYChart.Data(x, sin(x))
                            x += .1
                        }
                    }

                }
            },
            800.0, 600.0
        )
        primaryStage.title = "Hello"
        primaryStage.show()
    }
}