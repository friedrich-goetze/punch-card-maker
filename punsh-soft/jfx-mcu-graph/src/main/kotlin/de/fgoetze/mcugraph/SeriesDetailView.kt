package de.fgoetze.mcugraph

import de.fgoetze.mcugraph.AppModel
import javafx.beans.binding.Binding
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

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


    return VBox().apply {
        spacing = 3.0
        alignment = Pos.TOP_LEFT
        children += Label().apply {
            textProperty().bind(Bindings.createStringBinding({
                appModel.selectedMcuSeries.value?.path ?: "no series selected"
            }, appModel.selectedMcuSeries))
        }
        children += HBox().apply {
            disableProperty().bind(appModel.selectedMcuSeries.mapValue { it == null })
            spacing = 5.0
            alignment = Pos.CENTER_LEFT

            children += Label("xOffset:")
            children += TextField().apply {
                val doubleProp = doubleOnly().withDefault(0.0)
                doubleProp.bindBidirectional(xOffsetProp.prop as Property<Double>)
            }

            children += Label("yOffset:")
            children += TextField().apply {
                val doubleProp = doubleOnly().withDefault(0.0)
                doubleProp.bindBidirectional(yOffsetProp.prop as Property<Double>)
            }

            children += CheckBox("Visible").apply {
                selectedProperty().bindBidirectional(visibleProp.prop)
            }
        }
    }
}