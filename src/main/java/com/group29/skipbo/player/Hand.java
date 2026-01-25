package com.group29.skipbo.player;

import com.group29.skipbo.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hand {

    public static final int DEFAULT_MAX_SIZE = 5;
    private final int maxSize;
    private final List<Card> cards;

    public Hand() {
        this(DEFAULT_MAX_SIZE);
    }

    public Hand(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be > 0");
        }
        this.maxSize = maxSize;
        this.cards = new ArrayList<>();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public boolean isFull() {
        return cards.size() >= maxSize;
    }

    /**
     * Read-only view (for UI/tests)
     */
    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public Card get(int index) {
        return cards.get(index); // throws IndexOutOfBoundsException normally
    }

    /**
     * Adds a card to the hand (fails if already full).
     */
    public void add(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        if (isFull()) {
            throw new IllegalStateException("Hand is full");
        }
        cards.add(card);
    }

    /**
     * Removes and returns the card at a position (for "play card i").
     */
    public Card removeAt(int index) {
        return cards.remove(index);
    }

    /**
     * Removes a specific card instance if present.
     */
    public boolean remove(Card card) {
        return cards.remove(card);
    }

    /**
     * Clears the hand (end of game / reset).
     */
    public void clear() {
        cards.clear();
    }
}
