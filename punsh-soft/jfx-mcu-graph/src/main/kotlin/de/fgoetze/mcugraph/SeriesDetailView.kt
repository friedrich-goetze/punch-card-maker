package de.fgoetze.mcugraph

import de.fgoetze.mcugraph.AppModel
import javafx.beans.binding.Binding
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane

fun createDetailView(appModel: AppModel): Region {
    val isSeriesSelectedBinding = Bindings.createBooleanBinding({
        appModel.selectedMcuSeries.value != null
    }, appModel.selectedMcuSeries)

    val xOffsetProp = BidirectionalPropertyBinding(SimpleDoubleProperty())
    val yOffsetProp = BidirectionalPropertyBinding(SimpleDoubleProperty())
    val visibleProp = BidirectionalPropertyBinding(SimpleBooleanProperty());

    fun updateProps() {
        val sel = appModel.selectedMcuSeries.value
        xOffsetProp.bind(sel?.xOffsetProperty)
        yOffsetProp.bind(sel?.yOffsetProperty)
        visibleProp.bind(sel?.visibleProperty)
    }
    updateProps()
    appModel.selectedMcuSeries.addListener { _, _, _ -> updateProps() }


    return HBox().apply {
        spacing = 5.0
        children += Label().apply {
            textProperty().bind(Bindings.createStringBinding({
                appModel.selectedMcuSeries.value?.path ?: "no series selected"
            }, appModel.selectedMcuSeries))
        }
        children += TextField().apply {
            val doubleProp = doubleOnly().withDefault(0.0)
            doubleProp.bindBidirectional(xOffsetProp.prop as Property<Double>)
        }
    }
}