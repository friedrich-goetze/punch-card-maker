package de.fgoetze.mcugraph.app

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.ScrollPane
import javafx.stage.Stage
import kotlin.math.sin

class McuGraphApp : Application() {
    override fun start(primaryStage: Stage?) {
        primaryStage!!
        primaryStage.scene = Scene(
            ScrollPane().apply {
                isFitToWidth = true
                val xAxis = NumberAxis(-10.0, 10.0, 2.0)
                val yAxis = NumberAxis(-2.0, 2.0, .1)
                content = LineChart(xAxis, yAxis).apply {
                    val chartBG = lookup(".chart-plot-background")
//                    chartBG.setOnMouseMoved { println("${xAxis.getValueForDisplay(it.x)}, ${yAxis.getValueForDisplay(it.y)}") }
                    chartBG.setOnScroll { e ->
                        val xRange = xAxis.upperBound - xAxis.lowerBound
                        val xMid = xAxis.lowerBound + .5 * xRange
                        val newHalfXRange = .5 * xRange * (1.1 * if (e.deltaY > 0) 1.0 else -1.0)
//                        xAxis.lowerBound = xMid -
                        println(e.x)
                        println(xAxis.getDisplayPosition(e.x))
                    }
                    title = "Mein erster Test"
                    isLegendVisible = true
                    (xAxis as NumberAxis).lowerBound = -50.0
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