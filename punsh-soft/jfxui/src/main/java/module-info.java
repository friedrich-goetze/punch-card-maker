module de.fgoetze.punch.jfxui {
    requires de.fgoetze.punsh.core;
    requires kotlin.stdlib;
    requires javafx.controls;
    requires javafx.graphics;
    exports de.fgoetze.punch.jfxui.app;
    opens de.fgoetze.punch.jfxui.app to javafx.graphics;
}