package de.fgoetze.mcugraph.app

import de.fgoetze.mcugraph.*
import javafx.application.Application
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.SplitPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage

class McuGraphApp : Application() {

    private val model = AppModel()

    override fun start(primaryStage: Stage?) {
        primaryStage!!

        primaryStage.scene = Scene(
            SplitPane().apply {
                orientation = Orientation.HORIZONTAL
                items.add(VBox().apply {
                    val recorder = createRecordView()
                    val list = createSeriesListView(model)

                    children.setAll(recorder, list)
                    VBox.setVgrow(list, Priority.ALWAYS)
                })
                items.add(VBox().apply {
                    children.add(createGraphView(model).apply {
                        VBox.setVgrow(this, Priority.ALWAYS)
                    })
                    children.add(createDetailView(model))
                })
                setDividerPosition(0, .2)
            },
            800.0, 600.0
        )
        primaryStage.title = "MCU Graph Viewer"
        primaryStage.show()
    }
}