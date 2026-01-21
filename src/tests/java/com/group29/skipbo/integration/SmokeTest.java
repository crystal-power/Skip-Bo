package com.group29.skipbo.integration;

import com.group29.skipbo.card.*;
import com.group29.skipbo.player.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SmokeTest {

    @Test
    void miniTurn_flowWorks() {
        // Setup
        Deck deck = new Deck();
        // Make deck predictable: draw() takes from top (end), so add in draw order
        deck.addToTop(Card.numbered(CardColor.RED, 5));
        deck.addToTop(Card.numbered(CardColor.GREEN, 4));
        deck.addToTop(Card.skipBo());
        deck.addToTop(Card.numbered(CardColor.BLUE, 2));
        deck.addToTop(Card.numbered(CardColor.RED, 1));

        StockPile stock = new StockPile();
        stock.addToTop(Card.numbered(CardColor.RED, 1));

        Player p = new Player("P1", stock);
        BuildingPile building = new BuildingPile();

        // Start of turn: refill hand to 5 (will draw 5 here)
        p.refillHand(deck);
        assertEquals(5, p.getHand().size());
        assertTrue(deck.isEmpty());

        // Play from stock onto building (stock top is 1)
        p.playFromStock(building);
        assertEquals(2, building.getNextRequiredNumber());
        assertTrue(p.getStockPile().isEmpty());

        // Discard a card from hand to discard pile 0 (ends turn in real rules)
        int before = p.getHand().size();
        p.discardFromHand(0, 0);
        assertEquals(before - 1, p.getHand().size());
        assertNotNull(p.getDiscardPile(0).getTopCard());
    }
}
