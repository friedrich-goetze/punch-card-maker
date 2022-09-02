module punchsoft.mcugraph {
    requires kotlin.stdlib;
    requires javafx.controls;
    requires javafx.graphics;
    exports de.fgoetze.mcugraph.app;
    opens de.fgoetze.mcugraph.app to javafx.graphics;
}