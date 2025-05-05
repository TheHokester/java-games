package com.example.games.engine;

import com.example.games.gameLib.chests.ChestGame;
import com.example.games.gameLib.ExampleGame;
import com.example.games.launcher.MainLauncher;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class AbstractGame implements Game {
    protected boolean gameOver = false;


    public enum GameResult {
        WIN,
        LOSS,
        INPROGRESS


    }
    public class AnimationInfo {
        public String chestState;
        public int frame;
        public boolean isRunning;

        // Default constructor with default values
        public AnimationInfo() {
            this.chestState = "inactive";
            this.frame = 0;
            this.isRunning = false;
        }

        // Constructor with parameters
        public AnimationInfo(String chestState, int frame, boolean isRunning) {
            this.chestState = chestState;
            this.frame = frame;
            this.isRunning = isRunning;
        }
    }

    //nav bar properties
    protected static final int NAV_BAR_HEIGHT = 100;
    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    // You can also optionally define empty update() if not needed
    @Override
    public void update() {
        // Default: do nothing
    }
    protected GameEngine engine;

    @Override
    public void setEngine(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void render(GraphicsContext gc) {
        drawNavBar(gc);
        renderGame(gc);
    }

    @Override
    public void onClick(double x, double y) {
        if(y<=NAV_BAR_HEIGHT){
            handleNavClick(x);
        } else {
            onGameClick(x, y);
        }
    }

    protected void drawNavBar(GraphicsContext gc) {
        gc.setFill(Color.GRAY);
        gc.fillRect(0,0,1280,NAV_BAR_HEIGHT);

        gc.setFill(Color.BLACK);
        gc.fillText("Main Menu", 100, 50);
        gc.fillText("Example Game", 300, 50);
        gc.fillText("Chest Game", 500, 50);
    }

    protected void handleNavClick(double x) {
        if(x < 200) {
            MainMenu menu = new MainMenu(MainLauncher.getPrimaryStage());
            Scene menuScene = new Scene(menu,800,600);
            MainLauncher.getPrimaryStage().setScene(menuScene);
        } else if(x<400) {
            GameEngine engine =  new GameEngine(new ExampleGame());
            Scene gameScene = new Scene(engine,1280,720);
            MainLauncher.getPrimaryStage().setScene(gameScene);
        } else if(x < 600) {
            GameEngine engine = new GameEngine(new ChestGame());
            Scene gameScene = new Scene(engine, 1280, 720);
            MainLauncher.getPrimaryStage().setScene(gameScene);
        }
    }

    protected abstract void renderGame(GraphicsContext gc);

    protected abstract void onGameClick(double x, double y);


}
