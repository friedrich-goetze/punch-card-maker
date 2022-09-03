package de.fgoetze.mcugraph

import de.fgoetze.mcugraph.app.McuGraphApp
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty

fun main(args: Array<String>) {
    if (args.contains("-fix-black") || System.getenv("FIX_BLACK") == "true")
        System.setProperty("prism.order", "sw")

    Application.launch(McuGraphApp::class.java)
}