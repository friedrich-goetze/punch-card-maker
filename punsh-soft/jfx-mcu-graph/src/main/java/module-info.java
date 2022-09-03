module punchsoft.mcugraph {
    requires kotlin.stdlib;
    requires javafx.controls;
    requires javafx.graphics;
    requires com.fazecast.jSerialComm;
    requires kotlin.stdlib.jdk7;
    exports de.fgoetze.mcugraph.app;
    opens de.fgoetze.mcugraph.app to javafx.graphics;
}