package com.hoke.games.gameLib.poker;

import com.hoke.games.assets.Card;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class PokerEnvironment {
    private static final int TABLE_CENTRE_X = 640;
    private static final int TABLE_CENTRE_Y = 275;
    private static final int TABLE_RADIUS = 350;
    private static final double TABLE_ARC_START = 150;
    private static final double TABLE_ARC = 240;
    private static final double BORDER_THICKNESS = 4;
    private static final double COMMUNAL_CARDS_Y = 200;
    private static final int CARD_WIDTH = 72;
    private static final int CARD_HEIGHT = 112;
    private static final double CARD_SCALE = 0.6;

    private static final int CARD_GAP = 6;               // gap from border to border
    private static final int CARD_MARGIN = 4;


    private static final Color TABLE_COLOR = Color.DARKGREEN;
    static GraphicsContext gc;
    public PokerEnvironment(GraphicsContext gc) {
        this.gc = gc;
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
        int frameWidth = (int)(CARD_WIDTH * CARD_SCALE) + 2 * CARD_MARGIN;
        int totalWidth = 5 * frameWidth + 4 * CARD_GAP; // 5 frames, 4 gaps
        int startX = TABLE_CENTRE_X - totalWidth / 2;

        return startX + i * (frameWidth + CARD_GAP);
    }

}
