package de.fgoetze.mcugraph

import de.fgoetze.mcugraph.AppModel
import javafx.scene.control.Label
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane

fun createGraphView(appModel: AppModel): Region {
    return StackPane(Label("GraphView"))
}