package com.hoke.games.gameLib.poker;

import com.hoke.games.assets.Card;
import com.hoke.games.engine.AbstractGame;
import javafx.scene.canvas.GraphicsContext;

import java.util.*;

public class Poker extends AbstractGame {
    List<Card> deck = new ArrayList<>();
    List<Card> communityCards = new ArrayList<>();
    List<PokerPlayer> players = new ArrayList<>();
    private static boolean roundCardDrawActive = false;


    private static int round = 0;
    private static int roundRotation = 0;
    private static final int BOT_COUNT = 4;



    private static final double CARD_SCALE = 0.75;

    private static void dealCards(List<Card> deck, List<PokerPlayer> players) {
        for(PokerPlayer p : players) {
            p.hand.clear();
            for(int i = 0; i < 2; i++) {
                Card card = Card.newRandomCard(deck);
                card.flipped = true;
                p.hand.add(card);
            }
        }
    }

    @Override
    protected void renderGame(GraphicsContext gc) {
        PokerEnvironment.drawTable();
    }

    @Override
    protected void onGameClick(double x, double y) {

    }
    private void initRound() {
        communityCards.clear();
        deck.clear();

        for(Card card : Card.values()) {
            if(card.suit !=null && card.rank !=null) {
                deck.add(card);
            }
        }
        if(round == 0 ) {
            for(int index = 0; index < BOT_COUNT; index++) {
                players.add(new PokerBot());
            }
            players.add(2, new PokerPlayer());
        }
        if(startPlayer < players.size()-1) {
            startPlayer++;
        } else {
            startPlayer = 0;
        }
        turnPlayer = startPlayer;

        dealCards(deck, players);
        gamePhase = GamePhase.PREFLOP;
    }
    @Override
    public void start(GraphicsContext gc) {
        if(gc!=null) {
            new PokerEnvironment(gc);
        }
        initRound();
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


    public enum GamePhase {
        PREFLOP,FLOP,TURN,RIVER,SHOWDOWN, ROUND_END;
    }

    private GamePhase gamePhase = GamePhase.PREFLOP;
    private static int startPlayer = 0;//by index in players array.
    private static int turnPlayer = 0;
    private boolean waitingForPlayer = false;


    public void playRound() {
        switch (gamePhase) {
            case PREFLOP:
                //Players post blinds and perform preflop betting
                runPlayerTurn();
                if(allPlayersActed()) {
                    flop();
                    resetPlayerCycle();
                    gamePhase = GamePhase.FLOP;
                }
            break;
            case FLOP:
                runPlayerTurn();
                if(allPlayersActed()) {
                    turn();
                    resetPlayerCycle();
                    gamePhase = GamePhase.TURN;
                }
                break;
            case TURN:
                runPlayerTurn();
                if(allPlayersActed()) {
                    river();
                    resetPlayerCycle();
                    gamePhase = GamePhase.RIVER;
                }
                break;
            case RIVER:
                runPlayerTurn();
                if(allPlayersActed()) {
                    gamePhase = GamePhase.SHOWDOWN;
                }
                break;
            case SHOWDOWN:
                resolveWinners();
                gamePhase = GamePhase.ROUND_END;
                break;
            case ROUND_END:
                resetRound();
                break;
        }

    }


    private void runPlayerTurn() {
        PokerPlayer player = players.get(turnPlayer);
        if(!player.hasActed()) {
            player.takeTurn();//defined differently for bot vs player
        }
        advanceTurn();
    }
    private void advanceTurn() {
        do {
            turnPlayer = (turnPlayer + 1) % players.size();
        } while(players.get(turnPlayer).hasFolded());
    }
    private void resetPlayerCycle() {
        for(PokerPlayer player : players) {
            player.resetForNextBettingRound();
        }
        turnPlayer = startPlayer;
    }
    private boolean allPlayersActed() {
        for(PokerPlayer player : players) {
            if(!player.hasActed() && !player.hasFolded()) { return false; }
        }
        return true;
    }
    private void resolveWinners() {
        List<List<PokerPlayer>> winners = HandScoring.orderPokerPlayers(players, communityCards);
        List<PokerPlayer> winner = winners.getFirst();
        // do chip assignment and so on and so forth here
    }

    private void resetRound() {
        round++;

        for(PokerPlayer player : players) {
            player.resetHand();
        }
        initRound();
    }

}
