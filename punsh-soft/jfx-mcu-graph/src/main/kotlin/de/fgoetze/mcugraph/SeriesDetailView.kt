package de.fgoetze.mcugraph

import de.fgoetze.mcugraph.AppModel
import javafx.beans.binding.Bindings
import javafx.scene.control.Label
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane

fun createDetailView(appModel: AppModel): Region {
    return StackPane(Label().apply {
        textProperty().bind(Bindings.createStringBinding({
            appModel.selectedMcuSeries.value?.path ?: "nothing selected"
        }, appModel.selectedMcuSeries))
    })
}