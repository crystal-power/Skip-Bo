package com.group29.skipbo.player;

import com.group29.skipbo.card.BuildingPile;
import com.group29.skipbo.card.Card;
import com.group29.skipbo.card.CardColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTurnActionsTest {

    @Test
    void playFromHand_successful() {
        Player player = new Player("P1", new StockPile());
        BuildingPile building = new BuildingPile();

        player.getHand().add(Card.numbered(CardColor.RED, 1));
        player.getHand().add(Card.numbered(CardColor.GREEN, 5));

        player.playFromHand(0, building);

        // After playing 1, the next required number is 2
        assertEquals(2, building.getNextRequiredNumber());
        assertEquals(1, player.getHand().size());
        assertEquals(5, player.getHand().get(0).getNumber());
    }

    @Test
    void playFromHand_illegalMove_throwsAndDoesNotRemove() {
        Player player = new Player("P1", new StockPile());
        BuildingPile building = new BuildingPile();

        player.getHand().add(Card.numbered(CardColor.RED, 3));

        assertThrows(IllegalStateException.class, () -> player.playFromHand(0, building));
        assertEquals(1, player.getHand().size());
    }

    @Test
    void discardFromHand_movesCardToDiscardPile() {
        Player player = new Player("P1", new StockPile());

        player.getHand().add(Card.numbered(CardColor.BLUE, 7));
        player.discardFromHand(0, 2);

        assertTrue(player.getHand().isEmpty());
        assertEquals(7, player.getDiscardPile(2).getTopCard().getNumber());
    }

    @Test
    void playFromDiscard_successful() {
        Player player = new Player("P1", new StockPile());
        BuildingPile building = new BuildingPile();

        // Put a 1 on discard pile 0
        player.getDiscardPile(0).discard(Card.numbered(CardColor.RED, 1));

        player.playFromDiscard(0, building);

        assertTrue(player.getDiscardPile(0).isEmpty());
        assertEquals(2, building.getNextRequiredNumber());
    }

    @Test
    void playFromDiscard_emptyPile_throws() {
        Player player = new Player("P1", new StockPile());
        BuildingPile building = new BuildingPile();

        assertThrows(IllegalStateException.class, () -> player.playFromDiscard(0, building));
    }

    @Test
    void playFromStock_successful() {
        // StockPile top depends on your StockPile convention.
        // We'll build it so top is definitely 1 by using addToTop.
        StockPile stock = new StockPile();
        stock.addToTop(Card.numbered(CardColor.RED, 1));

        Player player = new Player("P1", stock);
        BuildingPile building = new BuildingPile();

        player.playFromStock(building);

        assertTrue(player.getStockPile().isEmpty());
        assertEquals(2, building.getNextRequiredNumber());
    }

    @Test
    void playFromStock_illegalMove_throws() {
        StockPile stock = new StockPile();
        stock.addToTop(Card.numbered(CardColor.RED, 5));

        Player player = new Player("P1", stock);
        BuildingPile building = new BuildingPile();

        assertThrows(IllegalStateException.class, () -> player.playFromStock(building));
        assertEquals(1, player.getStockPile().size());
    }
}