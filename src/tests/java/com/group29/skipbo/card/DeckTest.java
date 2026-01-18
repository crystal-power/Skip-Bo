package com.group29.skipbo.card;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    @Test
    void standardDeck_has126Cards() {
        Deck deck = Deck.createStandardDeck();
        assertEquals(126, deck.size());
    }

    @Test
    void draw_reducesSize() {
        Deck deck = new Deck();
        deck.addToTop(Card.numbered(CardColor.RED, 1));
        assertEquals(1, deck.size());
        deck.draw();
        assertEquals(0, deck.size());
        assertTrue(deck.isEmpty());
    }
}
