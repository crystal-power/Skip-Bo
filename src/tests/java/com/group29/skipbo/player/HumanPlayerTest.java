// src/test/java/com/group29/skipbo/player/HumanPlayerTest.java

package com.group29.skipbo.player;

import com.group29.skipbo.card.BuildingPile;
import com.group29.skipbo.card.Card;
import com.group29.skipbo.card.CardColor;
import com.group29.skipbo.card.DiscardPile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HumanPlayerTest {

    private HumanPlayer player;
    private BuildingPile[] buildingPiles;

    @BeforeEach
    void setUp() {
        player = HumanPlayer.create("Alice");
        buildingPiles = new BuildingPile[4];
        for (int i = 0; i < 4; i++) {
            buildingPiles[i] = new BuildingPile();
        }
    }

    // ==================== Constructor Tests ====================

    @Test
    void create_shouldCreatePlayerWithEmptyStockPile() {
        HumanPlayer newPlayer = HumanPlayer.create("Bob");

        assertEquals("Bob", newPlayer.getName());
        assertTrue(newPlayer.getStockPile().isEmpty());
        assertTrue(newPlayer.getHand().isEmpty());
        assertEquals(4, newPlayer.getDiscardPiles().size());
    }

    @Test
    void constructor_shouldCreatePlayerWithGivenStockPile() {
        StockPile stock = new StockPile();
        stock.addToTop(Card.numbered(CardColor.RED, 5));
        stock.addToTop(Card.numbered(CardColor.BLUE, 3));

        HumanPlayer newPlayer = new HumanPlayer("Charlie", stock);

        assertEquals("Charlie", newPlayer.getName());
        assertEquals(2, newPlayer.getStockPile().size());
    }

    // ==================== hasWon Tests ====================

    @Test
    void hasWon_shouldReturnTrueWhenStockPileEmpty() {
        // Stock pile is empty by default with create()
        assertTrue(player.hasWon());
    }

    @Test
    void hasWon_shouldReturnFalseWhenStockPileHasCards() {
        player.getStockPile().addToTop(Card.numbered(CardColor.RED, 5));

        assertFalse(player.hasWon());
    }

    // ==================== canPlayFromHand Tests ====================

    @Test
    void canPlayFromHand_shouldReturnTrueWhenCardMatchesPile() {
        player.getHand().add(Card.numbered(CardColor.RED, 1));

        assertTrue(player.canPlayFromHand(0, buildingPiles[0]));
    }

    @Test
    void canPlayFromHand_shouldReturnFalseWhenCardDoesNotMatch() {
        player.getHand().add(Card.numbered(CardColor.RED, 5));

        assertFalse(player.canPlayFromHand(0, buildingPiles[0])); // Pile expects 1
    }

    @Test
    void canPlayFromHand_shouldReturnTrueForSkipBoCard() {
        player.getHand().add(Card.skipBo());

        assertTrue(player.canPlayFromHand(0, buildingPiles[0])); // SkipBo matches anything
    }

    @Test
    void canPlayFromHand_shouldReturnFalseForInvalidIndex() {
        player.getHand().add(Card.numbered(CardColor.RED, 1));

        assertFalse(player.canPlayFromHand(-1, buildingPiles[0]));
        assertFalse(player.canPlayFromHand(5, buildingPiles[0]));
    }

    // ==================== canPlayAnyFromHand Tests ====================

    @Test
    void canPlayAnyFromHand_shouldReturnTrueWhenAtLeastOneCardMatches() {
        player.getHand().add(Card.numbered(CardColor.RED, 5));
        player.getHand().add(Card.numbered(CardColor.BLUE, 1));
        player.getHand().add(Card.numbered(CardColor.GREEN, 9));

        assertTrue(player.canPlayAnyFromHand(buildingPiles[0])); // Card(1) matches
    }

    @Test
    void canPlayAnyFromHand_shouldReturnFalseWhenNoCardMatches() {
        player.getHand().add(Card.numbered(CardColor.RED, 5));
        player.getHand().add(Card.numbered(CardColor.BLUE, 7));

        assertFalse(player.canPlayAnyFromHand(buildingPiles[0])); // Pile expects 1
    }

    @Test
    void canPlayAnyFromHand_shouldReturnFalseWhenHandEmpty() {
        assertFalse(player.canPlayAnyFromHand(buildingPiles[0]));
    }

    // ==================== canPlayFromStock Tests ====================

    @Test
    void canPlayFromStock_shouldReturnTrueWhenStockCardMatches() {
        player.getStockPile().addToTop(Card.numbered(CardColor.RED, 1));

        assertTrue(player.canPlayFromStock(buildingPiles[0]));
    }

    @Test
    void canPlayFromStock_shouldReturnFalseWhenStockCardDoesNotMatch() {
        player.getStockPile().addToTop(Card.numbered(CardColor.RED, 5));

        assertFalse(player.canPlayFromStock(buildingPiles[0])); // Pile expects 1
    }

    @Test
    void canPlayFromStock_shouldReturnFalseWhenStockEmpty() {
        assertFalse(player.canPlayFromStock(buildingPiles[0]));
    }

    @Test
    void canPlayFromStock_shouldReturnTrueForSkipBo() {
        player.getStockPile().addToTop(Card.skipBo());

        assertTrue(player.canPlayFromStock(buildingPiles[0]));
    }

    // ==================== canPlayFromDiscard Tests ====================

    @Test
    void canPlayFromDiscard_shouldReturnTrueWhenTopCardMatches() {
        player.getDiscardPile(0).discard(Card.numbered(CardColor.RED, 1));

        assertTrue(player.canPlayFromDiscard(0, buildingPiles[0]));
    }

    @Test
    void canPlayFromDiscard_shouldReturnFalseWhenTopCardDoesNotMatch() {
        player.getDiscardPile(0).discard(Card.numbered(CardColor.RED, 5));

        assertFalse(player.canPlayFromDiscard(0, buildingPiles[0]));
    }

    @Test
    void canPlayFromDiscard_shouldReturnFalseWhenDiscardPileEmpty() {
        assertFalse(player.canPlayFromDiscard(0, buildingPiles[0]));
    }

    @Test
    void canPlayFromDiscard_shouldReturnFalseForInvalidIndex() {
        assertFalse(player.canPlayFromDiscard(-1, buildingPiles[0]));
        assertFalse(player.canPlayFromDiscard(4, buildingPiles[0]));
    }

    // ==================== canDiscard Tests ====================

    @Test
    void canDiscard_shouldReturnTrueWhenHandHasCards() {
        player.getHand().add(Card.numbered(CardColor.RED, 5));

        assertTrue(player.canDiscard());
    }

    @Test
    void canDiscard_shouldReturnFalseWhenHandEmpty() {
        assertFalse(player.canDiscard());
    }

    // ==================== findPlayableFromStock Tests ====================

    @Test
    void findPlayableFromStock_shouldReturnMatchingPileIndices() {
        player.getStockPile().addToTop(Card.numbered(CardColor.RED, 1));

        List<Integer> playable = player.findPlayableFromStock(buildingPiles);

        // All empty piles expect 1, so all should match
        assertEquals(4, playable.size());
        assertTrue(playable.contains(0));
        assertTrue(playable.contains(1));
        assertTrue(playable.contains(2));
        assertTrue(playable.contains(3));
    }

    @Test
    void findPlayableFromStock_shouldReturnEmptyListWhenNoMatch() {
        player.getStockPile().addToTop(Card.numbered(CardColor.RED, 5));

        List<Integer> playable = player.findPlayableFromStock(buildingPiles);

        assertTrue(playable.isEmpty());
    }

    @Test
    void findPlayableFromStock_shouldReturnEmptyListWhenStockEmpty() {
        List<Integer> playable = player.findPlayableFromStock(buildingPiles);

        assertTrue(playable.isEmpty());
    }

    // ==================== findPlayableFromHand Tests ====================

    @Test
    void findPlayableFromHand_shouldReturnMatchingPileIndices() {
        player.getHand().add(Card.numbered(CardColor.RED, 1));

        List<Integer> playable = player.findPlayableFromHand(0, buildingPiles);

        assertEquals(4, playable.size()); // All piles expect 1
    }

    @Test
    void findPlayableFromHand_shouldReturnEmptyListForInvalidIndex() {
        player.getHand().add(Card.numbered(CardColor.RED, 1));

        List<Integer> playable = player.findPlayableFromHand(5, buildingPiles);

        assertTrue(playable.isEmpty());
    }

    // ==================== findPlayableFromDiscard Tests ====================

    @Test
    void findPlayableFromDiscard_shouldReturnMatchingPileIndices() {
        player.getDiscardPile(2).discard(Card.numbered(CardColor.RED, 1));

        List<Integer> playable = player.findPlayableFromDiscard(2, buildingPiles);

        assertEquals(4, playable.size());
    }

    @Test
    void findPlayableFromDiscard_shouldReturnEmptyListWhenDiscardEmpty() {
        List<Integer> playable = player.findPlayableFromDiscard(0, buildingPiles);

        assertTrue(playable.isEmpty());
    }

    // ==================== hasAnyValidMove Tests ====================

    @Test
    void hasAnyValidMove_shouldReturnTrueWhenStockCanPlay() {
        player.getStockPile().addToTop(Card.numbered(CardColor.RED, 1));

        assertTrue(player.hasAnyValidMove(buildingPiles));
    }

    @Test
    void hasAnyValidMove_shouldReturnTrueWhenHandCanPlay() {
        player.getHand().add(Card.numbered(CardColor.RED, 1));

        assertTrue(player.hasAnyValidMove(buildingPiles));
    }

    @Test
    void hasAnyValidMove_shouldReturnTrueWhenDiscardCanPlay() {
        player.getDiscardPile(0).discard(Card.numbered(CardColor.RED, 1));

        assertTrue(player.hasAnyValidMove(buildingPiles));
    }

    @Test
    void hasAnyValidMove_shouldReturnFalseWhenNoValidMoves() {
        // Add non-matching cards
        player.getStockPile().addToTop(Card.numbered(CardColor.RED, 5));
        player.getHand().add(Card.numbered(CardColor.BLUE, 7));
        player.getDiscardPile(0).discard(Card.numbered(CardColor.GREEN, 9));

        assertFalse(player.hasAnyValidMove(buildingPiles));
    }

    @Test
    void hasAnyValidMove_shouldReturnFalseWhenAllSourcesEmpty() {
        assertFalse(player.hasAnyValidMove(buildingPiles));
    }

    // ==================== toString Tests ====================

    @Test
    void toString_shouldContainPlayerInfo() {
        player.getHand().add(Card.numbered(CardColor.RED, 1));
        player.getStockPile().addToTop(Card.numbered(CardColor.BLUE, 5));

        String result = player.toString();

        assertTrue(result.contains("Alice"));
        assertTrue(result.contains("handSize=1"));
        assertTrue(result.contains("stockPileSize=1"));
    }
}
