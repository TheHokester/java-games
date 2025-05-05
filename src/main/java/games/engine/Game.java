package games.engine;

import javafx.scene.canvas.GraphicsContext;

public interface Game {
    void start();
    void setEngine(GameEngine engine);
    void update();
    void render(GraphicsContext gc);
    void onClick(double x, double y);
    boolean isGameOver();
}
