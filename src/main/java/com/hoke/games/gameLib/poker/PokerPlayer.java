package com.hoke.games.gameLib.poker;

import com.hoke.games.UIAssets.Card;

import java.util.ArrayList;
import java.util.*;

public class PokerPlayer {
    public List<Card> hand = new ArrayList<>();
    public Bet bet = new Bet();
    private boolean acted;
    boolean folded = false;
    private int chipBalance = 0;

    PokerPlayer() {
        this.folded = false;
        chipBalance = 1000;
    }
    public int getBalance() {
        return chipBalance;
    }
    public List<Card> getHand() {
        return hand;
    }
    public boolean hasFolded() {
        return folded;
    }
    public void fold() {
        folded = !folded;
    }
    public boolean hasActed() {
        return acted;
    }
    public void act() {// allows me to reverse the state.
        acted = true;
    }

    public void takeTurn() {

    }

    public void resetForNextBettingRound() {// clear per round information
        acted = false;
    }

    public void resetHand() {
        hand.clear();
    }

    public void postSmallBlind() {
        int toPay = Math.min(chipBalance, Bet.SMALL_BLIND);
        bet.committed += toPay;
        chipBalance -= toPay;
        act();
    }
    public void postBigBlind() {
        int toPay = Math.min(chipBalance, Bet.SMALL_BLIND);
        bet.committed += toPay;
        chipBalance -= toPay;
        act();
    }
    public double getChipBalance() {
        return chipBalance;
    }
}
