package com.group29.skipbo.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private static final int CARDS_PER_NUMBER_PER_COLOR = 3;
    private static final int SKIP_BO_COUNT = 18;

    private final List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    // creates a 126 card deck
    public static Deck createStandardDeck() {
        Deck deck = new Deck();

        for (CardColor color : CardColor.values()) {
            if (color == CardColor.SKIPBO) continue;

            for (int number = 1; number <= 12; number++) {
                for (int i = 0; i < CARDS_PER_NUMBER_PER_COLOR; i++) {
                    deck.cards.add(Card.numbered(color, number));
                }
            }
        }

        for (int i = 0; i < SKIP_BO_COUNT; i++) {
            deck.cards.add(Card.skipBo());
        }

        deck.shuffle();
        return deck;
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card draw() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot draw from empty deck");
        }
        return cards.remove(cards.size() - 1);
    }

    public List<Card> draw(int count) {
        if (count > size()) {
            throw new IllegalStateException(
                    "Cannot draw " + count + " cards, only " + size() + " available"
            );
        }

        List<Card> drawn = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            drawn.add(draw());
        }
        return drawn;
    }

    public void addToBottom(List<Card> cardsToAdd) {
        cards.addAll(0, cardsToAdd);
    }

    public void addToTop(Card card) {
        cards.add(card);
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public boolean hasCards(int count) {
        return size() >= count;
    }

}
