package com.example.games.gameLib;


import com.example.games.engine.AbstractGame;
import com.example.games.engine.Game;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ExampleGame extends AbstractGame {

    private final double circleX = 300;
    private final double circleY = 200;
    private final double circleRadius = 30;
    private boolean clicked = false;

    @Override
    public void start() {
        clicked = false; // Reset state if re-entered
    }

    @Override
    protected void renderGame(GraphicsContext gc) {
        if (clicked) {
            gc.setFill(Color.GREEN);
            gc.fillText("You clicked the circle! You win!", 250, 250);
        } else {
            gc.setFill(Color.BLUE);
            gc.fillOval(circleX - circleRadius, circleY - circleRadius + NAV_BAR_HEIGHT,
                    circleRadius * 2, circleRadius * 2);

            gc.setFill(Color.BLACK);
            gc.fillText("Click the blue circle to win!", 250, 150);
        }
    }

    @Override
    protected void onGameClick(double x, double y) {
        double dx = x - circleX;
        double dy = y - circleY - NAV_BAR_HEIGHT;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= circleRadius) {
            clicked = true;
            gameOver = true;
        }
    }
}
