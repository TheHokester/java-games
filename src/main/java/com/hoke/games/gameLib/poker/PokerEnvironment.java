package com.hoke.games.gameLib.poker;

import com.hoke.games.UIAssets.Button;
import com.hoke.games.UIAssets.Card;
import com.hoke.games.UIAssets.Slider;
import com.hoke.games.engine.AbstractGame;
import com.hoke.games.engine.GameEngine;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;

public class PokerEnvironment {
    private static final int TABLE_CENTRE_X = 640;
    private static final int TABLE_CENTRE_Y = 265;
    private static final int TABLE_RADIUS = 330;
    private static final double TABLE_ARC_START = 150;
    private static final double TABLE_ARC = 240;
    private static final double BORDER_THICKNESS = 4;
    private static final double COMMUNAL_CARDS_Y = 200;
    private static final int CARD_WIDTH = 72;
    private static final int CARD_HEIGHT = 112;
    private static final double CARD_SCALE = 0.6;
    private static final double BUTTON_PANEL_X = 000;
    private static final double BUTTON_PANEL_Y = 200;
    private static final double BUTTON_PANEL_WIDTH = 230;
    private static final double BUTTON_PANEL_HEIGHT = 100;
    private static final double BUTTON_WIDTH = 70;
    private static final double BUTTON_HEIGHT = 45;
    private static boolean callAnyButton = false;
    private static int radioButtonDiameter = 16;
    private static final int CARD_GAP = 6;               // gap from border to border
    private static final int CARD_MARGIN = 4;





    private static final Color TABLE_COLOR = Color.DARKGREEN;
    private static GraphicsContext gc;

    public PokerEnvironment(GraphicsContext gc) {
        this.gc = gc;
        getEngine();
    }

    public static void drawTable() {
        // === Table Arc ===
        gc.setFill(Color.BLACK);
        gc.fillArc(
                TABLE_CENTRE_X - TABLE_RADIUS,
                TABLE_CENTRE_Y - TABLE_RADIUS,
                TABLE_RADIUS * 2,
                TABLE_RADIUS * 2,
                TABLE_ARC_START,
                TABLE_ARC,
                ArcType.CHORD
        );

        gc.setFill(TABLE_COLOR);
        gc.fillArc(
                TABLE_CENTRE_X - TABLE_RADIUS + BORDER_THICKNESS,
                TABLE_CENTRE_Y - TABLE_RADIUS + BORDER_THICKNESS,
                2 * (TABLE_RADIUS - BORDER_THICKNESS),
                2 * (TABLE_RADIUS - BORDER_THICKNESS),
                TABLE_ARC_START,
                TABLE_ARC,
                ArcType.CHORD
        );

        // === Pre-calculate card frame size ===
        double frameWidth = CARD_WIDTH * CARD_SCALE + 2 * CARD_MARGIN;
        double frameHeight = CARD_HEIGHT * CARD_SCALE + 2 * CARD_MARGIN;

        // === Draw Communal Card Outlines ===
        for (int i = 0; i < 5; i++) {
            double frameX = getCommunalCardFrameX(i);
            double frameY = COMMUNAL_CARDS_Y - CARD_MARGIN;

            gc.setStroke(Color.DARKORANGE);
            gc.setLineWidth(2);
            gc.strokeRoundRect(
                    frameX, frameY,
                    frameWidth, frameHeight,
                    32 * CARD_SCALE, 32 * CARD_SCALE
            );
        }

        // === Draw Deck Frame ===
        double deckFrameX = getCommunalCardFrameX(2); // center card
        double deckFrameY = COMMUNAL_CARDS_Y - frameHeight - CARD_GAP;

        gc.strokeRoundRect(
                deckFrameX, deckFrameY,
                frameWidth, frameHeight,
                32 * CARD_SCALE, 32 * CARD_SCALE
        );

        // === Draw Deck Card ===
        Card card = Card.BACK_RED;
        card.x = deckFrameX + CARD_MARGIN;
        card.y = deckFrameY + CARD_MARGIN;
        card.drawCard(gc, CARD_SCALE, CARD_SCALE);
    }

    public static double getCommunalCardFrameX(int i) {
        int frameWidth = (int) (CARD_WIDTH * CARD_SCALE) + 2 * CARD_MARGIN;
        int totalWidth = 5 * frameWidth + 4 * CARD_GAP; // 5 frames, 4 gaps
        int startX = TABLE_CENTRE_X - totalWidth / 2;

        return startX + i * (frameWidth + CARD_GAP);
    }


    public static boolean inBounds(double px, double py, double x, double y, double w, double h) {
        return AbstractGame.inBounds(px, py, x, y, w, h);
    }
    protected GameEngine engine;
    public void getEngine() {
        this.engine = AbstractGame.getEngine();
    }

    public void buttonPanel() {
        Color panelColor = Color.GRAY;
        gc.setFill(panelColor);
        gc.fillRoundRect(BUTTON_PANEL_X,BUTTON_PANEL_Y, BUTTON_PANEL_WIDTH, BUTTON_PANEL_HEIGHT, 32, 32);

        gc.setFill((inBounds(engine.getMouseX(), engine.getMouseY(), BUTTON_PANEL_X+10, BUTTON_PANEL_Y+10,BUTTON_WIDTH, BUTTON_HEIGHT))?Color.PALEGOLDENROD.brighter(): Color.PALEGOLDENROD);
        gc.fillRoundRect(BUTTON_PANEL_X+10, BUTTON_PANEL_Y+10, BUTTON_WIDTH, BUTTON_HEIGHT, 16, 16);
        gc.setFill((inBounds(engine.getMouseX(), engine.getMouseY(), BUTTON_PANEL_X+10+BUTTON_WIDTH, BUTTON_PANEL_Y+10, BUTTON_WIDTH, BUTTON_HEIGHT))? Color.GREEN.brighter() :Color.GREEN);
        gc.fillRoundRect(BUTTON_PANEL_X+10+BUTTON_WIDTH, BUTTON_PANEL_Y+10, BUTTON_WIDTH, BUTTON_HEIGHT, 16, 16);
        gc.setFill((inBounds(engine.getMouseX(), engine.getMouseY(), BUTTON_PANEL_X+10+2*BUTTON_WIDTH, BUTTON_PANEL_Y+10, BUTTON_WIDTH, BUTTON_HEIGHT))?Color.ORANGERED : Color.RED);
        gc.fillRoundRect(BUTTON_PANEL_X+10+2*BUTTON_WIDTH, BUTTON_PANEL_Y+10, BUTTON_WIDTH, BUTTON_HEIGHT, 16, 16);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeOval(BUTTON_PANEL_X + 60, BUTTON_PANEL_Y+ 70, radioButtonDiameter, radioButtonDiameter);
        if(callAnyButton) {
            gc.setFill(Color.GRAY);
            gc.fillOval(BUTTON_PANEL_X+60 + 3, BUTTON_PANEL_Y+70 +3 , radioButtonDiameter-6, radioButtonDiameter-6);
        } else if(inBounds(engine.getMouseX(), engine.getMouseY(), BUTTON_PANEL_X+60, BUTTON_PANEL_Y+70, radioButtonDiameter, radioButtonDiameter)) {
            gc.setFill(Color.LIGHTGRAY);
            gc.fillOval(BUTTON_PANEL_X+60 + 3, BUTTON_PANEL_Y+70 +3 , radioButtonDiameter-6, radioButtonDiameter-6);
        }
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(12));
        gc.fillText("RAISE", BUTTON_PANEL_X+30, BUTTON_PANEL_Y+36);
        gc.fillText("CALL", BUTTON_PANEL_X+30+BUTTON_WIDTH, BUTTON_PANEL_Y+36);
        gc.fillText("FOLD", BUTTON_PANEL_X+30 + 2*BUTTON_WIDTH, BUTTON_PANEL_Y+36);
        betPanel();
    }

    Slider betAmount = new Slider(BUTTON_PANEL_X+42, BUTTON_PANEL_Y+BUTTON_PANEL_HEIGHT+20,
            BUTTON_PANEL_WIDTH-120,"Bet:", true, Bet.MIN_BET, 100,
            true, false,true, 1);
    Button bPButton1 = new Button(BUTTON_PANEL_X+10, BUTTON_PANEL_Y+BUTTON_PANEL_HEIGHT+40, 40,20,"-100",false,Color.LIGHTGRAY, Color.WHITE);
    Button bPButton2 = new Button(BUTTON_PANEL_X+50, BUTTON_PANEL_Y+BUTTON_PANEL_HEIGHT+40, 40,20,"-10",false,Color.LIGHTGRAY, Color.WHITE);
    Button bPButton3 = new Button(BUTTON_PANEL_X+90, BUTTON_PANEL_Y+BUTTON_PANEL_HEIGHT+40, 40,20,"+10",false,Color.LIGHTGRAY, Color.WHITE);
    Button bPButton4 = new Button(BUTTON_PANEL_X+130, BUTTON_PANEL_Y+BUTTON_PANEL_HEIGHT+40, 40,20,"+100",false,Color.LIGHTGRAY, Color.WHITE);
    Button bPButton5 = new Button(BUTTON_PANEL_X+170, BUTTON_PANEL_Y+BUTTON_PANEL_HEIGHT+40, 50,20,"All In",false,Color.LIGHTGRAY, Color.WHITE);
    public void betPanel() {
        Color panelColor = Color.GRAY;
        gc.setFill(panelColor);
        gc.fillRoundRect(BUTTON_PANEL_X, BUTTON_PANEL_Y+ BUTTON_PANEL_HEIGHT, BUTTON_PANEL_WIDTH, BUTTON_PANEL_HEIGHT*0.75, 32, 32);

        //bet slider
        betAmount.updateEndValue(Poker.getPlayers().get(2).getChipBalance());
        betAmount.update();
        betAmount.render(gc);

        bPButton1.update();
        bPButton2.update();
        bPButton3.update();
        bPButton4.update();
        bPButton5.update();
        bPButton1.render(gc);
        bPButton2.render(gc);
        bPButton3.render(gc);
        bPButton4.render(gc);
        bPButton5.render(gc);
    }



}
