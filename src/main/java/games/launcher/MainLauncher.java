package games.launcher;


import games.engine.MainMenu;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;



public class MainLauncher extends Application {

    private static Stage primaryStageRef;

    @Override
    public void start(Stage primaryStage) {
        primaryStageRef = primaryStage; // 🔥 Assign here!
        MainMenu menu = new MainMenu(primaryStage);

        Scene menuScene = new Scene(menu, 800, 600 );
        primaryStage.setTitle("Multi-Game Launcher");
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }
    public static Stage getPrimaryStage() {
        return primaryStageRef;
    }

    public static void main(String[] args) {
        launch(args);
    }
}