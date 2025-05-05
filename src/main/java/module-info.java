module com.example.games {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

//    exports com.example.games;
    opens com.example.games to javafx.fxml;
    exports com.example.games.launcher;
    opens com.example.games.launcher to javafx.fxml;
    exports com.example.games.engine;
    opens com.example.games.engine to javafx.fxml;
    exports com.example.games.gameLib;
    opens com.example.games.gameLib to javafx.fxml;
    exports com.example.games.gameLib.chests;
    opens com.example.games.gameLib.chests to javafx.fxml;
}