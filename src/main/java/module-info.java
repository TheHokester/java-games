module com.hoke.games {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

//    exports com.hoke.games;
//    opens   com.hoke.games to javafx.fxml;
    exports com.hoke.games.launcher;
    opens   com.hoke.games.launcher to javafx.fxml;
    exports com.hoke.games.engine;
    opens   com.hoke.games.engine to javafx.fxml;
    exports com.hoke.games.gameLib;
    opens   com.hoke.games.gameLib to javafx.fxml;
    exports com.hoke.games.gameLib.chests;
    opens   com.hoke.games.gameLib.chests to javafx.fxml;
}