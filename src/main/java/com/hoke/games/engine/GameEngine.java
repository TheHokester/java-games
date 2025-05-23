package com.hoke.games.engine;



import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

public class GameEngine extends Pane {

    public Canvas canvas;
    private Game currentGame;
    private boolean isMouseClicked = false;
    private boolean isMouseDragged = false;
    private double mouseX = 0;
    private double mouseY = 0;
    private double dragX = 0;
    private double dragY = 0;
    private double clickX = 0;
    private double clickY = 0;
    private String keyPressed = "";
    public GameEngine(Game startingGame) {
        this.currentGame = startingGame;
        this.canvas = new Canvas(1280, 720); // Create actual drawing canvas
        getChildren().add(canvas);          // Add Canvas to Pane
        currentGame.start(canvas.getGraphicsContext2D());

        this.currentGame = startingGame;
        currentGame.setEngine(this);  // Pass engine into game
        currentGame.start(canvas.getGraphicsContext2D());

        canvas.setFocusTraversable(true);
        canvas.requestFocus();

        canvas.setOnMousePressed(e -> {
            isMouseClicked = true;
            currentGame.onClick(e.getX(), e.getY());
            clickX = e.getX();
            clickY = e.getY();
            InputManager.setMousePressed(true);
        });
        canvas.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
            InputManager.setMouse(e.getX(), e.getY());
        });
        canvas.setOnMouseDragged(e -> {
            isMouseDragged = true;
            mouseX = e.getX();
            mouseY = e.getY();
            InputManager.setMouse(e.getX(), e.getY());
        });
        canvas.setOnKeyTyped(e -> {

            InputManager.setTypedChar(e.getCharacter());
        });
        canvas.setOnMouseReleased(e -> {
            isMouseClicked = false;
            isMouseDragged = false;
            InputManager.setMouseReleased(true);
        });


        FixedRateGameLoop gameLoop = new FixedRateGameLoop(120, () ->{
            //this runs 120x per sec
            Platform.runLater(() -> {
                currentGame.update();
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                currentGame.render(gc);
                InputManager.reset();
            });
        });
        gameLoop.start();
    }

    public void switchGame(Game newGame) {
        this.currentGame = newGame;
        currentGame.start(canvas.getGraphicsContext2D());
    }
    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public String getKeyPressed() { return keyPressed; }

    public double getDragX() { return dragX; }

    public double getDragY() { return dragY; }

    public double getClickX() { return clickX; }

    public double getClickY() { return clickY; }

    public boolean isMouseClicked() { return isMouseClicked; }

    public boolean isMouseDragged() { return isMouseDragged; }
}
