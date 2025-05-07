package com.hoke.games.assets;

import com.hoke.games.engine.AbstractGame;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.*;
import java.io.InputStream;
import java.util.List;


public enum Card {

    ACE_OF_SPADES(Suit.SPADES, Rank.ACE),
    TWO_OF_SPADES(Suit.SPADES, Rank.TWO),
    THREE_OF_SPADES(Suit.SPADES, Rank.THREE),
    FOUR_OF_SPADES(Suit.SPADES, Rank.FOUR),
    FIVE_OF_SPADES(Suit.SPADES, Rank.FIVE),
    SIX_OF_SPADES(Suit.SPADES, Rank.SIX),
    SEVEN_OF_SPADES(Suit.SPADES, Rank.SEVEN),
    EIGHT_OF_SPADES(Suit.SPADES, Rank.EIGHT),
    NINE_OF_SPADES(Suit.SPADES, Rank.NINE),
    TEN_OF_SPADES(Suit.SPADES, Rank.TEN),
    JACK_OF_SPADES(Suit.SPADES, Rank.JACK),
    QUEEN_OF_SPADES(Suit.SPADES, Rank.QUEEN),
    KING_OF_SPADES(Suit.SPADES, Rank.KING),

    ACE_OF_CLUBS(Suit.CLUBS, Rank.ACE),
    TWO_OF_CLUBS(Suit.CLUBS, Rank.TWO),
    THREE_OF_CLUBS(Suit.CLUBS, Rank.THREE),
    FOUR_OF_CLUBS(Suit.CLUBS, Rank.FOUR),
    FIVE_OF_CLUBS(Suit.CLUBS, Rank.FIVE),
    SIX_OF_CLUBS(Suit.CLUBS, Rank.SIX),
    SEVEN_OF_CLUBS(Suit.CLUBS, Rank.SEVEN),
    EIGHT_OF_CLUBS(Suit.CLUBS, Rank.EIGHT),
    NINE_OF_CLUBS(Suit.CLUBS, Rank.NINE),
    TEN_OF_CLUBS(Suit.CLUBS, Rank.TEN),
    JACK_OF_CLUBS(Suit.CLUBS, Rank.JACK),
    QUEEN_OF_CLUBS(Suit.CLUBS, Rank.QUEEN),
    KING_OF_CLUBS(Suit.CLUBS, Rank.KING),

    ACE_OF_DIAMONDS(Suit.DIAMONDS, Rank.ACE),
    TWO_OF_DIAMONDS(Suit.DIAMONDS, Rank.TWO),
    THREE_OF_DIAMONDS(Suit.DIAMONDS, Rank.THREE),
    FOUR_OF_DIAMONDS(Suit.DIAMONDS, Rank.FOUR),
    FIVE_OF_DIAMONDS(Suit.DIAMONDS, Rank.FIVE),
    SIX_OF_DIAMONDS(Suit.DIAMONDS, Rank.SIX),
    SEVEN_OF_DIAMONDS(Suit.DIAMONDS, Rank.SEVEN),
    EIGHT_OF_DIAMONDS(Suit.DIAMONDS, Rank.EIGHT),
    NINE_OF_DIAMONDS(Suit.DIAMONDS, Rank.NINE),
    TEN_OF_DIAMONDS(Suit.DIAMONDS, Rank.TEN),
    JACK_OF_DIAMONDS(Suit.DIAMONDS, Rank.JACK),
    QUEEN_OF_DIAMONDS(Suit.DIAMONDS, Rank.QUEEN),
    KING_OF_DIAMONDS(Suit.DIAMONDS, Rank.KING),

    ACE_OF_HEARTS(Suit.HEARTS, Rank.ACE),
    TWO_OF_HEARTS(Suit.HEARTS, Rank.TWO),
    THREE_OF_HEARTS(Suit.HEARTS, Rank.THREE),
    FOUR_OF_HEARTS(Suit.HEARTS, Rank.FOUR),
    FIVE_OF_HEARTS(Suit.HEARTS, Rank.FIVE),
    SIX_OF_HEARTS(Suit.HEARTS, Rank.SIX),
    SEVEN_OF_HEARTS(Suit.HEARTS, Rank.SEVEN),
    EIGHT_OF_HEARTS(Suit.HEARTS, Rank.EIGHT),
    NINE_OF_HEARTS(Suit.HEARTS, Rank.NINE),
    TEN_OF_HEARTS(Suit.HEARTS, Rank.TEN),
    JACK_OF_HEARTS(Suit.HEARTS, Rank.JACK),
    QUEEN_OF_HEARTS(Suit.HEARTS, Rank.QUEEN),
    KING_OF_HEARTS(Suit.HEARTS, Rank.KING),

    //Special Cards
    BACK_RED(null,null),
    BACK_BLACK(null,null),
    BLANK(null,null);

    private static final Map<Card,Image> imageMap = new EnumMap<>(Card.class);// empty map, mapping each card to a respective image file


    static { //populates the image map with the corresponding files
        for (Card card : Card.values()) {//loops through each card
            String imagePath;
            if(card.suit!=null && card.rank !=null) {
                //Normal Cards
                imagePath = String.format("/com/hoke/games/assets/cardPNGS/%s_%s.png", card.rank.shortName, card.suit.name().toLowerCase());
            } else {
                //Special Cards
                imagePath = String.format("/com/hoke/games/assets/cardPNGS/%s.png", card.name().toLowerCase());
            }
            try (InputStream stream = Card.class.getResourceAsStream(imagePath)) {
                if(stream != null) {
                    imageMap.put(card, new Image(stream));
                } else {
                    System.err.println("Image not found: " + imagePath);
                }
            } catch(Exception e) {
                System.err.println("Error loading image: " + imagePath);
                e.printStackTrace();
            }
        }
    }

    public Image getImage() {
        return imageMap.get(this);
    }

    public void cardDrawAnimation(double xFrom, double yFrom, double xTo, double yTo, GraphicsContext gc, double xScale, double yScale) {
        double lengthTime = (double)5.0E8F;
        if (this.animation.time == (double)0.0F) {
            this.animation.isRunning = true;
            this.animation.startTime = (double)System.nanoTime();
            this.animation.state = "moving";
        }

        this.animation.time = (double)System.nanoTime() - this.animation.startTime;
        this.x = xFrom + (xTo - xFrom) * this.animation.time / lengthTime;
        this.y = yFrom + (yTo - yFrom) * this.animation.time / lengthTime;
        if (this.flipped) {
            this.drawBack(gc, xScale, yScale, "RED");
        } else {
            this.drawCard(gc, xScale, yScale);
        }

        if (this.animation.time >= lengthTime) {
            this.animation.isRunning = false;
            this.animation.state = "inactive";
            this.animation.time = (double)0.0F;
        }

    }

    public void cardFlipAnimation(GraphicsContext gc, double xScale, double yScale) {
        double lengthTime = (double)3.0E8F;
        if (this.animation.time == (double)0.0F) {
            this.animation.isRunning = true;
            this.animation.startTime = (double)System.nanoTime();
            this.animation.state = "flipping";
        }

        this.animation.time = (double)System.nanoTime() - this.animation.startTime;
        double progress = this.animation.time / (double)3.0E8F;
        double angle = Math.PI * progress;
        double horizontalScale = xScale * Math.abs(Math.cos(angle));
        boolean pastHalfway = angle > (Math.PI / 2D);
        boolean drawFront = this.flipped && !pastHalfway || !this.flipped && pastHalfway;
        if (drawFront) {
            this.drawCard(gc, horizontalScale, yScale);
        } else {
            this.drawBack(gc, horizontalScale, yScale, "RED");
        }

        if (this.animation.time >= (double)3.0E8F) {
            this.animation.isRunning = false;
            this.animation.time = (double)0.0F;
            this.animation.state = "inactive";
            this.flipped = !this.flipped;
        }

    }


    public enum Suit {
        SPADES, DIAMONDS, CLUBS, HEARTS

    }
    public enum Rank {
        ACE("A"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),
        NINE("9"),
        TEN("10"),
        JACK("J"),
        QUEEN("Q"),
        KING("K");

        public final String shortName;
        Rank(String shortName) {
            this.shortName = shortName;
        }

    }
    public final Rank rank;
    public final Suit suit;
    Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }
    public double x;
    public double y;
    public boolean flipped;
    public AbstractGame.AnimationInfo animation = new AbstractGame.AnimationInfo();
    public static Suit getSuit(Card card) {
        return card.suit;
    }
    public static Rank getRank(Card card) {
        return card.rank;
    }
    public void drawCard( GraphicsContext gc, double xScale, double yScale) {
        Image img = imageMap.get(this);
        if(img != null) {
            gc.drawImage(img, x, y, img.getWidth() * xScale, img.getHeight() * yScale);
        } else {
            System.err.println("No available Image for: " + this.name());
        }
    }
    public void drawBack( GraphicsContext gc, double xScale, double yScale, String color) {
        Image img;
        switch (color) {
            case "BLACK":
                 img = imageMap.get(Card.BACK_BLACK);
            break;
            case "RED":
                 img = imageMap.get(Card.BACK_RED);
            break;
            default:
                System.err.println("No available Image for color: " + color);
                return;
        }
        gc.drawImage(img,x,y,img.getWidth()*xScale,img.getHeight()*yScale);
    }

    public static Card newRandomCard(List<Card> deck) {
        int index = (new Random()).nextInt(deck.size());
        Card card = (Card)deck.get(index);
        deck.remove(index);
        return card;
    }
}
