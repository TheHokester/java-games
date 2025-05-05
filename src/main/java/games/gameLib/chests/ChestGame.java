package games.gameLib.chests;

import games.engine.AbstractGame;
import games.gameLib.chests.*;
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




    private AnimationInfo[] animations = new AnimationInfo[30];


    private ChestContent[] chests = new ChestContent[CHEST_ROWS * CHEST_COLS];
    private boolean[] searched = new boolean[CHEST_ROWS * CHEST_COLS];
    private int mimicKillerUses = 0;
    public boolean x2Active = false;
    int x2TurnActivated = -1;
    public int turn = 0;

    private GameResult result = GameResult.INPROGRESS;
    private int safeChestsRemaining;
    private int mimicsRemaining;


    @Override
    public void start() {

        int numChests = CHEST_COLS * CHEST_ROWS;
        int numMimics = 4;
        points = 0;
        score = 0;
        multiplier = 0;
        result = GameResult.INPROGRESS;
        chests = new ChestContent[numChests];
        turn = 0;
        x2Active = false;



        for (int i = 0; i < numChests; i++) {
            chests[i] = ChestContent.EMPTY;
            searched[i] = false;
            animations[i] = new AnimationInfo();
        }

        // Place MIMIC_KILLER randomly
        int j;
        do {
            j = (int) (Math.random() * numChests);
        } while (chests[j] != ChestContent.EMPTY);
        chests[j] = ChestContent.MIMIC_KILLER;

        // Place mimics
        for (int i = 0; i < numMimics; i++) {
            do {
                j = (int) (Math.random() * numChests);
            } while (chests[j] != ChestContent.EMPTY);
            chests[j] = ChestContent.MIMIC;
        }

        // Place x2 reward
        do {
            j = (int) (Math.random() * numChests);
        } while (chests[j] != ChestContent.EMPTY);
        chests[j] = ChestContent.X2_REWARD;

        initializeGameState(numChests, numMimics);
    }


    public enum ChestContent {
        EMPTY('.'),
        MIMIC('!'),
        X2_REWARD('2'),
        MIMIC_KILLER('?'),
        KILLED('x');

        private final char symbol;

        ChestContent(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }
    }

    private static final EnumSet<ChestContent> SAFE_TYPE =
            EnumSet.of(ChestContent.EMPTY, ChestContent.MIMIC_KILLER, ChestContent.X2_REWARD);

    int chestTokenAngle = 0;
    protected void renderGame(GraphicsContext gc) {
        double hoverX = engine.getMouseX();
        double hoverY = engine.getMouseY();
        chestTokenAngle +=1;
        chestToken(230, 600,gc,Color.DARKGRAY, chestTokenAngle);
        environment(gc);


        for (int row = 0; row < CHEST_ROWS; row++) {
            for (int col = 0; col < CHEST_COLS; col++) {
                double x = col * (CHEST_WIDTH + H_GAP) + CHESTS_CORNER_X;
                double y = row * (CHEST_HEIGHT + V_GAP) + CHESTS_CORNER_Y;
                int index = row * 10 + col;
                boolean isHovered = (
                        hoverX >= x && hoverX <= x + CHEST_WIDTH &&
                                hoverY >= y && hoverY <= y + CHEST_HEIGHT
                );
                boolean isClicked = (
                        clickX >= x && clickX <= x + CHEST_WIDTH &&
                                clickY >= y && clickY <= y + CHEST_HEIGHT
                );
                if (isClicked && !searched[index]) {
                    chestClicked(index);
                    animations[index].frame =0;
                    openChestAnimation(x,y,gc,index,Color.SADDLEBROWN);
                } else if (isHovered && !searched[index]&&(animations[index].chestState.equals("hovered"))) {
                    chestHover(x, y, index, gc);
                } else if(isHovered && !searched[index]) {
                    hoverAnimation(x,y,gc,index,Color.SADDLEBROWN);
                } else if(!searched[index] && !isHovered &&
                        (animations[index].chestState.equals("hovered") || animations[index].chestState.equals("hovering") || animations[index].chestState.equals("unhovering"))) {
                    unHoverAnimation(x,y,gc,index,Color.SADDLEBROWN);
                } else if (!searched[index]) {
                    chestGraphic(x, y, index, gc);
                } else if(searched[index] && animations[index].chestState.equals("opened")) {
                    openedChest(x, y, index, gc);
                } else if(animations[index].chestState.equals("opening")) {
                    openChestAnimation(x,y,gc,index,Color.SADDLEBROWN);
                }
            }
        }
//        mimicSprite(600,600,gc,true);
        clickX = -1;
        clickY = -1;

    }

    protected void onGameClick(double x, double y) {

        clickX = x;
        clickY = y;

        if(x >= 800 && x <= 880  && y >= 300 && y <= 340) {
            start();
        }
    }

    private void chestClicked(int chestIndex) {
        if (isChestSearched(chestIndex)) {
            //System.out.println("You already searched this chest!");
            return;
        }
        if (getGameResult() == GameResult.LOSS) return;

        turn++;
        boolean gameContinues = true;
        ChestContent currentContent = chests[chestIndex];

        switch (chests[chestIndex]) {
            case EMPTY:
                searched[chestIndex] = true;
                points += x2Active ? 10 : 5;
                multiplier += x2Active ? 0.2 : 0.1;
                break;
            case MIMIC:
                gameContinues = handleMimic(chestIndex);
                break;
            case MIMIC_KILLER:
                searchMimicKiller(chestIndex);
                break;
            case X2_REWARD:
                activateX2Reward(chestIndex);
                break;
        }

        updateGameStateAfterMove(currentContent);
        deactivateX2AfterTurn();
        gameContinues = checkGameState(currentContent, chestIndex);
            score = points * multiplier;
            if(result != GameResult.INPROGRESS) {
                endGame();
            }

    }

    public boolean isChestSearched(int loc) {
        return searched[loc];
    }

    private void searchMimicKiller(int loc) {
        if (mimicKillerUses == 0) {
            mimicKillerUses = x2Active ? 2 : 1;
        }
        searched[loc] = true;
        points += x2Active ? 20 : 10;
        multiplier += x2Active ? 0.4 : 0.2;
    }

    private void activateX2Reward(int loc) {
        x2Active = true;
        x2TurnActivated = turn;
        searched[loc] = true;
        points +=20;
        multiplier += 0.2;
    }

    private boolean handleMimic(int loc) {
        if (turn <= 2 || mimicKillerUses > 0 || Math.random() < 0.01) {
            if (mimicKillerUses > 0) mimicKillerUses--;
            chests[loc] = ChestContent.KILLED;
            searched[loc] = true;
            points += x2Active ? 100 : 50;
            multiplier += x2Active ? 0.6 : 0.3;
            return true;
        }
        searched[loc] = true;
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

    private void updateGameStateAfterMove(ChestContent originalContent) {
        if (originalContent == ChestContent.MIMIC) {
            mimicsRemaining--;
        } else if (SAFE_TYPE.contains(originalContent)) {
            safeChestsRemaining--;
        }
    }

    private boolean checkGameState(ChestContent originalContent, int loc) {
        if (safeChestsRemaining == 0) {
            result = GameResult.WIN;
            return false;
        }
        if (originalContent == ChestContent.MIMIC && searched[loc]) {
            return false;
        }
        return true;
    }

    public void printChests() {
        for (ChestContent c : chests) {
            System.out.print(c.getSymbol() + " ");
        }
        System.out.println();
    }

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



    private void chestGraphic(double x, double y, int index, GraphicsContext gc) {
        Color bodyColor = (chests[index] == ChestContent.MIMIC_KILLER) ? Color.GOLD : Color.SADDLEBROWN;
        Color frameColor = bodyColor.darker();

        // Shadow/background
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x, y, 30, 30, 4, 4); // Slight rounding

        // Lid
        gc.setFill(bodyColor);
        gc.fillRoundRect(x + 1, y + 1, 28, 7, 3, 3);

        gc.setFill(bodyColor.brighter());
        gc.fillRect(x + 2, y + 2, 26, 2); // Highlight on lid

        // Lid framing
        gc.setFill(frameColor);
        gc.fillRect(x + 1, y + 1, 2, 7);
        gc.fillRect(x + 27, y + 1, 2, 7);
        gc.fillRect(x + 1, y + 7, 28, 1);

        // Body
        gc.setFill(bodyColor);
        gc.fillRoundRect(x + 1, y + 10, 28, 19, 3, 3);

        // Body framing
        gc.setFill(frameColor);
        gc.fillRect(x + 1, y + 10, 2, 19);
        gc.fillRect(x + 27, y + 10, 2, 19);
        gc.fillRect(x + 1, y + 28, 28, 1);

        // 3 planks = 2 horizontal lines
        gc.setStroke(frameColor);
        gc.setLineWidth(1);
        gc.strokeLine(x + 3, y + 16, x + 27, y + 16);
        gc.strokeLine(x + 3, y + 22, x + 27, y + 22);

        chestLock(x,y,gc);

        // Final chest outline
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRoundRect(x, y, 30, 30, 4, 4);
    }//fully closed(rounded) chest
    private void chestHover(double x, double y, int index, GraphicsContext gc) {
        Color bodyColor = (chests[index] == ChestContent.MIMIC_KILLER) ? Color.GOLD : Color.SADDLEBROWN;
        Color frameColor = bodyColor.darker();

        chestBottom(x,y,gc,bodyColor);
        //lid
        chestLid(x,y,gc,bodyColor,15, false);
        // 🔐 Lock (still visible for consistency, optional)
        chestLock(x,y,gc);


    }//hovered chest


    private void mimicSprite(double x, double y, GraphicsContext gc, boolean isAlive) {

        Color bodyColor = Color.SADDLEBROWN;
        Color frameColor = bodyColor.darker();

        chestBottom(x,y,gc,bodyColor);

        //lid
        gc.setFill(Color.BLACK);
        gc.fillPolygon(
                new double[] {x+1, x+3, x+27, x+29},
                new double[] {y-11, y-17, y-17, y-11},
                4
        );
        gc.fillPolygon(
                new double[] {x+1, x+2, x+28, x+29},
                new double[] {y-11, y+6, y+6, y-11},
                4
        );
        gc.fillPolygon(
                new double[] {x, x+2, x+28, x+29},
                new double[] {y+9, y+6, y+6, y+9},
                4
        );
        gc.setFill(frameColor);
        gc.fillPolygon(
                new double[] {x+2, x+4, x+26, x+28},
                new double[] {y-12, y-16, y-16, y-12},
                4
        );
        gc.setFill(Color.RED.darker().darker());
        gc.fillPolygon(
                new double[] {x+2, x+3, x+27, x+28},
                new double[] {y-10, y+5, y+5, y-10},
                4
        );
        gc.fillPolygon(
                new double[] {x+1,x+3, x+26, x+28},
                new double[] {y+8, y+7, y+7, y+8},
                4
        );
        gc.setFill(bodyColor);
        gc.fillPolygon(
                new double[] {x+4, x+6, x+24, x+26},
                new double[] {y-13, y-15, y-15, y-13},
                4
        );
        gc.setFill(bodyColor.brighter());
        gc.fillPolygon(
                new double[] {x+5, x+6, x+24, x+25},
                new double[] {y-14, y-15, y-15, y-14},
                4
        );



        // 🔐 Lock (still visible for consistency, optional)
        chestLock(x,y,gc);                  // keyhole

        //rear hinges
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x + 4, y + 4, 4, 3);
        gc.fillRect(x + 22, y + 4, 4, 3);

        //teeth
        mimicTeethTopRow(x,y-2-29*Math.sin(Math.toRadians(45)),gc,1);
        //bottom row
        mimicTeethBottomRow(x,y,gc,1);
        //top row


        //eyes
        if(isAlive) {
            mimicEye(x+8,y-18,gc, true, 1);
            mimicEye(x+22,y-18,gc, true, 1);
        }
    }
    private void mimicTooth(double x, double y, GraphicsContext gc, boolean isFacingUp, double direction ,double size) {
        gc.setFill(Color.ANTIQUEWHITE);
        if(isFacingUp) {
            gc.fillPolygon(
                    new double[] {x-size , x, x+size, x+direction*size},
                    new double[] {y, y+size, y, y-3*size},
                    4
            );
        } else {
            gc.fillPolygon(
                    new double[] {x-size , x, x+size, x+direction*size},
                    new double[] {y,y-size,y,y+3*size},
                    4
            );
        }
    }
    private void mimicEye(double x, double y, GraphicsContext gc, boolean isAlive, double scale) {
        gc.setFill(Color.BLACK);
        gc.fillOval(x-5, y-4, 10*scale, 8*scale);
        gc.setFill(Color.GHOSTWHITE);
        gc.fillOval(x-4, y-3, 8*scale, 6*scale);
        if(isAlive) {
            gc.setFill(Color.DARKRED);
            gc.fillOval(x - 2, y - 3, 4*scale, 6*scale);
            gc.setFill(Color.BLACK);
            gc.fillOval(x - 2, y - 1, 4*scale, 2*scale);
        } else {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeLine(x-4,y-3,x+4,y+3);
            gc.strokeLine(x-4,y+3,x+4,y-3);
        }
    }
    private void mimicTeethTopRow(double x, double y, GraphicsContext gc, double scale) {
        y+=20;
        mimicTooth(x+2,y-11,gc,false,0.5,scale*2);
        mimicTooth(x+28,y-11,gc,false,-0.5,scale*2);
        mimicTooth(x+7,y-11,gc,false,0.5,scale*4);
        mimicTooth(x+23,y-11,gc,false,-0.5,scale*4);
        mimicTooth(x+12,y-11,gc,false,0,scale*3);
        mimicTooth(x+18,y-11,gc,false,0,scale*3);
        mimicTooth(x+2,y-5,gc,false,1.5,scale*1);
        mimicTooth(x+28,y-5,gc,false,-1.5,scale*1);
    }
    private void mimicTeethBottomRow(double x, double y, GraphicsContext gc, double scale) {
        mimicTooth(x+1,y+9,gc,true, 0.5,scale*3);
        mimicTooth(x+28,y+9,gc,true,-0.5,scale*3);
        mimicTooth(x+6,y+9,gc,true,0.5,scale*2);
        mimicTooth(x+23,y+9,gc,true,-0.5,scale*2);
    }


    private void openedChest(double x, double y, int index, GraphicsContext gc) {
        if (chests[index] == ChestContent.MIMIC ||chests[index] == ChestContent.KILLED) mimicSprite(x, y, gc, chests[index] == ChestContent.MIMIC );
        else {

            Color bodyColor = (chests[index] == ChestContent.MIMIC_KILLER) ? Color.GOLD : Color.SADDLEBROWN;
            chestBottom(x,y,gc,bodyColor);
            chestLid(x,y,gc,bodyColor,165, false);
            chestLock(x,y,gc);
            chestToken(x+15,y-10,gc,(chests[index] == ChestContent.MIMIC_KILLER) ? Color.GOLD : Color.DARKGRAY,0);
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Arial",10));
            switch (chests[index]) {
                case ChestContent.EMPTY:
                    if (x2Active) {
                        gc.fillText("10", x + 11, y -3);
                    } else {
                        gc.fillText("5", x + 13, y -6);
                    }

                    break;

                case ChestContent.X2_REWARD:

                    gc.fillText("X2", x+10,y-6);
                    break;

            }

        }
    }
    private void chestBottom(double x, double y, GraphicsContext gc, Color bodyColor) {
        Color frameColor = bodyColor.darker();
        double chestWidth = 30;
        double chestHeight = 21;
        // 🔳 Shadow/base (adjusted to new height)
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x, y + 9, chestWidth, chestHeight, 4, 4);
        // 🧱 Chest body
        gc.setFill(bodyColor);
        gc.fillRoundRect(x + 1, y + 10, chestWidth - 2, chestHeight - 3, 3, 3);
        // Framing (new vertical size)
        gc.setFill(frameColor);
        gc.fillRect(x + 1, y + 10, 2, chestHeight - 4);               // left wall
        gc.fillRect(x + chestWidth - 3, y + 10, 2, chestHeight - 4);  // right wall
        gc.fillRect(x + 1, y + 28, chestWidth - 2, 1);  // bottom edge
        // Horizontal planks (adjusted to new Y ranges)
        gc.setStroke(frameColor);
        gc.setLineWidth(1);
        gc.strokeLine(x + 3, y + 16, x + 27, y + 16);
        gc.strokeLine(x + 3, y + 22, x + 27, y + 22);
        // Final outline
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRoundRect(x, y + 9, chestWidth, chestHeight, 4, 4);
    };
    private void chestLock(double x, double y, GraphicsContext gc) {
        // 🔐 Lock (still visible for consistency, optional)
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x + 10, y + 4, 10, 10, 4, 4);      // Raised slightly
        gc.setFill(Color.DARKGRAY);
        gc.fillOval(x + 12, y + 3, 6, 4);                   // shackle
        gc.setFill(Color.SILVER);
        gc.fillRect(x + 12, y + 7, 6, 4);                   // lock face
        gc.setFill(Color.BLACK);
        gc.fillOval(x + 14, y + 8, 2, 2);                   // keyhole
    };
    private void chestLid(double x, double y, GraphicsContext gc, Color bodyColor, double angle, boolean isMimic) {
        double lidElev = 29 * Math.sin(Math.toRadians(angle));
        double lidHeight = 10 * Math.cos(Math.toRadians(angle));
        double underLidTrapezoidSlant = 2*Math.cos(Math.toRadians(angle));
        double lidTrapezoidSlant = 1*Math.cos(Math.toRadians(angle));
        gc.setFill(Color.BLACK);
        gc.fillPolygon(
                new double[] {x+2-underLidTrapezoidSlant, x+2, x+28, x+28+underLidTrapezoidSlant},
                new double[] {y+8-lidElev, y+8, y+8, y+8-lidElev},
                4
        );
        if(angle<90) {
            gc.setFill(Color.BLACK);
            gc.fillPolygon(
                    new double[]{x + 2 - underLidTrapezoidSlant, x + 2 - underLidTrapezoidSlant + lidTrapezoidSlant, x + 28 + underLidTrapezoidSlant - lidTrapezoidSlant, x + 28 + underLidTrapezoidSlant},
                    new double[]{y + 8 - lidElev, y + 8 - lidElev - lidHeight, y + 8 - lidElev - lidHeight, y + 8 - lidElev},
                    4

            );
            gc.setFill(bodyColor.darker().darker());
            if(isMimic) gc.setFill(Color.RED.darker().darker());
            gc.fillPolygon(
                    new double[] {x+3-underLidTrapezoidSlant, x+3, x+27, x+27+underLidTrapezoidSlant},
                    new double[] {y+8-lidElev, y+7, y+7, y+8-lidElev},
                    4
            );
            gc.setFill(bodyColor.darker());
            gc.fillPolygon(
                    new double[]{x + 3 - underLidTrapezoidSlant, x + 3 - underLidTrapezoidSlant + lidTrapezoidSlant, x + 27 + underLidTrapezoidSlant - lidTrapezoidSlant, x + 27 + underLidTrapezoidSlant},
                    new double[]{y + 6 - lidElev, y + 9 - lidElev - lidHeight, y + 9 - lidElev - lidHeight, y + 6 - lidElev},
                    4

            );
            gc.setFill(bodyColor);
            if(angle<70) gc.fillPolygon(
                    new double[]{x + 5 - underLidTrapezoidSlant, x + 5 - underLidTrapezoidSlant + lidTrapezoidSlant, x + 25 + underLidTrapezoidSlant - lidTrapezoidSlant, x + 25 + underLidTrapezoidSlant},
                    new double[]{y + 5 - lidElev, y + 7 + Math.sin(Math.toRadians(angle)) - lidElev - 0.7*lidHeight, y + 7 + Math.sin(Math.toRadians(angle)) - lidElev - 0.7*lidHeight, y + 5 - lidElev},
                    4

            );
            gc.setFill(bodyColor.brighter());
            if(angle<60)  gc.fillPolygon(
                    new double[]{x + 5 - underLidTrapezoidSlant, x + 5 - underLidTrapezoidSlant + lidTrapezoidSlant, x + 25 + underLidTrapezoidSlant - lidTrapezoidSlant, x + 25 + underLidTrapezoidSlant},
                    new double[]{y + 3 - lidElev, y + 4 + 0.4*Math.sin(Math.toRadians(angle)) - lidElev - 0.3*lidHeight, y + 4 + 0.4*Math.sin(Math.toRadians(angle))  - lidElev - 0.3*lidHeight, y + 3 - lidElev},
                    4

            );
        } else {
            gc.setFill(bodyColor.darker().darker());
            gc.fillPolygon(
                    new double[] {x+4-underLidTrapezoidSlant, x+4, x+26, x+26+underLidTrapezoidSlant},
                    new double[] {y+9-lidElev, y+7, y+7, y+9-lidElev},
                    4
            );
        }
        //rear hinges
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x + 4, y + 6, 4, 3);
        gc.fillRect(x + 22, y + 6, 4, 3);
    };
    private void chestToken(double x, double y, GraphicsContext gc,Color tokenColor,  double angle) {
        double width = 15;
        tokenColor = tokenColor.darker();
        angle = angle%360;

        double adjustWidth = Math.abs(width*Math.cos(Math.toRadians(angle)));


            angle = Math.toRadians(angle);
            gc.setFill(tokenColor.darker());
            gc.fillRoundRect(x-adjustWidth/2+2*Math.cos(angle),y-width/2+2, adjustWidth,width,4,4 );
            gc.setFill(tokenColor);
            gc.fillRoundRect(x-adjustWidth/2,y-width/2, adjustWidth,width,4,4 );

        if(angle ==90 || angle ==270) {
            gc.setFill(tokenColor.darker());
            gc.fillRoundRect(x-adjustWidth/2,y-width/2, 2,width,4,4 );
        }

    }

    private void hoverAnimation(double x, double y,GraphicsContext gc, int index, Color color) {
        if(chests[index] == ChestContent.MIMIC_KILLER) color = Color.GOLD;
        int length = 15;
        animations[index].frame +=1;
        int frame = animations[index].frame;
        chestBottom(x,y,gc,color);
        chestLid(x,y,gc,color,1*frame, false);
        chestLock(x,y,gc);
        if(frame == 1) {
            animations[index].isRunning = true;
        }else if(frame == length) {
            animations[index].isRunning = false;
            animations[index].chestState = "hovered";
            animations[index].frame = 0;
        }



    }
    private void unHoverAnimation(double x, double y,GraphicsContext gc, int index, Color color) {
        if(chests[index] == ChestContent.MIMIC_KILLER) color = Color.GOLD;
        int length = 15;
        if(animations[index].frame== 0) {
            animations[index].frame = length;
            animations[index].isRunning = true;
            animations[index].chestState = "unhovering";
        }
        animations[index].frame -=1;
        int frame = animations[index].frame;
        if(frame == 0) {
            chestGraphic(x,y,index,gc);
            animations[index].isRunning = false;
            animations[index].chestState = "inactive";
            animations[index].frame = 0;
        } else {
            chestBottom(x, y, gc, color);
            chestLid(x, y, gc, color, 1 * frame - 1, false);
            chestLock(x, y, gc);
        }
    }
    private void openChestAnimation(double x, double y,GraphicsContext gc, int index, Color color) {
        if(chests[index] == ChestContent.MIMIC_KILLER) color = Color.GOLD;
        if(chests[index] == ChestContent.MIMIC || chests[index] == ChestContent.KILLED) {
            mimicChestAnimation(x,y,gc,index);
            return;
        }
        int length = 110;
        if(animations[index].frame== 0) {
            animations[index].isRunning = true;
            animations[index].chestState = "opening";
        }
            animations[index].frame ++;
        int frame = animations[index].frame;
        if(frame <=50) {
            chestBottom(x,y,gc, color);
            chestLid(x, y, gc, color, 15+ animations[index].frame*3, false);
            chestLock(x, y, gc);
        } else {
            chestBottom(x,y,gc, color);
            chestLid(x, y, gc, color, 165, false);
            chestLock(x, y, gc);
        }


        if(animations[index].frame==length) {
            animations[index].isRunning = false;
            animations[index].chestState = "opened";
            animations[index].frame = 0;
            openedChest(x,y,index,gc);
        }
        //token will do 1 rotation at 30/F, 12F total
        //1 rotation at 24/F, 15F, 27F total
        //0.5 rotation at 18/F, 10F, 37F total
        //0.5 rotation at 12/F, 15F, 52F total
        //0.5 rotation at 10/F, 18F, 70F total
        //0.33 rotation at 6/F, 20F, 90F total
        //0.16 rotation at 3/F, 20F, 110F total
        //start from 25th frame
        Color tokenColor = (chests[index] == ChestContent.MIMIC_KILLER) ? Color.GOLD : Color.DARKGRAY;
        int angle;
        if (frame <= 12) {
            chestToken(x + 15, y + 2 - frame, gc, tokenColor, frame * 30);
        } else if (frame <= 27) {
            angle = frame * 24;
            chestToken(x + 15, y - 10, gc, tokenColor, angle);
        } else if (frame <= 37) {
            angle = 180 + frame * 12;
            chestToken(x + 15, y - 10, gc, tokenColor, angle);
        } else if (frame <= 52) {
            angle = 180 + frame * 12;
            chestToken(x + 15, y - 10, gc, tokenColor, angle);
        } else if (frame <= 70) {
            angle = frame * 10;
            chestToken(x + 15, y - 10, gc, tokenColor, angle);
        } else if (frame <= 90) {
            angle = 180 + frame * 6;
            chestToken(x + 15, y - 10, gc, tokenColor, angle);
        } else {
            angle = 300 + frame * 3;
            chestToken(x + 15, y - 10, gc, tokenColor, angle);
        }

    }
    private void mimicChestAnimation(double x, double y,GraphicsContext gc, int index) {
//        animations[index].chestState = "opened";
//        mimicSprite(x, y, gc, chests[index] == ChestContent.MIMIC );
        int length = (ChestContent.KILLED == chests[index]) ? 60 : 30;
        if(animations[index].frame==0) {
            animations[index].isRunning = true;
            animations[index].chestState = "opening";

        }
        animations[index].frame ++;
        int frame = animations[index].frame;
        chestBottom(x,y,gc,Color.SADDLEBROWN);
        if(frame <=30) {
            chestLid(x,y - (double)frame/15, gc, Color.SADDLEBROWN, 15+frame, true);
            chestLock(x, y, gc);
            mimicTeethTopRow(x,y - (double)frame/15 - 29*Math.sin(Math.toRadians(15+frame)),gc,(double)frame/30);
            mimicTeethBottomRow(x,y,gc,(double)frame/30);
            mimicEye(x+8,y-18,gc,true, (double) frame /30);
            mimicEye(x+22,y-18,gc,true,(double) frame /30);
        } else {
            chestLid(x,y-2, gc, Color.SADDLEBROWN , 45, true);
            chestLock(x, y, gc);
            mimicTeethTopRow(x,y-2-29*Math.sin(Math.toRadians(45)),gc,1);
            mimicTeethBottomRow(x,y,gc,1);
            double yGravity = 0.2*Math.pow(frame - 30, 2);
            //L eye
            mimicEye(x+8 - (frame - 30), y-18+yGravity,gc,true,1);
            //R eye
            mimicEye(x+22+(frame - 30), y-18+yGravity,gc,true,1);
        }
        if(animations[index].frame==length) {
            animations[index].isRunning = false;
            animations[index].chestState = "opened";
            animations[index].frame = 0;
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