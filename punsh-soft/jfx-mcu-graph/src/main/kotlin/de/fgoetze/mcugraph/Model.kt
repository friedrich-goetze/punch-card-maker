package de.fgoetze.mcugraph

import javafx.beans.binding.Binding
import javafx.beans.property.*
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.chart.XYChart
import java.util.IdentityHashMap

data class McuSeries(
    val path: String? = null,
    val xyData: List<Double>,
    val visibleProperty: BooleanProperty = SimpleBooleanProperty(false),
    val series: XYChart.Series<Number, Number> = XYChart.Series<Number, Number>(),
    val xOffsetProperty: DoubleProperty = SimpleDoubleProperty(0.0),
    val yOffsetProperty: DoubleProperty = SimpleDoubleProperty(0.0),
)

class AppModel {

    private val mutDisplayFXSeries: ObservableList<XYChart.Series<Number, Number>> = FXCollections.observableArrayList()
    val selectedMcuSeries: ObjectProperty<McuSeries?> = SimpleObjectProperty(null)
    val mcuSeries: ObservableList<McuSeries> = FXCollections.observableArrayList()
    val displayFXSeries = FXCollections.unmodifiableObservableList(mutDisplayFXSeries)

    private val series2listeners = IdentityHashMap<McuSeries, McuListeners>()


    init {
        mcuSeries.addListener(ListChangeListener { c ->
            while (c.next()) {
                c.addedSubList.forEach {
                    val l = McuListeners(it)
                    series2listeners[it] = l
                    computeSeries(it)
                }
                c.removed.forEach {
                    series2listeners.remove(it)?.dispose() ?: return@forEach
                    mutDisplayFXSeries.remove(it.series)
                }
                if (selectedMcuSeries.value != null && !c.list.contains(selectedMcuSeries.value))
                    selectedMcuSeries.value = null
            }
            updateDisplayFXSeries()
        })
    }

    private fun onVisibleUpdated(series: McuSeries) {
        updateDisplayFXSeries()
    }

    private fun computeSeries(series: McuSeries) {
        val newSeries = FXCollections.observableArrayList<XYChart.Data<Number, Number>>()
        val xOff = series.xOffsetProperty.value
        val yOff = series.yOffsetProperty.value
        for (i in series.xyData.indices step 2) {
            newSeries += XYChart.Data(
                series.xyData[i] + xOff,
                series.xyData[i + 1] + yOff
            )
        }
        series.series.data = newSeries
    }

    private fun updateDisplayFXSeries() {
        val visible = mcuSeries.filter { it.visibleProperty.value }
        mutDisplayFXSeries.removeAll(mutDisplayFXSeries.filter { fx -> visible.none { vis -> vis.series === fx } })
        visible.forEachIndexed { index, mcuSeries ->
            if (mutDisplayFXSeries[index] !== mcuSeries.series)
                mutDisplayFXSeries.add(index, mcuSeries.series)
        }
    }

    private inner class McuListeners(private val mcuSeries: McuSeries) {
        private val visible = ChangeListener<Boolean> { _, old, new ->
            if (old != new) onVisibleUpdated(mcuSeries)
        }
        private val xOffset =
            ChangeListener<Number> { _, old, new -> if (old != new) computeSeries(mcuSeries) }
        private val yOffset =
            ChangeListener<Number> { _, old, new -> if (old != new) computeSeries(mcuSeries) }

        init {
            mcuSeries.visibleProperty.addListener(visible)
            mcuSeries.xOffsetProperty.addListener(xOffset)
            mcuSeries.yOffsetProperty.addListener(yOffset)
        }

        fun dispose() {
            mcuSeries.visibleProperty.removeListener(visible)
            mcuSeries.xOffsetProperty.removeListener(xOffset)
            mcuSeries.yOffsetProperty.removeListener(yOffset)
        }
    }
}

