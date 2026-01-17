package com.group29.skipbo.card;

import java.util.ArrayDeque;
import java.util.Deque;

public class BuildingPile {
    private static final int MAX_VALUE = 12;

    private final Deque<Card> cards = new ArrayDeque<>();
    private int currentValue = 0; // 0 means empty pile (next required is 1)
    private boolean justCompleted = false; // true only immediately after a 12 is played

    public int getNextRequiredNumber() {
        return currentValue + 1;
    }

    /** DiscardPile-style: returns null if empty */
    public Card getTopCard() {
        return cards.peek(); // null if empty
    }

    public boolean canPlay(Card card) {
        if (card == null) return false;

        int required = getNextRequiredNumber();
        if (card.isSkipBo()) return true;
        return card.getNumber() == required;
    }

    public void play(Card card) {
        if (!canPlay(card)) {
            throw new IllegalStateException("Illegal move: cannot play card here.");
        }

        // start new sequence, reset the completion flag
        justCompleted = false;

        cards.push(card);
        currentValue++;

        if (currentValue == MAX_VALUE) {
            justCompleted = true; // observable by UI if needed
            clear();
        }
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * True if the pile hit 12 on the most recent play().
     * (Because we auto-clear immediately.)
     */
    public boolean isComplete() {
        return justCompleted;
    }

    private void clear() {
        cards.clear();
        currentValue = 0;
    }
}