package com.group29.skipbo.player;

import com.group29.skipbo.card.Card;
import com.group29.skipbo.card.CardColor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockPileTest {

    @Test
    void emptyPile_isEmptyAndSizeZero() {
        StockPile pile = new StockPile();
        assertTrue(pile.isEmpty());
        assertEquals(0, pile.size());
    }

    @Test
    void peekOnEmpty_throws() {
        StockPile pile = new StockPile();
        assertThrows(IllegalStateException.class, pile::peekTop);
    }

    @Test
    void drawOnEmpty_throws() {
        StockPile pile = new StockPile();
        assertThrows(IllegalStateException.class, pile::drawTop);
    }

    @Test
    void addToBottom_thenPeekShowsTop() {
        StockPile pile = new StockPile();
        pile.addToBottom(Card.numbered(CardColor.RED, 3));
        assertEquals(1, pile.size());
        assertFalse(pile.isEmpty());
        assertEquals(3, pile.peekTop().getNumber());
    }

    @Test
    void constructorOrder_lastCardBecomesTop() {
        Card c1 = Card.numbered(CardColor.RED, 1);
        Card c2 = Card.numbered(CardColor.GREEN, 2);
        Card c3 = Card.numbered(CardColor.BLUE, 3);

        StockPile pile = new StockPile(List.of(c1, c2, c3));

        assertEquals(3, pile.size());
        assertEquals(3, pile.peekTop().getNumber()); // last is top
    }

    @Test
    void drawRemovesFromTop() {
        StockPile pile = new StockPile();
        pile.addToTop(Card.numbered(CardColor.RED, 1));
        pile.addToTop(Card.numbered(CardColor.RED, 2));

        assertEquals(2, pile.drawTop().getNumber());
        assertEquals(1, pile.drawTop().getNumber());
        assertTrue(pile.isEmpty());
    }

    @Test
    void addToTop_overridesTop() {
        StockPile pile = new StockPile();
        pile.addToBottom(Card.numbered(CardColor.RED, 5));
        pile.addToTop(Card.numbered(CardColor.GREEN, 9));

        assertEquals(9, pile.peekTop().getNumber());
    }
}
