package com.hoke.games.gameLib.poker;

import com.hoke.games.assets.Card;

import java.util.*;

public class HandScoring {

    public enum HandType {
        HIGHCARD,
        ONEPAIR,
        TWOPAIR,
        THREEOFAKIND,
        STRAIGHT,
        FLUSH,
        FULLHOUSE,
        FOUROFAKIND,
        STRAIGHTFLUSH,
        ROYALFLUSH
    }
    public static class ScoredHand {
        /*
        rankingValues Structure
        Hand Type            rankingValues Content

        HIGHCARD             [14,13,10,6,5], 5 highest cards
        ONEPAIR              [value of pair, top kickers] 3 highest kickers
        TWOPAIR              [Higher pair, Lower pair, kicker]
        THREEOFAKIND         [value of triplet, kickers] 2 highest kickers
        STRAIGHT             [highest card in straight]
        FLUSH                [sorted descending cards in flush]
        FULLHOUSE            [triplet value, pair value, kicker]
        FOUROFAKIND          [quadruplet value, kicker]
        STRIAGHTFLUSH        [highest card in straight]
        ROYALFLUSH           [14]
        */

        public final HandScoring.HandType handType;
        public final List<Integer> rankingValues;

        public ScoredHand(HandScoring.HandType handType, List<Integer> rankingValues) {
            this.handType = handType;
            this.rankingValues = rankingValues;
        }
    }

    public static ScoredHand scoreHand(List<Card> hand, List<Card> communityCards) {
        List<Card> fullHand = new ArrayList<>();
        fullHand.addAll(hand);
        fullHand.addAll(communityCards);
        fullHand.sort(Comparator.comparingInt(Poker::cardValue).reversed());

        if(isRoyalFLush(fullHand))  {
            return new ScoredHand(HandType.ROYALFLUSH, List.of(14));
        }
        if(isStraightFlush(fullHand)) {
            int high = getStraightHigh(fullHand);
            return new ScoredHand(HandType.STRAIGHTFLUSH, List.of(high));
        }
        if(isFourOfAKind(fullHand)) {
            int[] valueCount = countValues(fullHand);
            int quad = getHighestOfCount(valueCount, 4);
            int kicker = getHighestExcluding(valueCount, quad);
            return new ScoredHand(HandType.FOUROFAKIND, List.of(quad, kicker));
        }
        if(isFullHouse(fullHand)) {
            int[] valueCount = countValues(fullHand);
            int triple = getHighestOfCount(valueCount, 3);
            valueCount[triple] = 0;
            int pair = getHighestOfCount(valueCount, 2);
            return new ScoredHand(HandType.FULLHOUSE, List.of(triple, pair));
        }
        if(isFlush(fullHand)) {
            List<Integer> flushValues = getFlushValues(fullHand);
            return new ScoredHand(HandType.FLUSH, flushValues);
        }
        if(isStraight(fullHand))  {
            int high = getStraightHigh(fullHand);
            return new ScoredHand(HandType.STRAIGHT, List.of(high));
        }
        if(isThreeOfAKind(fullHand)) {
            int[] valueCount = countValues(fullHand);
            int triple = getHighestOfCount(valueCount, 3);
            List<Integer> kickers = getTopKickers(valueCount,triple,2);
            List<Integer> result = new ArrayList<>();
            result.add(triple);
            result.addAll(kickers);
            return new ScoredHand(HandType.THREEOFAKIND, result);
        }
        if(isTwoPair(fullHand)) {
            int[] valueCount = countValues(fullHand);
            int highPair = getHighestOfCount(valueCount, 2);
            valueCount[highPair] = 0;
            int lowPair = getHighestOfCount(valueCount, 2);
            valueCount[lowPair] = 0;
            int kicker = getHighestOfCount(valueCount,1);
            return new ScoredHand(HandType.TWOPAIR, List.of(highPair, lowPair, kicker));
        }
        if(isOnePair(fullHand)) {
            int[] valueCount = countValues(fullHand);
            int pair = getHighestOfCount(valueCount, 1);
            valueCount[pair] = 0;
            List<Integer> result = new ArrayList<>();
            List<Integer> kickers = getTopKickers(valueCount,pair,3);
            result.add(pair);
            result.addAll(kickers);
            return new ScoredHand(HandType.ONEPAIR, result);
        }
        //high cards
        int[] valueCount = countValues(fullHand);
        List<Integer> highCards = getTopKickers(valueCount,-1,5);
        return new ScoredHand(HandType.HIGHCARD, highCards);

    }

    private static boolean isRoyalFLush(List<Card> fullHand) {

        if(isStraightFlush(fullHand)) {
            Set<Integer> royalValues = Set.of(10,11,12,13,14);
            Set<Integer> handValues = new HashSet<>();
            for(Card card : fullHand) {
                handValues.add(Poker.cardValue(card));
            }
            return handValues.containsAll(royalValues);
        }
        return false;

    }
    private static boolean isStraightFlush(List<Card> fullHand) {
        Map<Card.Suit, List<Card>> suitGroups = new HashMap<>();

        for (Card card : fullHand) {
            suitGroups.computeIfAbsent(card.suit, k -> new ArrayList<>()).add(card);
        }

        for (List<Card> suitedCards : suitGroups.values()) {
            if (suitedCards.size() >= 5) {
                if (isStraight(suitedCards)) {
                    return true;
                }
            }
        }

        return false;
    }
    private static boolean isFourOfAKind(List<Card> fullHand) {
        int[] valueCount = new int[15];
        for(Card card : fullHand) {
            valueCount[Poker.cardValue(card)]++;
            if(valueCount[Poker.cardValue(card)] >= 4) return true;
        }
        return false;
    }
    private static boolean isFullHouse(List<Card> fullHand) {
        int[] valueCount = new int[15];
        for(Card card : fullHand) {
            valueCount[Poker.cardValue(card)]++;
        }
        boolean hasThreeOfAKind = false;
        boolean hasPair = false;

        for (int i = 14; i >= 2; i--) {
            if (valueCount[i] >= 3) {
                hasThreeOfAKind = true;
                // Reduce count to avoid reusing in pair check
                valueCount[i] -= 3;
                break;
            }
        }

        for (int i = 14; i >= 2; i--) {
            if (valueCount[i] >= 2) {
                hasPair = true;
                break;
            }
        }

        return hasThreeOfAKind && hasPair;

    }
    private static boolean isFlush(List<Card> fullHand) {
        int[] suitCounts = new int[4]; // Assuming 4 suits, and Suit.ordinal() is 0–3
        for (Card card : fullHand) {
            suitCounts[card.suit.ordinal()]++;
            if (suitCounts[card.suit.ordinal()] >= 5) {
                return true;
            }
        }
        return false;
    }
    private static boolean isStraight(List<Card> fullHand) {
        List<Integer>  values = new ArrayList<>();
        for(Card card: fullHand) {//adds unique values to the list
            int val = Poker.cardValue(card);
            if(!values.contains(val)) {
                values.add(val);
            }
        }
        //handle special ACE case, A,2,3,4,5
        if(values.contains(14)) {
            values.add(1);
            Collections.sort(values, Collections.reverseOrder());
        }

        int consecutive = 1;//straight determination algorithm.
        for(int i = 0; i < values.size(); i++) {
            if(values.get(i) == values.get(i-1) -1) {
                consecutive++;
                if(consecutive == 5)  return true;
            } else {
                consecutive = 1;
            }
        }
        return false;
    }
    private static boolean isThreeOfAKind(List<Card> fullHand) {
        int[] valueCount = new int[15];
        for (Card card : fullHand) {
            valueCount[Poker.cardValue(card)]++;
        }
        for(int count : valueCount) {
            if(count>=3) return true;
        }
        return false;
    }
    private static boolean isTwoPair(List<Card> fullHand) {
        int[] valueCount = new int[15];
        for (Card card : fullHand) {
            valueCount[Poker.cardValue(card)]++;
        }
        int pairCount = 0;
        for (int count : valueCount) {
            if (count >= 2) pairCount++;
        }
        return pairCount >= 2;

    }
    private static boolean isOnePair(List<Card> fullHand) {
            int[] valueCount = new int[15];
            for (Card card : fullHand) {
                valueCount[Poker.cardValue(card)]++;
            }
            for (int count : valueCount) {
                if (count >= 2) return true;
            }
            return false;

    }


    private static int[] countValues(List<Card> fullHand) {
        int[] valueCount = new int[15];
        for (Card card : fullHand) {
            valueCount[Poker.cardValue(card)]++;
        }
        return valueCount;
    } //prod
    private static int getHighestOfCount(int[] valueCount, int count) {
        for(int i = 14; i >= 2; i--) {
            if(valueCount[i] >= count) {
                return i;
            }
        }
        return -1;
    }
    private static int getHighestExcluding(int[] valueCount, int exclude){
        for(int i = 14; i >= 2; i--) {
            if(i!=exclude && valueCount[i] > 0) return i;
        }
        return -1;
    }
    private static List<Integer> getTopKickers(int[] valueCount, int exclude, int count){
        List<Integer> topKickers = new ArrayList<>();
        for(int i = 14; i >= 2 && topKickers.size() < count; i--) {
            if(i != exclude && valueCount[i] > 0) topKickers.add(i);
        }
        return topKickers;
    }
    private static List<Integer> getFlushValues(List<Card> fullHand){
        Map<Card.Suit, List<Integer>> suits = new HashMap<>();
        for(Card card : fullHand) {
            suits.computeIfAbsent(card.suit, k -> new ArrayList<>()).add(Poker.cardValue(card));
        }
        for(List<Integer> cards : suits.values()) {
            if(cards.size() >= 5) {
                cards.sort(Collections.reverseOrder());
                return cards.subList(0, 5);
            }
        }
        return List.of();
    }
    private static int getStraightHigh(List<Card> hand){
        Set<Integer> values = new TreeSet<>();
        for(Card card : hand) {
            values.add(Poker.cardValue(card));
        }
        if(values.contains(14)) values.add(1);

        List<Integer> list = new ArrayList<>(values);
        int consecutive =1;
        for(int i = 1; i < list.size(); i++) {
            if(list.get(i) == list.get(i-1)-1) {
                consecutive++;
                if(consecutive == 5) return list.get(i-4);
            } else {
                consecutive = 1;
            }
        }
        return list.get(0);
    }
}
