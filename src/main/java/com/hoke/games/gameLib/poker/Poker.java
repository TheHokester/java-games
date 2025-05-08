package com.hoke.games.gameLib.poker;

import com.hoke.games.assets.Card;
import com.hoke.games.engine.AbstractGame;
import javafx.scene.canvas.GraphicsContext;

import java.util.*;

public class Poker extends AbstractGame {
    List<Card> deck = new ArrayList<>();
    List<Card> communityCards = new ArrayList<>();
    List<PokerPlayer> players = new ArrayList<>();
    private static final int BOT_COUNT = 4;
    private static final int SMALL_BLIND = 5;
    private static final int BIG_BLIND = 2 * SMALL_BLIND;
    private static void dealCards() {

    }

    @Override
    protected void renderGame(GraphicsContext gc) {

    }

    @Override
    protected void onGameClick(double x, double y) {

    }

    @Override
    public void start(GraphicsContext gc) {
        communityCards.clear();
        deck.clear();
        players.clear();
        for(Card card : Card.values()) {
            if(card.suit !=null && card.rank !=null) {
                deck.add(card);
            }
        }
        players.add(new PokerPlayer(deck));
        for(int index = 0; index < BOT_COUNT; index++) {
            players.add(new PokerBot(deck));
        }
    }
    public static int cardValue(Card card) {
        switch(card.rank) {
            case ACE: return 14;
            case TWO: return 2;
            case THREE: return 3;
            case FOUR: return 4;
            case FIVE: return 5;
            case SIX: return 6;
            case SEVEN: return 7;
            case EIGHT: return 8;
            case NINE: return 9;
            case TEN: return 10;
            case JACK: return 11;
            case QUEEN: return 12;
            case KING: return 13;
            default: return 0;
        }
    }

    public void flop() {
        communityCards.add(Card.newRandomCard(deck));
        communityCards.add(Card.newRandomCard(deck));
        communityCards.add(Card.newRandomCard(deck));
    }
    public void turn() {
        communityCards.add(Card.newRandomCard(deck));
    }
    public void river() {
        communityCards.add(Card.newRandomCard(deck));
    }
}
