package com.hoke.games.UIAssets;

import com.hoke.games.engine.InputManager;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.util.*;



public class RadioButtons extends uiElement {
    private List<Circle> buttons;
    private List<String> labels;

    private Set<Integer> selectedIndices;
    private int hoveredIndex;

    private Color baseColor;
    private Color hoveredColor;
    private boolean textOnLeft;
    private boolean allowMultiple;

    private int diameter;
    private int startX, startY, xOffset, yOffset;

    public RadioButtons() {
    }

    public RadioButtons(int startX, int startY, int diameter, int numButtons, int xOffset, int yOffset, Color color, Color hoverColor, List<String> labels, boolean textOnLeft, boolean allowMultipleSelection) {
        this.startX = startX;
        this.startY = startY;
        this.diameter = diameter;
        this.hoveredColor = hoverColor;
        this.baseColor = color;
        this.labels = labels;
        this.textOnLeft = textOnLeft;
        this.allowMultiple = allowMultipleSelection;

        this.buttons = new ArrayList<>();
        this.selectedIndices = new HashSet<>();

        for (int i = 0; i < numButtons; i++) {
            int x = startX + i * xOffset;
            int y = startY + i * yOffset;
            buttons.add(new Circle(x, y, diameter / 2.0));
        }
    }

    public void updateHover(double mouseX, double mouseY) {
        hoveredIndex = -1;
        for (int i = 0; i < buttons.size(); i++) {
            Circle button = buttons.get(i);
            if (button.contains(mouseX, mouseY)) {
                hoveredIndex = i;
                break;
            }
        }
    }

    public void handleClick(double mouseX, double mouseY) {
        for (int i = 0; i < buttons.size(); i++) {
            Circle button = buttons.get(i);
            if (button.contains(mouseX, mouseY)) {
                if (allowMultiple) {
                    if (selectedIndices.contains(i)) {
                        selectedIndices.remove(i);
                    } else {
                        selectedIndices.add(i);
                    }
                } else {
                    if (selectedIndices.contains(i)) {
                        selectedIndices.clear();
                    } else {
                        selectedIndices.clear();
                        selectedIndices.add(i);
                    }
                }
                break;
            }
        }
    }

    public void render(GraphicsContext gc) {

        for (int i = 0; i < buttons.size(); i++) {
            Circle button = buttons.get(i);
            double x = button.getCenterX(), y = button.getCenterY(), r = button.getRadius();

            boolean isSelected = selectedIndices.contains(i);
            boolean isHovered = hoveredIndex == i;

            if (allowMultiple) {
                //Checkbox visuals
                double size = r * 2;
                double boxX = x - r;
                double boxY = y - r;

                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeRoundRect(boxX, boxY, size, size, 2, 2);

                if (isSelected) {
                    gc.setFill(baseColor);
                    gc.fillRoundRect(boxX + 1, boxY + 1, size - 2, size - 2, 2, 2);

                    gc.setFill(Color.BLACK);
                    gc.fillRect(boxX + size * 0.25, boxY + size * 0.25, size * 0.5, size * 0.5);

                } else if (isHovered) {
                    gc.setFill(hoveredColor);
                    gc.fillRoundRect(boxX + 1, boxY + 1, size - 2, size - 2, 2, 2);
                }
            } else {
                //radiobutton visuals
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                gc.strokeOval(x - r, y - r, r * 2, r * 2);
                if (isSelected) {
                    gc.setFill(baseColor);
                    gc.fillOval(x - r, y - r, r * 2, r * 2);
                    gc.setFill(Color.BLACK);
                    gc.fillOval(x - r / 3, y - r / 3, (2 * r) / 3, (2 * r) / 3);
                } else if (isHovered) {
                    gc.setFill(hoveredColor);
                    gc.fillOval(x - r, y - r, r * 2, r * 2);
                }
            }
            // draw label
            if (labels != null && i < labels.size()) {
                String label = labels.get(i);
                gc.setFill(Color.BLACK);
                gc.setFont(new Font("Arial", 14));
                double textWidth = getTextWidth(label, gc);
                double textX = textOnLeft ? x - r - 10 - textWidth : x + r + 10;
                double textY = y + 5;

                gc.fillText(label, textX, textY);
            }
        }
    }
    // --- Accessors for selection ---

    public Set<Integer> getSelectedIndices() {
        return new HashSet<>(selectedIndices);// returns a copy
    }

    public List<String> getSelectedLabels() {
        List<String> selected = new ArrayList<>();
        for( int index : selectedIndices ) {
            if(index >=0 && index < labels.size()) {
                selected.add(labels.get(index));
            }
        }
        return selected;
    }
    public int getSelectedIndex() {
        if(!allowMultiple && !selectedIndices.isEmpty()) {
            return selectedIndices.iterator().next();
        }
        return -1;
    }
    public String getSelectedLabel() {
        int index  = getSelectedIndex();
        if(index >= 0 && index < labels.size()) {
            return labels.get(index);
        }
        return null;
    }

    public void update() {
        updateHover(InputManager.mouseX, InputManager.mouseY);

        if(InputManager.mousePressed) {
            handleClick(InputManager.mouseX, InputManager.mouseY);
        }
    }
}


