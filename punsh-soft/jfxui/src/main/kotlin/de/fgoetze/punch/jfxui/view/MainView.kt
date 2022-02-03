package de.fgoetze.punch.jfxui.view

import javafx.scene.control.Button
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.StackPane

class MainView : TabPane(
    Tab("Project", StackPane(Button("1"))),
    Tab("P2", StackPane(Button("2"))),
    Tab("Pro3", StackPane(Button("3"))),
)