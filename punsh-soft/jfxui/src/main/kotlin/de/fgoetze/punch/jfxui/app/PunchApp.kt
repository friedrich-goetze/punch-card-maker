package de.fgoetze.punch.jfxui.app

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class PunchApp : Application() {
    override fun start(primaryStage: Stage?) {
        primaryStage!!
        primaryStage.scene = Scene(
            StackPane(
                Button("Hello World")
            ),
            800.0, 600.0
        )
        primaryStage.title = "Hello"
        primaryStage.show()
    }
}