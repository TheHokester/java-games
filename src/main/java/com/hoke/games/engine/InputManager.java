package com.hoke.games.engine;

public class InputManager {
    public static double mouseX, mouseY;
    public static double prevMouseX, prevMouseY;

    public static boolean isMouseDown = false;
    public static boolean mousePressed = false;
    public static boolean mouseReleased = false;

    public static boolean keyPressed = false;
    public static String typedChar = "";

    public static void setMouse(double x, double y) {
        prevMouseX = mouseX;
        prevMouseY = mouseY;
        mouseX = x;
        mouseY = y;
    }

    public static void setMousePressed(boolean pressed) {
        if (pressed && !isMouseDown) {
            mousePressed = true;
        }
        isMouseDown = pressed;
    }

    public static void setMouseReleased(boolean released) {
        if (released && isMouseDown) {
            mouseReleased = true;
        }
        if (released) {
            isMouseDown = false;
        }
    }

    public static void setTypedChar(String character) {
        typedChar = character;
    }

    public static void reset() {
        mousePressed = false;
        mouseReleased = false;
        typedChar = "";
        prevMouseX = mouseX;
        prevMouseY = mouseY;
    }

    public static boolean mouseMoved() {
        return mouseX != prevMouseX || mouseY != prevMouseY;
    }
}
