package de.fgoetze.mcugraph

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.bufferedWriter
import kotlin.io.path.isDirectory
import kotlin.math.min

private class Connection(val port: SerialPort, val file: Path, val writeTime: Boolean) : SerialPortDataListener {
    private val buf = ByteArray(1024)
    private var isBeginOfLine = true
    private val output = file.bufferedWriter(Charsets.US_ASCII)
    private val startTime = System.currentTimeMillis()

    init {
        if(!port.isOpen) port.openPort()
        port.addDataListener(this)
    }

    fun close() {
        port.closePort()
        port.removeDataListener()
        output.close()
    }

    override fun getListeningEvents() = SerialPort.LISTENING_EVENT_DATA_AVAILABLE
    override fun serialEvent(event: SerialPortEvent) {
        if (event.eventType != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) return
        var avail = port.bytesAvailable()
        while (avail > 0) {
            val nRead = min(buf.size, avail)
            avail -= nRead
            port.readBytes(buf, nRead.toLong())
            val chars = String(buf, 0, nRead, Charsets.US_ASCII)
            for (ch in chars) {
                if(isBeginOfLine && writeTime) {
                    output.write((System.currentTimeMillis() - startTime).toString())
                    output.write(" ")
                }
                isBeginOfLine = false
                when {
                    ch == '\n' -> {
                        output.write("\n")
                        isBeginOfLine = true
                    }
                    ch.isDigit() || ch == '.' -> output.append(ch)
                }
            }
        }
    }
}

fun createRecordView(): Region {

    val dirProperty = SimpleStringProperty(Path(".").absolutePathString())
    val fileNameProperty = SimpleStringProperty("")
    val connectionProperty = SimpleObjectProperty<Connection?>(null)
    val portCombo = ComboBox(FXCollections.observableList(SerialPort.getCommPorts().map { it.systemPortName })).apply {
        selectionModel.selectFirst()
    }
    val recordTSCB = CheckBox("Record Timestamps")

    return StackPane(VBox().apply {
        spacing = 2.0
        children += HBox().apply {
            /*
             * Dir row
             */
            children += TextField().apply {
                textProperty().bindBidirectional(dirProperty)
                HBox.setHgrow(this, Priority.ALWAYS)
            }
            children += Button("[]").apply {
                setOnAction {
                    dirProperty.value = DirectoryChooser().apply {
                        initialDirectory = File(dirProperty.value)
                    }.showDialog(scene.window)?.absolutePath
                }
            }
        }
        children += TextField().apply {
            textProperty().bindBidirectional(fileNameProperty)
        }
        children += recordTSCB
        children += HBox().apply {
            /*
             * Connection Row
             */
            children += portCombo
            children += Button("").apply {
                textProperty().bind(Bindings.createStringBinding({
                    if (connectionProperty.value != null) "Disconnect"
                    else "Connect"
                }, connectionProperty))
                disableProperty().bind(Bindings.createBooleanBinding({
                    val si = portCombo.selectionModel.selectedItem
                    si == null || si.isBlank()
                }, portCombo.selectionModel.selectedItemProperty()))
                setOnAction {
                    val curCon = connectionProperty.value
                    if (curCon == null) {
                        val dir = Path(dirProperty.value)
                        if(!dir.isDirectory()) {
                            println("no directory selected :/")
                            return@setOnAction
                        }
                        val fn = fileNameProperty.value?.trim()
                        if(fn?.isNotBlank() != true) {
                            println("no filename entered :/")
                            return@setOnAction
                        }
                        val port = SerialPort.getCommPorts().firstOrNull { it.systemPortName == portCombo.value }
                        if (port == null) {
                            println("Selected port not found :/")
                            return@setOnAction
                        }
                        if (port.isOpen) {
                            println("Selected port is already open :/")
                            return@setOnAction
                        }
                        port.openPort()
                        connectionProperty.value = Connection(port, dir.resolve(fn), recordTSCB.isSelected)
                    } else {
                        curCon.close()
                        connectionProperty.value = null
                        fileNameProperty.value = ""
                    }
                }
            }
        }
    })
}