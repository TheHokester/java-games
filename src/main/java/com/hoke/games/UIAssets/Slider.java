package com.hoke.games.UIAssets;

import com.hoke.games.engine.InputManager;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Slider extends  uiElement{
    private double x, y, length;
    private double pinRadius = 10;
    private double value = 0.0;

    private double valueStart;
    private double valueEnd;
    private double stepSize;

    private String labelText;
    private boolean showTextbox;
    private boolean showValueInPin;
    private boolean vertical;
    private boolean showMinMaxMarkers;

    private double textboxWidth = 50;
    private String textboxContent = "0";
    private boolean textboxFocused = false;

    private boolean dragging = false;
    private int hoverState = 0;

    private Font font = Font.font("Arial", 14);
    private Color trackColor = Color.LIGHTGRAY;
    private Color pinColor = Color.DARKBLUE;
    private Color hoverColor = Color.LIGHTBLUE;



    public Slider(double x, double y, double length,
                  String label,
                  boolean showTextbox,
                  double valueStart, double valueEnd,
                  boolean showValueInPin,
                  boolean vertical,
                  boolean showMinMaxMarkers,
                  double stepSize) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.labelText = label;
        this.showTextbox = showTextbox;
        this.valueStart = valueStart;
        this.valueEnd = valueEnd;
        this.showValueInPin = showValueInPin;
        this.vertical = vertical;
        this.showMinMaxMarkers = showMinMaxMarkers;
        this.stepSize = stepSize;
        setValueRaw(valueStart);
    }

    public double getValueRaw() {
        return valueStart + (valueEnd - valueStart) * value;
    }

    public void setValueRaw(double raw) {
        setValueRaw(raw, true);
    }
    public void setValueRaw(double raw, boolean updateTextBoxValue) {
        double clamped = Math.max(valueStart, Math.min(valueEnd, raw));
        if (stepSize > 0) {
            clamped = Math.round((clamped - valueStart) / stepSize) * stepSize + valueStart;
        }
        value = (clamped - valueStart) / (valueEnd - valueStart);
        if(updateTextBoxValue){
            textboxContent = String.format("%.2f", getValueRaw());
        }
    }

    public double getNormalizedValue() {
        return value;
    }

    public void updateHover(double mouseX, double mouseY) {
        hoverState = 0;
        double pinX = vertical ? x : x + value * length;
        double pinY = vertical ? y + (1.0 - value) * length : y;

        if (Math.hypot(mouseX - pinX, mouseY - pinY) <= pinRadius) {
            hoverState = 1;
        } else if (vertical
                ? (mouseX >= x - 5 && mouseX <= x + 5 && mouseY >= y && mouseY <= y + length)
                : (mouseY >= y - 5 && mouseY <= y + 5 && mouseX >= x && mouseX <= x + length)) {
            hoverState = 2;
        } else if (showTextbox) {
            double boxX = vertical ? x + 20 : x + length + 15;
            double boxY = y - 10;
            if (mouseX >= boxX && mouseX <= boxX + textboxWidth &&
                    mouseY >= boxY && mouseY <= boxY + 20) {
                hoverState = 3;
            }
        }

    }

    public void handleClick(double mouseX, double mouseY) {

        updateHover(mouseX, mouseY);

        if (hoverState == 1 || hoverState == 2) {
            setValueFromMouse(mouseX, mouseY);
            dragging = (hoverState == 1);
            textboxFocused = false;
        } else if (hoverState == 3) {
            textboxFocused = true;

        } else {
            textboxFocused = false;
        }
    }

    public void handleDrag(double mouseX, double mouseY) {
        if (dragging) {
            setValueFromMouse(mouseX, mouseY);
        }
    }

    public void handleRelease() {
        dragging = false;
    }

    public void handleTextInput(String typed) {
        if (!textboxFocused) return;

        if (typed.equals("\b")) {
            if (!textboxContent.isEmpty())
                textboxContent = textboxContent.substring(0, textboxContent.length() - 1);
        } else if (typed.equals("\n")) {
            try {
                double parsed = Double.parseDouble(textboxContent);
                setValueRaw(parsed, false);
            } catch (NumberFormatException ignored) {}
        } else if ("0123456789.-".contains(typed)) {
            textboxContent += typed;
        }

        try {
            double parsed = Double.parseDouble(textboxContent);
            setValueRaw(parsed, false);
        } catch (NumberFormatException ignored) {}
    }

    public void render(GraphicsContext gc) {
        gc.setFont(font);
        double pinX = vertical ? x : x + value * length;
        double pinY = vertical ? y + (1.0 - value) * length : y;

        // Label
        if (labelText != null && !labelText.isEmpty()) {
            gc.setFill(Color.BLACK);
            double tx = vertical ? x + 40 : x - getTextWidth(labelText, gc) - 10;
            double ty = vertical ? y + length / 2 : y + 5;
            gc.fillText(labelText, tx, ty);
        }

        // Track
        gc.setStroke(trackColor);
        gc.setLineWidth(4);
        if (vertical) {
            gc.strokeLine(x, y, x, y + length);
        } else {
            gc.strokeLine(x, y, x + length, y);
        }

        // Pin
        gc.setFill(hoverState == 1 ? hoverColor : pinColor);
        gc.fillOval(pinX - pinRadius, pinY - pinRadius, pinRadius * 2, pinRadius * 2);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeOval(pinX - pinRadius, pinY - pinRadius, pinRadius * 2, pinRadius * 2);

        // Value inside pin
        if (showValueInPin) {
            gc.setFill(Color.WHITE);
            String val = String.format("%.1f", getValueRaw());
            gc.setFont(new Font("arial", 8));
            gc.fillText(val, pinX - getTextWidth(val, gc) / 2, pinY + 4);
        }

        // Textbox
        if (showTextbox) {
            double boxX = vertical ? x + 20 : x + length + 15;
            double boxY = y - 10;
            gc.setStroke(Color.BLACK);
            gc.strokeRect(boxX, boxY, textboxWidth, 20);
            gc.setFill(Color.WHITE);
            gc.fillRect(boxX, boxY, textboxWidth, 20);

            gc.setFill(Color.BLACK);
            gc.setFont(font);
            gc.fillText(textboxContent, boxX + 5, boxY + 15);
            if (textboxFocused) {
                gc.setStroke(Color.BLUE);
                gc.strokeRect(boxX, boxY, textboxWidth, 20);
            }
        }

        // Min/max markers
        if (showMinMaxMarkers) {
            gc.setFill(Color.GRAY);
            String minText = String.format("%.1f", valueStart);
            String maxText = String.format("%.1f", valueEnd);
            if (vertical) {
                gc.fillText(maxText, x + 10, y + 5);
                gc.fillText(minText, x + 10, y + length);
            } else {
                gc.fillText(minText, x, y + 20);
                gc.fillText(maxText, x + length - getTextWidth(maxText, gc), y + 20);
            }
        }
    }

    private void setValueFromMouse(double mouseX, double mouseY) {
        double ratio = vertical
                ? 1.0 - (mouseY - y) / length
                : (mouseX - x) / length;
        setValueRaw(valueStart + (valueEnd - valueStart) * ratio);
    }



    public void update( ) {
        updateHover(InputManager.mouseX, InputManager.mouseY);

        if(InputManager.mousePressed) {
            handleClick(InputManager.mouseX, InputManager.mouseY);
        }

        if(InputManager.isMouseDown && dragging) {
            handleDrag(InputManager.mouseX, InputManager.mouseY);
        }

        if(InputManager.mouseReleased) {
            handleRelease();
        }
        if(textboxFocused && !InputManager.typedChar.isEmpty()) {
            handleTextInput(InputManager.typedChar);
        }
    }

    public void updateStartValue(double value) {
        this.valueStart = value;
    }
    public void updateEndValue(double value) {
        this.valueEnd = value;
    }
    public void setValue(double value) {
        setValueRaw(value);
    }


}

