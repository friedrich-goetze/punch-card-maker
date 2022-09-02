package de.fgoetze.mcugraph.app

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TitledPane
import javafx.stage.Stage

class McuGraphApp : Application() {
    override fun start(primaryStage: Stage?) {
        primaryStage!!
        primaryStage.scene = Scene(
            TitledPane("Hey!", Label("Foo")),
            800.0, 600.0
        )
        primaryStage.title = "Hello"
        primaryStage.show()
    }
}