module punchsoft.jfxui {
    requires punchsoft.core;
    requires kotlin.stdlib;
    requires javafx.controls;
    requires javafx.graphics;
    requires org.jfxtras.styles.jmetro;
    exports de.fgoetze.punch.jfxui.app;
    opens de.fgoetze.punch.jfxui.app to javafx.graphics;
}