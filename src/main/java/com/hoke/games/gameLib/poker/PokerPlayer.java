package com.hoke.games.gameLib.poker;

import com.hoke.games.assets.Card;

import java.util.ArrayList;
import java.util.*;

public class PokerPlayer {
    public List<Card> hand = new ArrayList<>();

    PokerPlayer(List<Card> deck) {
        hand.add(Card.newRandomCard(deck));
        hand.add(Card.newRandomCard(deck));
        for (Card card : hand) {
            card.flipped = true;
        }
    }
    public List<Card> getHand() {
        return hand;
    }

}
