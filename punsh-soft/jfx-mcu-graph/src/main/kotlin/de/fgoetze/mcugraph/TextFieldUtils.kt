package de.fgoetze.mcugraph

import javafx.beans.binding.Binding
import javafx.beans.property.DoubleProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.util.converter.DoubleStringConverter

fun TextField.doubleOnly(min: Double? = null, max: Double? = null, defaultValue: Double = min ?: 0.0): Property<Double?> {

    textFormatter = TextFormatter(DoubleStringConverter(), defaultValue) {
        val txt = it.controlNewText
        if (txt == null || txt.isBlank()) return@TextFormatter it
        val newVal = txt.toDoubleOrNull()
        if (newVal != null && (min == null || newVal >= min) && (max == null || newVal <= max)) it
        else null
    }

    return textProperty().convertBidirectional({it.toDoubleOrNull()}, { it?.toString() })
}