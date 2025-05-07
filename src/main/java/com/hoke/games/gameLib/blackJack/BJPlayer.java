//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hoke.games.gameLib.blackJack;

import com.hoke.games.assets.Card;
import com.hoke.games.assets.Card.Rank;
import java.util.ArrayList;
import java.util.List;

public class BJPlayer {
    List<Card> hand = new ArrayList();
    boolean stood = false;

    public BJPlayer(List<Card> deck) {
        this.hand.add(Card.newRandomCard(deck));
        this.hand.add(Card.newRandomCard(deck));
        this.stood = false;

        for(Card card : this.hand) {
            card.flipped = true;
        }

    }

    public int cardValue(Card card) {
        switch (card.rank) {
            case ACE:
                return 1;
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            case FIVE:
                return 5;
            case SIX:
                return 6;
            case SEVEN:
                return 7;
            case EIGHT:
                return 8;
            case NINE:
                return 9;
            case TEN:
            case JACK:
            case QUEEN:
            case KING:
                return 10;
            default:
                return 0;
        }
    }

    public int handValue() {
        int total = 0;
        boolean hasAce = false;

        for(Card card : this.hand) {
            total += this.cardValue(card);
            if (card.rank == Rank.ACE) {
                hasAce = true;
            }
        }

        if (hasAce && total + 10 <= 21) {
            return total + 10;
        } else {
            return total;
        }
    }

    public void hit(List<Card> deck) {
        this.hand.add(Card.newRandomCard(deck));
        ((Card)this.hand.get(this.hand.size() - 1)).flipped = true;
    }

    public void stand() {
        this.stood = true;
    }

    public boolean isStood() {
        return this.stood;
    }
}
