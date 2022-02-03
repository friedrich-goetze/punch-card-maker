package de.fgoetze.punch.jfxui

import de.fgoetze.punch.model.PCProject
import javafx.beans.property.SimpleObjectProperty

class UiCtx {
    val curProject = SimpleObjectProperty<PCProject?>(null)
}