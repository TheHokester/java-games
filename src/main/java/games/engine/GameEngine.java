package games.engine;



import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

public class GameEngine extends Pane {

    private Canvas canvas;
    private Game currentGame;
    private double mouseX;
    private double mouseY;
    public GameEngine(Game startingGame) {
        this.currentGame = startingGame;
        this.canvas = new Canvas(1280, 720); // Create actual drawing canvas
        getChildren().add(canvas);          // Add Canvas to Pane
        currentGame.start();

        this.currentGame = startingGame;
        currentGame.setEngine(this);  // Pass engine into game
        currentGame.start();

        canvas.setOnMouseClicked(e -> {
            currentGame.onClick(e.getX(), e.getY());
        });
        canvas.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });


        FixedRateGameLoop gameLoop = new FixedRateGameLoop(120, () ->{
            //this runs 120x per sec
            Platform.runLater(() -> {
                currentGame.update();
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                currentGame.render(gc);
            });
        });
        gameLoop.start();
    }

    public void switchGame(Game newGame) {
        this.currentGame = newGame;
        currentGame.start();
    }
    public double getMouseX() {
        return mouseX;
    }
    public double getMouseY() {
        return mouseY;
    }
}
