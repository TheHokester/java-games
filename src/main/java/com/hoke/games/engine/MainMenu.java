package com.hoke.games.engine;


import com.hoke.games.gameLib.blackJack.BlackJack;
import com.hoke.games.gameLib.chests.ChestGame;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class MainMenu extends VBox {
    public MainMenu(Stage primaryStage) {
        setSpacing(20);
        setAlignment(Pos.CENTER);

        Button blackJackButton = new Button("Play BlackJack");
        Button exitButton = new Button("Exit");
        Button chestGameButton = new Button("Play Chest Game");

        blackJackButton.setOnAction(e ->{
            GameEngine engine = new GameEngine(new BlackJack());
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

        getChildren().addAll(blackJackButton, chestGameButton, exitButton);
    }
}
