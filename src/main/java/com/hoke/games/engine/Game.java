package com.hoke.games.engine;

import javafx.scene.canvas.GraphicsContext;

public interface Game {
    void start(GraphicsContext gc);
    void setEngine(GameEngine engine);
    void update();
    void render(GraphicsContext gc);
    void onClick(double x, double y);
    boolean isGameOver();
}
