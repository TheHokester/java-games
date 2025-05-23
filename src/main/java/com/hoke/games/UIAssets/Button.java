package com.hoke.games.UIAssets;

import com.hoke.games.engine.InputManager;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class Button extends uiElement {
    private double x,y,width,height;
    private String label;

    private boolean isToggleButton;
    private boolean isToggled;
    private boolean isHovering;

    private double cornerRadiusMultiplier = 0.15;

    private Font font = Font.font("Arial", 14);
    private Color buttonColor, hoverColor;

    public Button(double x, double y, double width, double height,
                  String label,
                  boolean isToggleButton,
                  Color buttonColor,
                  Color hoverColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        this.isToggleButton = isToggleButton;
        this.buttonColor = buttonColor;
        this.hoverColor = hoverColor;
    }

    public void updateHover(double mouseX, double mouseY) {
        Rectangle2D button = new Rectangle2D(mouseX, mouseY, width, height);
        isHovering = false;
        if(button.contains(mouseX, mouseY)) {
            isHovering = true;
        }
    }
    public void handleClick(double mouseX, double mouseY) {
        updateHover(mouseX, mouseY);
        if(isHovering) {
            //do something
        }
    }

    public void render(GraphicsContext gc) {
        gc.setFont(font);
        double radius = Math.hypot(width, height) * cornerRadiusMultiplier;

        gc.setFill(isHovering ? hoverColor : buttonColor);
        gc.fillRoundRect(x, y, width, height, radius, radius);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRoundRect(x, y, width, height, radius, radius);

        double textX = x + (width - getTextWidth(label,gc))/2;
        double textY = y + (height - getTextHeight(label,gc))/2;
        gc.setFill(Color.BLACK);
        gc.fillText(label, textX, textY);

        if(isToggleButton) {
            double circleRadius = Math.min(width, height) * 0.2;
            double circleX = x+width - circleRadius;
            double circleY = y+height - circleRadius;

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.5);
            gc.strokeOval(circleX, circleY, circleRadius*2, circleRadius*2);

            if(isToggled) {
                Paint toggleOnColor = Color.GREEN;
                gc.setFill(toggleOnColor);
                gc.fillOval(circleX+3, circleY+3,(circleRadius - 3) * 2, (circleRadius - 3) * 2);
            }
        }
    }

    public void update() {
        updateHover(InputManager.mouseX, InputManager.mouseY);
        if(InputManager.mousePressed) {
            handleClick(InputManager.mouseX, InputManager.mouseY);
        }
    }
    public boolean isToggled() {
        return isToggled;
    }
    public void setToggled(boolean toggled) {
        isToggled = toggled;
    }
    public String getLabel() {
        return label;
    }



}
