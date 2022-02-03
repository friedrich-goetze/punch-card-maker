package de.fgoetze.punch.jfxui.app

import de.fgoetze.punch.jfxui.view.MainView
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TitledPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style

class PunchApp : Application() {
    override fun start(primaryStage: Stage?) {
        primaryStage!!
        primaryStage.scene = Scene(
            TitledPane("Hey!", MainView()),
            800.0, 600.0
        )
        JMetro(Style.DARK).scene = primaryStage.scene;
        primaryStage.title = "Hello"
        primaryStage.show()
    }
}