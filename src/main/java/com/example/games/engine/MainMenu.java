package com.example.games.engine;

import com.example.games.gameLib.ExampleGame;
import com.example.games.gameLib.chests.ChestGame;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class MainMenu extends VBox {
    public MainMenu(Stage primaryStage) {
        setSpacing(20);
        setAlignment(Pos.CENTER);

        Button exampleGameButton = new Button("Play Example Game");
        Button exitButton = new Button("Exit");
        Button chestGameButton = new Button("Play Chest Game");

        exampleGameButton.setOnAction(e ->{
            GameEngine engine = new GameEngine(new ExampleGame());
            Scene scene = new Scene(engine);
            primaryStage.setScene(scene);
        });
        chestGameButton.setOnAction(e ->{
            GameEngine engine = new GameEngine(new ChestGame());
            Scene scene = new Scene(engine);
            primaryStage.setScene(scene);
        });
        exitButton.setOnAction(e ->{
            primaryStage.close();
        });

        getChildren().addAll(exampleGameButton, chestGameButton, exitButton);
    }
}
