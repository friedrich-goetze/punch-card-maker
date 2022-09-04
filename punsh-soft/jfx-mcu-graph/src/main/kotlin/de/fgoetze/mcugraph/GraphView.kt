package de.fgoetze.mcugraph

import de.fgoetze.mcugraph.AppModel
import javafx.scene.chart.LineChart
import javafx.scene.control.Label
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane

fun createGraphView(appModel: AppModel): Region {
    val xA = NumberAxis2(0.0, 100.0, 20)
    val yA = NumberAxis2(0.0, 100.0, 15)

    return LineChart<Number, Number>(xA, yA).apply {
        addMouseNavigation()
        createSymbols = false
        animated = false
        appModel.revision.addListener { _, _, _ ->
            if (appModel.displayFXSeries.isEmpty() || appModel.displayFXSeries.all { it.data.isEmpty() }) {
                yA.lowerBound = 0.0
                yA.upperBound = 100.0
            } else {
                yA.lowerBound =
                    appModel.displayFXSeries.minOfOrNull { it.data.minOfOrNull { it.yValue.toDouble() } ?: 0.0 } ?: 0.0
                yA.upperBound =
                    appModel.displayFXSeries.maxOfOrNull { it.data.maxOfOrNull { it.yValue.toDouble() } ?: 0.0 }
                        ?: 100.0
                val extra = .05 * (yA.upperBound - yA.lowerBound)
                yA.lowerBound -= extra
                yA.upperBound += extra
            }
        }
        data = appModel.displayFXSeries
    }
}