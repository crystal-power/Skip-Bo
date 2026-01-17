package com.group29.skipbo.player;

import com.group29.skipbo.card.Card;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Objects;

public class StockPile {

    private final Deque<Card> cards = new ArrayDeque<>();

    /**
     * Create an empty stockpile.
     */
    public StockPile() {}

    /**
     * Create a stockpile from an initial set of cards.
     * The LAST card in the collection becomes the TOP of the pile.
     */
    public StockPile(Collection<Card> initialCards) {
        Objects.requireNonNull(initialCards, "initialCards");
        for (Card c : initialCards) {
            addToTop(c);
        }
    }

    /**
     * Adds a card to the bottom of the stockpile (used when dealing/building the pile).
     */
    public void addToBottom(Card card) {
        Objects.requireNonNull(card, "card");
        cards.addLast(card);
    }

    /**
     * Adds a card to the top of the stockpile (rarely used; included for completeness).
     */
    public void addToTop(Card card) {
        Objects.requireNonNull(card, "card");
        cards.addFirst(card);
    }

    /**
     * Returns the top card without removing it.
     */
    public Card peekTop() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Stock pile is empty");
        }
        return cards.peekFirst();
    }

    /**
     * Removes and returns the top card.
     */
    public Card drawTop() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Stock pile is empty");
        }
        return cards.removeFirst();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }
}