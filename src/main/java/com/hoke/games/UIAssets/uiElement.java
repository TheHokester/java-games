package com.hoke.games.UIAssets;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Text;

public abstract class uiElement {





    public double getTextWidth(String text, GraphicsContext gc) {
        Text t = new Text(text);
        t.setFont(gc.getFont());
        return t.getLayoutBounds().getWidth();
    }
    public double getTextHeight(String text, GraphicsContext gc) {
        Text t = new Text(text);
        t.setFont(gc.getFont());
        return t.getLayoutBounds().getHeight();
    }

    public abstract void update();

    public abstract void render(GraphicsContext gc);

    public abstract void updateHover(double mouseX, double mouseY);

    public abstract void handleClick(double mouseX, double mouseY);
}
