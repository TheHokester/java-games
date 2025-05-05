package com.hoke.games.gameLib.chests;

import com.hoke.games.engine.AbstractGame;
import com.hoke.games.gameLib.chests.Chest;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

import java.util.EnumSet;

public class ChestGame extends AbstractGame {

    int wins = 0;
    int losses = 0;
    int points = 0;
    double score = 0;
    double multiplier = 0;
    double highScore = 0;


    private static final int CHEST_WIDTH = 30;
    private static final int CHEST_HEIGHT = 30;
    private static final int CHEST_COLS = 10;
    private static final int CHEST_ROWS = 3;
    private static final int CHESTS_CORNER_X = 200;
    private static final int CHESTS_CORNER_Y = 250;

    private static final int H_GAP = 20;
    private static final int V_GAP = 60;
    double clickX;
    double clickY;

    private Chest[] chests;


    private int mimicKillerUses = 0;
    public boolean x2Active = false;
    int x2TurnActivated = -1;
    public int turn = 0;

    private GameResult result = GameResult.INPROGRESS;
    private int safeChestsRemaining;
    private int mimicsRemaining;


    @Override
    public void start(GraphicsContext gc) {

        int numChests = CHEST_COLS * CHEST_ROWS;
        int numMimics = 4;
        points = 0;
        score = 0;
        multiplier = 0;
        result = GameResult.INPROGRESS;
        chests = new Chest[30];
        turn = 0;
        x2Active = false;



        for (int row = 0; row < CHEST_ROWS; row++) {
            for (int col = 0; col < CHEST_COLS; col++) {
                double x = col * (CHEST_WIDTH + H_GAP) + CHESTS_CORNER_X;
                double y = row * (CHEST_HEIGHT + V_GAP) + CHESTS_CORNER_Y;
                int index = row * 10 + col;
                chests[index] = new Chest(x , y, Chest.Content.EMPTY, gc);
            }
        }

        // Place MIMIC_KILLER randomly
        int j;
        do {
            j = (int) (Math.random() * numChests);
        } while (chests[j].content != Chest.Content.EMPTY);
        chests[j].content = Chest.Content.MIMIC_KILLER;

        // Place mimics
        for (int i = 0; i < numMimics; i++) {
            do {
                j = (int) (Math.random() * numChests);
            } while (chests[j].content != Chest.Content.EMPTY);
            chests[j].content = Chest.Content.MIMIC;
        }

        // Place x2 reward
        do {
            j = (int) (Math.random() * numChests);
        } while (chests[j].content != Chest.Content.EMPTY);
        chests[j].content = Chest.Content.X2_REWARD;

        initializeGameState(numChests, numMimics);
    }





    protected void renderGame(GraphicsContext gc) {
        double hoverX = engine.getMouseX();
        double hoverY = engine.getMouseY();
        environment(gc);

        int index = 0;
        for(Chest c : chests) {
            boolean isHovered = (
                    hoverX >= c.x && hoverX <= c.x + CHEST_WIDTH &&
                            hoverY >= c.y && hoverY <= c.y + CHEST_HEIGHT
            );
           c.printChest(c.isClicked(clickX,clickY), isHovered);
           if(c.isClicked(clickX,clickY) && !c.searched) {
                chestClicked(c);
           }
           index++;
        }

        clickX = -1;
        clickY = -1;

    }

    protected void onGameClick(double x, double y) {

        clickX = x;
        clickY = y;

        if(x >= 800 && x <= 880  && y >= 300 && y <= 340) {
            start(engine.canvas.getGraphicsContext2D());
        }
    }

    private void chestClicked(Chest chest) {
        if (chest.searched) {
            //System.out.println("You already searched this chest!");
            return;
        }
        if (getGameResult() == GameResult.LOSS) return;

        turn++;
        boolean gameContinues = true;


        switch (chest.content) {
            case EMPTY:
                chest.searched = true;
                points += x2Active ? 10 : 5;
                multiplier += x2Active ? 0.2 : 0.1;
                break;
            case MIMIC:
                gameContinues = handleMimic(chest);
                break;
            case MIMIC_KILLER:
                searchMimicKiller(chest);
                break;
            case X2_REWARD:
                activateX2Reward(chest);
                break;
        }

        updateGameStateAfterMove(chest);
        deactivateX2AfterTurn();
        gameContinues = checkGameState(chest);
            score = points * multiplier;
            if(result != GameResult.INPROGRESS) {
                endGame();
            }

    }



    private void searchMimicKiller(Chest chest) {
        if (mimicKillerUses == 0) {
            mimicKillerUses = x2Active ? 2 : 1;
        }
        chest.searched = true;
        points += x2Active ? 20 : 10;
        multiplier += x2Active ? 0.4 : 0.2;
    }

    private void activateX2Reward(Chest chest) {
        x2Active = true;
        x2TurnActivated = turn;
        chest.searched = true;
        points +=20;
        multiplier += 0.2;
    }

    private boolean handleMimic(Chest chest) {
        if (turn <= 2 || mimicKillerUses > 0 || Math.random() < 0.01) {
            if (mimicKillerUses > 0) mimicKillerUses--;
            chest.content = Chest.Content.KILLED;
            chest.searched = true;
            points += x2Active ? 100 : 50;
            multiplier += x2Active ? 0.6 : 0.3;
            return true;
        }
        chest.searched = true;
        result = GameResult.LOSS;
        return false;
    }

    private void deactivateX2AfterTurn() {
        if (x2Active && turn > x2TurnActivated) {
            x2Active = false;
        }
    }

    private void initializeGameState(int numChests, int numMimics) {
        safeChestsRemaining = numChests - numMimics;
        mimicsRemaining = numMimics;
    }

    private void updateGameStateAfterMove(Chest chest) {
        if (chest.content == Chest.Content.KILLED) {
            mimicsRemaining--;
        } else if (chest.content.isSafe()) {
            safeChestsRemaining--;
        }

    }

    private boolean checkGameState(Chest chest) {
        if (safeChestsRemaining == 0) {
            result = GameResult.WIN;
            return false;
        }
        if (chest.content == Chest.Content.MIMIC && chest.searched) {
            return false;
        }
        return true;
    }

//    public void printChests() {
//        for (ChestContent c : chests) {
//            System.out.print(c.getSymbol() + " ");
//        }
//        System.out.println();
//    }

    public int getNumChests() {
        return chests.length;
    }

    public GameResult getGameResult() {
        return result;
    }

    public void endGame() {
        if(score > highScore) {
            highScore = score;
        }
        if(getGameResult() == GameResult.LOSS) {
            losses++;
        } else if(getGameResult() == GameResult.WIN) {
            wins++;
        }
    }














    private void chestShelf(double x, double y, GraphicsContext gc) {
        Color bodyColor = Color.SADDLEBROWN;
        Color frameColor = bodyColor.darker();
        double shelfHeight = 20;
        double shelfWidth = 550;
        double shelfLegHeight = 30;
        double shelfLegWidth = 20;
        double leg1Pos = 80;
        double leg2Pos = 450;
        LinearGradient shelfLegGradient = new LinearGradient(
                0, 0, 0, 1,  // startX, startY, endX, endY (relative to shape bounds if proportional)
                true,        // proportional
                CycleMethod.NO_CYCLE,  // or REFLECT, REPEAT
                new Stop(0, Color.BLACK),
                new Stop(1, Color.SADDLEBROWN)
        );
        //shelf body
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x, y, shelfWidth, shelfHeight, 4, 4);
        gc.setFill(frameColor);
        gc.fillRoundRect(x+1,y+1, shelfWidth-2, shelfHeight-2, 4, 4);
        gc.setFill(bodyColor);
        gc.fillRoundRect(x+3,y+3, shelfWidth-6, shelfHeight-6, 3, 3);

        //shelf legs
        gc.setFill(shelfLegGradient);
        gc.fillPolygon(
                new double[] {x + leg1Pos, x + leg1Pos+2, x + leg1Pos + shelfLegWidth - 2, x + leg1Pos + shelfLegWidth},
                new double[] {y+shelfHeight, y+shelfHeight +shelfLegHeight, y+shelfHeight +shelfLegHeight,y +shelfHeight},
                4
        );
        gc.fillPolygon(
                new double[] {x + leg2Pos, x + leg2Pos+2, x + leg2Pos + shelfLegWidth - 2, x + leg2Pos + shelfLegWidth},
                new double[] {y+shelfHeight, y+shelfHeight +shelfLegHeight, y+shelfHeight +shelfLegHeight,y +shelfHeight},
                4
        );

    }
    private void environment(GraphicsContext gc) {
        //
        gc.setFill(Color.BURLYWOOD);
        gc.fillPolygon(
                new double[] {0, 120, 120, 0},
                new double[] {100,190,600 ,690},
                4
        );
        gc.fillPolygon(
                new double[] {1280,1180,1180,1280},
                new double[] {100,130,660,690},
                4
        );

        gc.setFill(Color.SADDLEBROWN);
        gc.fillPolygon(
                new double[] {0, 120, 120, 0},
                new double[] {690,600,630,720},
                4
        );
        gc.fillPolygon(
                new double[] {1280,1180,1180,1280},
                new double[] {690,660,690,720},
                4
        );
        gc.setFill(Color.LIGHTGRAY);
        gc.fillPolygon(
                new double[] {0, 120,820,920},
                new double[] {100,190,190,100},
                4
        );
        gc.fillPolygon(
                new double[] {920,820,1180,1280},
                new double[] {100,130, 130, 100},
                4
        );

        //dataBricks
        gc.setFill(Color.CORAL);
        gc.fillRoundRect(800, 300, 80, 40, 8, 8);
        gc.setFill(Color.TRANSPARENT);
        gc.strokeText("Restart Game", 800, 320);

        gc.strokeText(String.format("Score: %.1f", score), 800, 360 );
        gc.strokeText(String.format("Wins: %d", wins), 800, 400 );
        gc.strokeText(String.format("Losses: %d", losses), 800, 420 );
        gc.strokeText(String.format("Points: %d", points), 800, 440 );
        gc.strokeText(String.format("High Score: %.1f", highScore), 800, 460 );
        gc.strokeText(String.format("Multiplier: %.1f", multiplier), 800, 480 );
        gc.strokeText(String.format("Mimic Killer Uses: %d",mimicKillerUses), 800, 500 );
        gc.strokeText(String.format("Is x2 active: %s",String.valueOf(x2Active)), 800, 520 );




        chestShelf(CHESTS_CORNER_X-35, CHESTS_CORNER_Y + CHEST_HEIGHT , gc);//top shelf
        chestShelf(CHESTS_CORNER_X-35, CHESTS_CORNER_Y +CHEST_HEIGHT*2 + V_GAP, gc);//middle shelf
        chestShelf(CHESTS_CORNER_X-35, CHESTS_CORNER_Y + CHEST_HEIGHT*3 +V_GAP*2, gc);//bottom shelf
    }

}