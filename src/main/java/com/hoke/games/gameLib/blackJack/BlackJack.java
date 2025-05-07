//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hoke.games.gameLib.blackJack;

import com.hoke.games.assets.Card;
import com.hoke.games.engine.AbstractGame;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class BlackJack extends AbstractGame {
    private static final int BUTTON_X = 250;
    private static final int BUTTON_Y = 500;
    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_ARC = 20;
    private static final int TABLE_X = 200;
    private static final int TABLE_Y = 150;
    private static final int TABLE_WIDTH = 600;
    private static final int TABLE_HEIGHT = 300;
    private static final int TABLE_ARC = 40;
    private static final int SIDE_TABLE_WIDTH = 150;
    private static final int SIDE_TABLE_CENTRE_X = TABLE_X+TABLE_WIDTH + SIDE_TABLE_WIDTH/2;
    private static final int SIDE_TABLE_CENTRE_Y = TABLE_Y+TABLE_HEIGHT/2;
    private static boolean initialDraw = false;
    private static boolean drawActive = false;
    public static boolean dealerDrawActive = false;
    private double mouseX = (double)0.0F;
    private double mouseY = (double)0.0F;
    double betAmount = (double)100.0F;
    List<Card> deck = new ArrayList();
    BJPlayer player;
    BJDealer dealer;
    AbstractGame.GameResult result;
    private long animationStartTime;
    private long STAGGER_NS;
    private final long DRAW_DURATION_NS;
    private final long FLIP_DURATION_NS;
    private long dealerCardDrawStartTime;
    private int dealerDrawIndex;

    public BlackJack() {
        this.result = GameResult.INPROGRESS;
        this.animationStartTime = -1L;
        this.STAGGER_NS = 50000000L;
        this.DRAW_DURATION_NS = 500000000L;
        this.FLIP_DURATION_NS = 300000000L;
        this.dealerCardDrawStartTime = -1L;
        this.dealerDrawIndex = 2;
    }

    public void start(GraphicsContext gc) {
        initialDraw = true;
        drawActive = false;
        dealerDrawActive = false;
        this.dealerDrawIndex = 2;
        this.deck.clear();

        for(Card card : Card.values()) {
            if (card.suit != null && card.rank != null) {
                this.deck.add(card);
            }
        }

        this.player = new BJPlayer(this.deck);
        this.dealer = new BJDealer(this.deck);
        this.result = GameResult.INPROGRESS;
    }

    protected void onGameClick(double clickX, double clickY) {
        // RESET button
        if (this.inBounds(clickX, clickY, BUTTON_X + 2 * BUTTON_WIDTH, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
            this.start(this.engine.canvas.getGraphicsContext2D());
        }

        if (this.result == GameResult.INPROGRESS) {
            // HIT button
            if (this.inBounds(clickX, clickY, BUTTON_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                this.player.hit(this.deck);
                drawActive = true;
                if (this.player.handValue() > 21) {
                    this.result = GameResult.LOSS;
                }
            }
            // STAND button
            else if (this.inBounds(clickX, clickY, BUTTON_X + BUTTON_WIDTH, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                this.player.stand();

                while (!this.dealer.isStood()) {
                    this.dealer.dealerHand(this.deck);
                }

                dealerDrawActive = true;
                int playerValue = this.player.handValue();
                int dealerValue = this.dealer.handValue();
                if (dealerValue <= 21 && playerValue <= dealerValue) {
                    if (playerValue < dealerValue) {
                        this.result = GameResult.LOSS;
                    } else {
                        this.result = GameResult.LOSS;
                    }
                } else {
                    this.result = GameResult.WIN;
                }
            }

        }
    }

    protected void renderGame(GraphicsContext gc) {
        this.mouseX = this.engine.getMouseX();
        this.mouseY = this.engine.getMouseY();
        this.drawButtons(gc);
        this.drawTable(gc);
        if (initialDraw) {
            this.initialCardDeal(gc, this.player.hand, this.dealer.hand);
        } else if (dealerDrawActive) {
            this.dealerCardDrawAnimation(gc);
            this.drawPlayerHand(gc);
        } else if (drawActive) {
            this.shiftAndDrawCard(gc, this.player.hand, this.getPlayerCardY());
            this.drawDealerHand(gc);
        } else {
            this.drawPlayerHand(gc);
            this.drawDealerHand(gc);
        }

        gc.setFill(Color.BLACK);
        gc.setFont(new Font((double)18.0F));
        if (this.result != GameResult.INPROGRESS) {
            int var10001 = this.player.handValue();
            gc.fillText("Player: " + var10001, (double)50.0F, (double)100.0F);
            var10001 = this.dealer.handValue();
            gc.fillText("Dealer: " + var10001, (double)50.0F, (double)130.0F);
            gc.setFill(this.result == GameResult.WIN ? Color.GREEN : Color.RED);
            gc.fillText("Result: " + this.result.name(), (double)50.0F, (double)160.0F);
        }

    }

    private void drawButtons(GraphicsContext gc) {
        int spacing = 101; // Button width + 1px border in original layout

        // HIT Button
        boolean hoverHit = inBounds(mouseX, mouseY, BUTTON_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        gc.setFill(hoverHit ? Color.DARKGREEN : Color.GREEN);
        gc.fillRoundRect(BUTTON_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_ARC, BUTTON_ARC);

        // STAND Button
        double standX = BUTTON_X + spacing;
        boolean hoverStand = inBounds(mouseX, mouseY, standX, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        gc.setFill(hoverStand ? Color.DARKRED : Color.RED);
        gc.fillRoundRect(standX, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_ARC, BUTTON_ARC);

        // RESET Button
        double resetX = BUTTON_X + 2 * spacing;
        boolean hoverReset = inBounds(mouseX, mouseY, resetX, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
        gc.setFill(hoverReset ? Color.DARKGREY.darker() : Color.DARKGREY);
        gc.fillRoundRect(resetX, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_ARC, BUTTON_ARC);

        // Dividers
        gc.setFill(Color.BLACK);
        gc.fillRect(BUTTON_X + spacing - 1, BUTTON_Y, 1, BUTTON_HEIGHT);
        gc.fillRect(BUTTON_X + 2 * spacing - 1, BUTTON_Y, 1, BUTTON_HEIGHT);

        // Labels
        gc.setFont(new Font(18));
        gc.setFill(Color.BLACK);
        gc.fillText("HIT", BUTTON_X + 30, BUTTON_Y + 30);
        gc.setFill(Color.WHITE);
        gc.fillText("STAND", (int)(standX + 14), BUTTON_Y + 30);
        gc.fillText("RESET", (int)(resetX + 14), BUTTON_Y + 30);
    }


    private void drawTable(GraphicsContext gc) {
        // Main table border (black outline)
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(TABLE_X - 4, TABLE_Y - 4, TABLE_WIDTH + 8, TABLE_HEIGHT + 8, TABLE_ARC + 4, TABLE_ARC + 4);

        // Main table surface (green center)
        gc.setFill(Color.DARKGREEN);
        gc.fillRoundRect(TABLE_X, TABLE_Y, TABLE_WIDTH, TABLE_HEIGHT, TABLE_ARC, TABLE_ARC);

        // Side table border (black outline)
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(SIDE_TABLE_CENTRE_X - (SIDE_TABLE_WIDTH / 2) - 4, TABLE_Y - 4, SIDE_TABLE_WIDTH + 8, TABLE_HEIGHT + 8, TABLE_ARC + 4, TABLE_ARC + 4);

        // Side table surface (green center)
        gc.setFill(Color.DARKGREEN);
        gc.fillRoundRect(
                SIDE_TABLE_CENTRE_X - (SIDE_TABLE_WIDTH / 2), TABLE_Y, SIDE_TABLE_WIDTH, TABLE_HEIGHT, TABLE_ARC, TABLE_ARC);

        // Labels
        gc.setFill(Color.WHITE);
        gc.setFont(new Font(16));
        gc.fillText("PLAYER", TABLE_X + 30, TABLE_Y + TABLE_HEIGHT + 20);
        gc.fillText("DEALER", TABLE_X + 30, TABLE_Y + 40);
    }


    private void drawPlayerHand(GraphicsContext gc) {
        double height = this.deck.get(0).getImage().getHeight();
        double scale = 0.75;
        double cardSpacing = 80.0 * scale;

        // Center horizontally in the main table area
        double playerX = TABLE_X + (TABLE_WIDTH / 2.0) - (cardSpacing * this.player.hand.size() / 2.0);

        // Position above bottom of table, with padding
        double playerY = TABLE_Y + TABLE_HEIGHT - (scale * height) - 30.0;

        int i = 0;
        for (Card card : this.player.hand) {
            card.x = playerX + i * cardSpacing;
            card.y = playerY;
            card.drawCard(gc, scale, scale);
            i++;
        }
    }
    private void drawDealerHand(GraphicsContext gc) {
        double height = this.deck.get(0).getImage().getHeight();
        double scale = 0.75;
        double cardSpacing = 80.0 * scale;

        // Center horizontally in the main table area
        double dealerX = TABLE_X + (TABLE_WIDTH / 2.0) - (cardSpacing * this.dealer.hand.size() / 2.0);

        // Dealer cards are drawn near the top of the table
        double dealerY = TABLE_Y + 30.0;

        int j = 0;
        for (Card card : this.dealer.hand) {
            card.x = dealerX + j * cardSpacing;
            card.y = dealerY;

            if (j == 1 && this.dealer.hand.size() == 2 && this.result == GameResult.INPROGRESS) {
                card.drawBack(gc, scale, scale, "RED");
            } else {
                card.drawCard(gc, scale, scale);
            }

            j++;
        }
    }

    private boolean inBounds(double px, double py, double x, double y, double w, double h) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }

    private void initialCardDeal(GraphicsContext gc, List<Card> playerHand, List<Card> dealerHand) {
        if (this.animationStartTime < 0L) {
            this.animationStartTime = System.nanoTime();
        }

        long time = System.nanoTime() - this.animationStartTime;
        int totalCards = this.player.hand.size() + this.dealer.hand.size();

        for(int i = 0; i < totalCards; ++i) {
            Card card = i < 2 ? (Card)this.player.hand.get(i) : (Card)this.dealer.hand.get(i - 2);
            long drawStartTime = (long)i * this.STAGGER_NS;
            if (time >= drawStartTime) {
                long elapsed = time - drawStartTime;
                double xTo;
                double yTo;
                if (i < 2) {
                    xTo = this.getCardX(i, this.player.hand.size());
                    yTo = this.getPlayerCardY();
                } else {
                    xTo = this.getCardX(i - 2, this.dealer.hand.size());
                    yTo = this.getDealerCardY();
                }

                if (elapsed < 500000000L) {
                    card.cardDrawAnimation((double)875.0F, (double)300.0F, xTo, yTo, gc, (double)0.75F, (double)0.75F);
                } else if (elapsed < 800000000L) {
                    card.x = xTo;
                    card.y = yTo;
                    if (i < 3) {
                        card.cardFlipAnimation(gc, (double)0.75F, (double)0.75F);
                    } else {
                        card.drawBack(gc, (double)0.75F, (double)0.75F, "RED");
                    }
                } else {
                    initialDraw = false;
                    this.animationStartTime = -1L;
                }
            }
        }

    }

    private void shiftAndDrawCard(GraphicsContext gc, List<Card> hand, double yTo) {
        if (this.animationStartTime < 0L) {
            this.animationStartTime = System.nanoTime();
        }

        long time = System.nanoTime();

        for(int i = 0; i < hand.size(); ++i) {
            Card card = (Card)hand.get(i);
            long elapsed = time - this.animationStartTime;
            double xTo = this.getCardX(i, this.player.hand.size());
            if (elapsed <= 500000000L) {
                if (i == hand.size() - 1) {
                    card.cardDrawAnimation((double)875.0F, (double)300.0F, xTo, yTo, gc, (double)0.75F, (double)0.75F);
                } else {
                    card.cardDrawAnimation(this.getCardX(i, this.player.hand.size() - 1), card.y, xTo, yTo, gc, (double)0.75F, (double)0.75F);
                }
            } else if (elapsed <= 810000000L) {
                if (i == hand.size() - 1) {
                    card.cardFlipAnimation(gc, (double)0.75F, (double)0.75F);
                } else {
                    card.x = xTo;
                    card.y = yTo;
                    card.drawCard(gc, (double)0.75F, (double)0.75F);
                }
            } else {
                card.x = xTo;
                card.y = yTo;
                card.drawCard(gc, (double)0.75F, (double)0.75F);
                this.animationStartTime = -1L;
                drawActive = false;
            }
        }

    }

    private void dealerCardDrawAnimation(GraphicsContext gc) {
        if (this.dealerCardDrawStartTime < 0L) {
            this.dealerCardDrawStartTime = System.nanoTime();
        }

        long elapsed = System.nanoTime() - this.dealerCardDrawStartTime;
        if (elapsed < 300000000L) {
            ((Card)this.dealer.hand.get(1)).cardFlipAnimation(gc, (double)0.75F, (double)0.75F);
            ((Card)this.dealer.hand.get(0)).drawCard(gc, (double)0.75F, (double)0.75F);
        } else if (this.dealerDrawIndex < this.dealer.hand.size()) {
            long cardStartTime = 300000000L + (long)(this.dealerDrawIndex - 2) * 800000000L;
            long cardEndTime = cardStartTime + 500000000L + 300000000L;
            if (elapsed >= cardStartTime) {
                this.shiftAndDrawCard(gc, this.dealer.hand.subList(0, this.dealerDrawIndex + 1), this.getDealerCardY());
            }

            if (elapsed >= cardEndTime) {
                ++this.dealerDrawIndex;
                this.animationStartTime = -1L;
            }

        } else {
            dealerDrawActive = false;
            this.dealerCardDrawStartTime = -1L;
            this.dealerDrawIndex = 2;
        }
    }

    private double getCardX(int i, int handSize) {
        double cardSpacing = (double)60.0F;
        double dealerX = (double)500.0F - cardSpacing * (double)handSize / (double)2.0F;
        return dealerX + cardSpacing * (double)i;
    }

    private double getPlayerCardY() {
        double dealerY = (double)420.0F - (double)0.75F * ((Card)this.player.hand.get(0)).getImage().getHeight();
        return dealerY;
    }

    private double getDealerCardY() {
        double dealerY = (double)180.0F;
        return dealerY;
    }
}