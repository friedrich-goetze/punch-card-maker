package de.fgoetze.mcugraph

import javafx.scene.control.Button
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import java.io.File

fun createSeriesListView(appModel: AppModel): Region = VBox().apply {
    spacing = 4.0;
    children += ListView(appModel.mcuSeries).apply {
        VBox.setVgrow(this, Priority.ALWAYS)
        setCellFactory {
            object : ListCell<McuSeries>() {
                override fun updateItem(item: McuSeries?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (!empty && item != null) {
                        text = item.path ?: "unnamed"
                    }
                }
            }
        }
        appModel.selectedMcuSeries.bind(selectionModel.selectedItemProperty())
    }
    var lastDir = File(".")
    children += Button("Load ...").apply {
        setOnAction {
            val file = FileChooser().apply {
                initialDirectory = lastDir
            }.showOpenDialog(scene.window)
            if (file != null) {
                val xy = mutableListOf<Double>()
                var lastX = Double.NaN
                file.bufferedReader(Charsets.US_ASCII).use { r ->
                    while (true) {
                        val l = r.readLine()?.trim() ?: break
                        val parts = l.split(" ").mapNotNull { it.toDoubleOrNull() }.filter { !it.isNaN() && it.isFinite() }
                        val x = when (parts.size) {
                            0 -> continue
                            1 -> if (lastX.isNaN()) 0.0 else lastX + 1.0
                            else -> parts[0]
                        }
                        if (!lastX.isNaN() && x <= lastX) continue
                        val y = if (parts.size == 1) parts[0] else parts[1]
                        xy.add(x)
                        xy.add(y)
                        lastX = x
                    }
                }
                lastDir = file.parentFile
                appModel.mcuSeries.add(McuSeries(file.name, xy))
            }
        }
    }
}