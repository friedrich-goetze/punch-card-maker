module punchsoft.jfxui {
    requires punchsoft.core;
    requires kotlin.stdlib;
    requires javafx.controls;
    requires javafx.graphics;
    exports de.fgoetze.punch.jfxui.app;
    opens de.fgoetze.punch.jfxui.app to javafx.graphics;
}