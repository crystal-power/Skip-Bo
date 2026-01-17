package com.group29.skipbo.card;

import java.util.ArrayList;
import java.util.List;

public class DiscardPile {

    private final List<Card> cards;

    public DiscardPile() {
        this.cards = new ArrayList<>();
    }

    public Card getTopCard() {
        if (isEmpty()) {
            return null;
        }
        return cards.get(cards.size()- 1);
    }

    public void discard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        cards.add(card);
    }

    public Card playTopCard() {
        if (isEmpty()) {
            throw new IllegalStateException("Discard pile is empty");
        }
        return cards.remove(cards.size()- 1);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

}
