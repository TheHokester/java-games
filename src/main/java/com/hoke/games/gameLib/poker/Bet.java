package com.hoke.games.gameLib.poker;

public class Bet {
    public static final int SMALL_BLIND = 5;
    public static final int BIG_BLIND = 2 * SMALL_BLIND;
    public static final int MIN_BET = 10;

    public int committed;    // Total chips this player has put in this round

    private boolean allIn;

    public Bet() {
        committed = 0;
        allIn = false;
    }

    public int call(int tableCurrentBet, int playerChips) {
        int toCall = Math.min(tableCurrentBet - committed, playerChips);
        committed += toCall;
        if (toCall == playerChips) allIn = true;
        return toCall;
    }

    public int raise(int tableCurrentBet, int raiseAmount, int playerChips) {
        int newTotal = tableCurrentBet + raiseAmount;
        int toPay = Math.min(newTotal - committed, playerChips);
        committed += toPay;
        if (toPay == playerChips) allIn = true;
        return toPay;
    }



    public void resetForNextRound() {
        committed = 0;
        allIn = false;
    }

    public int getCommitted() {
        return committed;
    }



    public boolean isAllIn() {
        return allIn;
    }


}

