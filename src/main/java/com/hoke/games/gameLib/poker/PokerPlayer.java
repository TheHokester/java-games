package com.hoke.games.gameLib.poker;

import com.hoke.games.assets.Card;

import java.util.ArrayList;
import java.util.*;

public class PokerPlayer {
    public List<Card> hand = new ArrayList<>();
    public Bet bet = new Bet();
    private boolean acted;
    boolean folded = false;

    PokerPlayer() {
        this.folded = false;
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
}
