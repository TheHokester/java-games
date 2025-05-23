package com.hoke.games.gameLib.poker;

import com.hoke.games.UIAssets.Card;
import com.hoke.games.engine.AbstractGame;
import javafx.scene.canvas.GraphicsContext;

import java.util.*;

public class Poker extends AbstractGame {
    List<Card> deck = new ArrayList<>();
    List<Card> communityCards = new ArrayList<>();
    static List<PokerPlayer> players = new ArrayList<>();
    private static boolean roundCardDrawActive = false;
    private static PokerEnvironment environment;

    public static int round = 0;

    private static final int BOT_COUNT = 4;



    private static final double CARD_SCALE = 0.75;
    private static double clickX;
    private static double clickY;

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
        environment.buttonPanel();

        clickX = -1;
        clickY = -1;
    }

    @Override
    protected void onGameClick(double x, double y) {
        this.clickX = x;
        this.clickY = y;
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
             environment = new  PokerEnvironment(gc);
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
    private boolean preFlopSetupDone = false;
    private boolean waitingForPlayer = false;


    public void playRound() {
        switch (gamePhase) {
            case PREFLOP:
                if(!preFlopSetupDone) {
                    int smallBlindIdx = startPlayer;
                    int bigBlindIdx = (startPlayer + 1) % players.size();

                    players.get(smallBlindIdx).postSmallBlind();
                    players.get(bigBlindIdx).postBigBlind();

                    preFlopSetupDone = true;
                    turnPlayer = (bigBlindIdx+1) % players.size();
                }
                //Players post blinds and perform preflop betting
                runPlayerTurn();
                if(bettingRoundComplete()) {
                    flop();
                    resetPlayerCycle();
                    gamePhase = GamePhase.FLOP;
                }
            break;
            case FLOP:
                runPlayerTurn();
                if(bettingRoundComplete()) {
                    turn();
                    resetPlayerCycle();
                    gamePhase = GamePhase.TURN;
                }
                break;
            case TURN:
                runPlayerTurn();
                if(bettingRoundComplete()) {
                    river();
                    resetPlayerCycle();
                    gamePhase = GamePhase.RIVER;
                }
                break;
            case RIVER:
                runPlayerTurn();
                if(bettingRoundComplete()) {
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
        int maxCommitted = getMaxCommitted();
        if(!player.hasFolded() && player.bet.committed <  maxCommitted) {
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
    private boolean bettingRoundComplete() {
        int maxCommitted = getMaxCommitted();
        for(PokerPlayer player : players) {
            if(player.hasFolded() || player.bet.isAllIn()) continue;

            if(!player.hasActed() || player.bet.committed <  maxCommitted) {
                return false;
            }
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
        preFlopSetupDone = false;
        for(PokerPlayer player : players) {
            player.resetHand();
        }
        initRound();
    }
    private int getMaxCommitted() {
        int maxCommitted = 0;
        for(PokerPlayer player : players) {
            maxCommitted = Math.max(maxCommitted, player.bet.committed);
        }
        return maxCommitted;
    }

    public static double getClickX() {
        return clickX;
    }
    public static double getClickY() {
        return clickY;
    }

    public static List<PokerPlayer> getPlayers() {
        return players;
    }
}
