//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hoke.games.gameLib.blackJack;

import com.hoke.games.assets.Card;
import java.util.List;

public class BJDealer extends BJPlayer {
    public BJDealer(List<Card> deck) {
        super(deck);
    }

    public void dealerHand(List<Card> deck) {
        if (this.handValue() > 16) {
            this.stand();
        } else {
            this.hit(deck);
        }

    }
}
