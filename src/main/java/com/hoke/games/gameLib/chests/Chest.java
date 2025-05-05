package com.hoke.games.gameLib.chests;

import com.hoke.games.engine.AbstractGame;
import com.hoke.games.engine.GameEngine;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.EnumSet;

public class Chest {
    private static final int CHEST_WIDTH = 30;
    private static final int CHEST_HEIGHT = 30;
    public double x;
    public double y;
    private GraphicsContext gc;
    public Content content;
    private AbstractGame.AnimationInfo animation;
    public boolean searched = false;
    private Color color;


    public Chest(double x, double y, Content content, GraphicsContext gc) {
        this.content = content;
        this.x = x;
        this.y = y;
        this.searched = false;
        this.animation = new AbstractGame.AnimationInfo();
        this.gc = gc;
        color = (content == Content.MIMIC_KILLER) ? Color.GOLD : Color.SADDLEBROWN;
    }
    enum Content {
        EMPTY('.'),
        MIMIC('!'),
        X2_REWARD('2'),
        MIMIC_KILLER('?'),
        KILLED('x');

        private final char symbol;

        Content(char symbol) { this.symbol = symbol;}

        public char getSymbol() { return symbol; }

        public boolean isSafe() {
            return SAFE_TYPES.contains(this);
        }
        public boolean isMimic() {
            return MIMIC_TYPES.contains(this);
        }
    }
    static final EnumSet<Content> SAFE_TYPES =
            EnumSet.of(Content.EMPTY, Content.X2_REWARD, Content.MIMIC_KILLER);

    static final EnumSet<Content> MIMIC_TYPES =
            EnumSet.of(Content.MIMIC, Content.KILLED);

    public void printChest(boolean clicked, boolean hovered) {
        if ((clicked && !searched) || animation.state.equals("opening")) {
            openChestAnimation();
            return;
        }
        if (!animation.isRunning && searched) {
            openedChest();
            return;
        }
        if (hovered && animation.state.equals("closed") || animation.state.equals("hovering")) {
            hoverAnimation();
            return;
        }
        if (animation.state.equals("hovered") || animation.state.equals("hovering") || animation.state.equals("unhovering")) {
            unHoverAnimation();
            return;
        }
        if (animation.state.equals("hovered")) {
            chestHover();
            return;
        }
        chestGraphic();
    }
    public boolean isHovered(GameEngine engine) {
        double hoverX = engine.getMouseX();
        double hoverY = engine.getMouseY();

        return hoverX >= x && hoverX <= x + CHEST_WIDTH && hoverY >= y && hoverY <= y + CHEST_HEIGHT;
    }
    public boolean isClicked(double clickX, double clickY) {
        return clickX >= x && clickX <= x + CHEST_WIDTH && clickY >= y && clickY <= y + CHEST_HEIGHT;
    }






    private void chestGraphic() {

        Color frameColor = color.darker();

        // Shadow/background
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x, y, 30, 30, 4, 4); // Slight rounding

        // Lid
        gc.setFill(color);
        gc.fillRoundRect(x + 1, y + 1, 28, 7, 3, 3);

        gc.setFill(color.brighter());
        gc.fillRect(x + 2, y + 2, 26, 2); // Highlight on lid

        // Lid framing
        gc.setFill(frameColor);
        gc.fillRect(x + 1, y + 1, 2, 7);
        gc.fillRect(x + 27, y + 1, 2, 7);
        gc.fillRect(x + 1, y + 7, 28, 1);

        // Body
        gc.setFill(color);
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

        chestLock();

        // Final chest outline
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRoundRect(x, y, 30, 30, 4, 4);
    }//fully closed(rounded) chest
    private void chestHover() {


        chestBottom();
        //lid
        chestLid(15, false);
        // 🔐 Lock (still visible for consistency, optional)
        chestLock();


    }//hovered chest
    private void chestLock() {
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
    private void openedChest() {
        if (content.isMimic()) mimicSprite(content == Content.MIMIC);
        else {


            chestBottom();
            chestLid(165, false);
            chestLock();
            chestToken(x+15,y-10,0);
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Arial",10));
            switch (content) {
                case Content.EMPTY:
                        gc.fillText("5", x + 13, y -6);
                    break;

                case Content.X2_REWARD:
                    gc.fillText("X2", x+10,y-6);
                    break;

            }

        }
    }
    private void chestBottom() {
        Color frameColor = color.darker();
        double chestWidth = 30;
        double chestHeight = 21;
        // 🔳 Shadow/base (adjusted to new height)
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x, y + 9, chestWidth, chestHeight, 4, 4);
        // 🧱 Chest body
        gc.setFill(color);
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
    private void chestLid( double angle, boolean isMimic) {
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
            gc.setFill(color.darker().darker());
            if(isMimic) gc.setFill(Color.RED.darker().darker());
            gc.fillPolygon(
                    new double[] {x+3-underLidTrapezoidSlant, x+3, x+27, x+27+underLidTrapezoidSlant},
                    new double[] {y+8-lidElev, y+7, y+7, y+8-lidElev},
                    4
            );
            gc.setFill(color.darker());
            gc.fillPolygon(
                    new double[]{x + 3 - underLidTrapezoidSlant, x + 3 - underLidTrapezoidSlant + lidTrapezoidSlant, x + 27 + underLidTrapezoidSlant - lidTrapezoidSlant, x + 27 + underLidTrapezoidSlant},
                    new double[]{y + 6 - lidElev, y + 9 - lidElev - lidHeight, y + 9 - lidElev - lidHeight, y + 6 - lidElev},
                    4

            );
            gc.setFill(color);
            if(angle<70) gc.fillPolygon(
                    new double[]{x + 5 - underLidTrapezoidSlant, x + 5 - underLidTrapezoidSlant + lidTrapezoidSlant, x + 25 + underLidTrapezoidSlant - lidTrapezoidSlant, x + 25 + underLidTrapezoidSlant},
                    new double[]{y + 5 - lidElev, y + 7 + Math.sin(Math.toRadians(angle)) - lidElev - 0.7*lidHeight, y + 7 + Math.sin(Math.toRadians(angle)) - lidElev - 0.7*lidHeight, y + 5 - lidElev},
                    4

            );
            gc.setFill(color.brighter());
            if(angle<60)  gc.fillPolygon(
                    new double[]{x + 5 - underLidTrapezoidSlant, x + 5 - underLidTrapezoidSlant + lidTrapezoidSlant, x + 25 + underLidTrapezoidSlant - lidTrapezoidSlant, x + 25 + underLidTrapezoidSlant},
                    new double[]{y + 3 - lidElev, y + 4 + 0.4*Math.sin(Math.toRadians(angle)) - lidElev - 0.3*lidHeight, y + 4 + 0.4*Math.sin(Math.toRadians(angle))  - lidElev - 0.3*lidHeight, y + 3 - lidElev},
                    4

            );
        } else {
            gc.setFill(color.darker().darker());
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
    private void chestToken(double x,double y,double angle) {
        Color tokenColor = Color.DARKGRAY;
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
    private void mimicSprite( boolean isAlive) {

        Color bodyColor = Color.SADDLEBROWN;
        Color frameColor = bodyColor.darker();

        chestBottom();

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
        chestLock();                  // keyhole

        //rear hinges
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x + 4, y + 4, 4, 3);
        gc.fillRect(x + 22, y + 4, 4, 3);

        //teeth
        mimicTeethTopRow(1);
        //bottom row
        mimicTeethBottomRow(1);
        //top row


        //eyes
        if(isAlive) {
            mimicEye(x+8,y-18, true, 1);
            mimicEye(x+22,y-18, true, 1);
        }
    }
    private void mimicTooth(double x, double y, boolean isFacingUp, double direction ,double size) {
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
    private void mimicEye(double x, double y, boolean isAlive, double scale) {
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
    private void mimicTeethTopRow(double scale) {
        y+=20;
        mimicTooth(x+2,y-11,false,0.5,scale*2);
        mimicTooth(x+28,y-11,false,-0.5,scale*2);
        mimicTooth(x+7,y-11,false,0.5,scale*4);
        mimicTooth(x+23,y-11,false,-0.5,scale*4);
        mimicTooth(x+12,y-11,false,0,scale*3);
        mimicTooth(x+18,y-11,false,0,scale*3);
        mimicTooth(x+2,y-5,false,1.5,scale*1);
        mimicTooth(x+28,y-5,false,-1.5,scale*1);
    }
    private void mimicTeethBottomRow(double scale) {
        mimicTooth(x+1,y+9,true, 0.5,scale*3);
        mimicTooth(x+28,y+9,true,-0.5,scale*3);
        mimicTooth(x+6,y+9,true,0.5,scale*2);
        mimicTooth(x+23,y+9,true,-0.5,scale*2);
    }


    private void hoverAnimation() {

        double lengthTime = 125_000_000;//time length seconds
        if(animation.time == 0) {
            animation.startTime = System.nanoTime();
            animation.isRunning = true;
            animation.state = "hovering";

        }
        animation.time = System.nanoTime() - animation.startTime;
        chestBottom();
        chestLid(15* animation.time/lengthTime, false);
        chestLock();

        if(animation.time >= lengthTime) {
            animation.isRunning = false;
            animation.state = "hovered";
            animation.time = 0;
        }



    }
    private void unHoverAnimation() {

        double lengthTime = 125_000_000; // duration in nanoseconds

        if (animation.time == 0) {
            animation.startTime = System.nanoTime();
            animation.isRunning = true;
            animation.state = "unhovering";
        }

        animation.time = System.nanoTime() - animation.startTime;

        // Calculate progress reversed (from 1.0 to 0.0)
        double progress = 1.0 - (double) animation.time / lengthTime;
        progress = Math.max(0, Math.min(progress, 1)); // clamp to [0,1]

        chestBottom();
        chestLid(15 * progress, false);
        chestLock();

        if (animation.time >= lengthTime) {
            animation.isRunning = false;
            animation.state = "inactive";
            animation.time = 0;
        }
    }
    private void openChestAnimation() {
        double lengthTime = 916_666_667; // total duration in nanoseconds

        if (animation.time == 0) {
            animation.startTime = System.nanoTime();
            animation.isRunning = true;
            animation.state = "opening";
            if (content.isMimic()) {
                mimicChestAnimation(); // defer to mimic animation
                return;
            }
        }

        animation.time = System.nanoTime() - animation.startTime;
        double progress = Math.min(animation.time / lengthTime, 1.0);

        // Lid angle (0.0 to 1.0 mapped to 15 to 165 degrees)
        double lidAngle = 15 + 150 * Math.min(progress, 50.0 / 110.0); // stabilize after frame 50
        chestBottom();
        chestLid( lidAngle, false);
        chestLock();

        // Token animation: mimics frame-based behavior using progress
        int pseudoFrame = (int)(progress * 110);
        Color tokenColor = (content == Content.MIMIC_KILLER) ? Color.GOLD : Color.DARKGRAY;
        int angle;
        if (pseudoFrame <= 12) {
            chestToken(x + 15, y + 2 - pseudoFrame,  pseudoFrame * 30);
        } else if (pseudoFrame <= 27) {
            angle = pseudoFrame * 24;
            chestToken(x + 15, y - 10, angle);
        } else if (pseudoFrame <= 37) {
            angle = 180 + pseudoFrame * 12;
            chestToken(x + 15, y - 10, angle);
        } else if (pseudoFrame <= 52) {
            angle = 180 + pseudoFrame * 12;
            chestToken(x + 15, y - 10, angle);
        } else if (pseudoFrame <= 70) {
            angle = pseudoFrame * 10;
            chestToken(x + 15, y - 10, angle);
        } else if (pseudoFrame <= 90) {
            angle = 180 + pseudoFrame * 6;
            chestToken(x + 15, y - 10, angle);
        } else {
            angle = 300 + pseudoFrame * 3;
            chestToken(x + 15, y - 10, angle);
        }

        if (animation.time >= lengthTime) {
            animation.isRunning = false;
            animation.state = "opened";
            animation.time = 0;
            openedChest();
        }
    }
    private void mimicChestAnimation() {
        double lengthTime = (content == Content.KILLED) ? 500_000_000 : 250_000_000;

        if (animation.time == 0) {
            animation.startTime = System.nanoTime();
            animation.isRunning = true;
            animation.state = "opening";
        }

        animation.time = System.nanoTime() - animation.startTime;
        double progress = Math.min(animation.time / lengthTime, 1.0);
        int pseudoFrame = (int)(progress * ((content == Content.KILLED) ? 60 : 30));

        chestBottom();

        if (pseudoFrame <= 30) {
            double lidAngle = 15 + pseudoFrame;
            double lift = (double)pseudoFrame / 15;
            double teethY = y - lift - 29 * Math.sin(Math.toRadians(lidAngle));
            double eyeProgress = (double)pseudoFrame / 30;

            chestLid(lidAngle, true);
            chestLock();
            mimicTeethTopRow(eyeProgress);
            mimicTeethBottomRow(eyeProgress);
            mimicEye(x + 8, y - 18,  true, eyeProgress);
            mimicEye(x + 22, y - 18,  true, eyeProgress);
        } else {
            chestLid(45, true);
            chestLock();
            mimicTeethTopRow(1);
            mimicTeethBottomRow(1);

            int offset = pseudoFrame - 30;
            double gravity = 0.2 * Math.pow(offset, 2);
            mimicEye(x + 8 - offset, y - 18 + gravity,  true, 1);
            mimicEye(x + 22 + offset, y - 18 + gravity,  true, 1);
        }

        if (animation.time >= lengthTime) {
            animation.isRunning = false;
            animation.state = "opened";
            animation.time = 0;
        }
    }




}
