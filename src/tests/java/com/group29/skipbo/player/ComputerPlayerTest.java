// src/test/java/com/group29/skipbo/player/ComputerPlayerTest.java

package com.group29.skipbo.player;

import com.group29.skipbo.card.BuildingPile;
import com.group29.skipbo.card.Card;
import com.group29.skipbo.card.CardColor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComputerPlayerTest {

    private ComputerPlayer computer;
    private BuildingPile[] buildingPiles;

    @BeforeEach
    void setUp() {
        computer = ComputerPlayer.create("Bot");
        buildingPiles = new BuildingPile[4];
        for (int i = 0; i < 4; i++) {
            buildingPiles[i] = new BuildingPile();
        }
    }

    // ==================== Constructor Tests ====================

    @Test
    void create_shouldCreateComputerWithEmptyStockPile() {
        ComputerPlayer bot = ComputerPlayer.create("TestBot");

        assertEquals("TestBot", bot.getName());
        assertTrue(bot.getStockPile().isEmpty());
        assertTrue(bot.getHand().isEmpty());
        assertEquals(4, bot.getDiscardPiles().size());
    }

    @Test
    void constructor_shouldCreateComputerWithGivenStockPile() {
        StockPile stock = new StockPile();
        stock.addToTop(Card.numbered(CardColor.RED, 5));

        ComputerPlayer bot = new ComputerPlayer("TestBot", stock);

        assertEquals(1, bot.getStockPile().size());
    }

    // ==================== hasWon Tests ====================

    @Test
    void hasWon_shouldReturnTrueWhenStockPileEmpty() {
        assertTrue(computer.hasWon());
    }

    @Test
    void hasWon_shouldReturnFalseWhenStockPileHasCards() {
        computer.getStockPile().addToTop(Card.numbered(CardColor.RED, 5));

        assertFalse(computer.hasWon());
    }

    // ==================== playOneCard Tests ====================

    @Test
    void playOneCard_shouldPlayFromStockWhenPossible() {
        computer.getStockPile().addToTop(Card.numbered(CardColor.RED, 1));
        int initialStockSize = computer.getStockPile().size();

        boolean played = computer.playOneCard(buildingPiles);

        assertTrue(played);
        assertEquals(initialStockSize - 1, computer.getStockPile().size());
    }

    @Test
    void playOneCard_shouldPlayFromHandWhenStockCantPlay() {
        computer.getStockPile().addToTop(Card.numbered(CardColor.RED, 5)); // Can't play
        computer.getHand().add(Card.numbered(CardColor.BLUE, 1)); // Can play
        int initialHandSize = computer.getHand().size();

        boolean played = computer.playOneCard(buildingPiles);

        assertTrue(played);
        assertEquals(initialHandSize - 1, computer.getHand().size());
    }

    @Test
    void playOneCard_shouldPlayFromDiscardWhenOthersCannotPlay() {
        computer.getStockPile().addToTop(Card.numbered(CardColor.RED, 5)); // Can't play
        computer.getHand().add(Card.numbered(CardColor.BLUE, 7)); // Can't play
        computer.getDiscardPile(0).discard(Card.numbered(CardColor.GREEN, 1)); // Can play

        boolean played = computer.playOneCard(buildingPiles);

        assertTrue(played);
        assertTrue(computer.getDiscardPile(0).isEmpty());
    }

    @Test
    void playOneCard_shouldReturnFalseWhenNoValidMoves() {
        computer.getStockPile().addToTop(Card.numbered(CardColor.RED, 5));
        computer.getHand().add(Card.numbered(CardColor.BLUE, 7));

        boolean played = computer.playOneCard(buildingPiles);

        assertFalse(played);
    }

    @Test
    void playOneCard_shouldPlaySkipBoFromStock() {
        computer.getStockPile().addToTop(Card.skipBo());

        boolean played = computer.playOneCard(buildingPiles);

        assertTrue(played);
        assertTrue(computer.getStockPile().isEmpty());
    }

    // ==================== playTurn Tests ====================

    @Test
    void playTurn_shouldReturnTrueWhenWinning() {
        // Stock pile has card that can be played
        computer.getStockPile().addToTop(Card.numbered(CardColor.RED, 1));
        computer.getHand().add(Card.numbered(CardColor.BLUE, 5)); // In hand for discarding

        boolean result = computer.playTurn(buildingPiles);

        assertTrue(result);
        assertTrue(computer.hasWon());
    }

    @Test
    void playTurn_shouldDiscardWhenNoMovesAvailable() {
        computer.getStockPile().addToTop(Card.numbered(CardColor.RED, 5)); // Can't play
        computer.getHand().add(Card.numbered(CardColor.BLUE, 7)); // Can't play

        computer.playTurn(buildingPiles);

        // Card should be discarded
        assertTrue(computer.getHand().isEmpty());
        assertEquals(1, countDiscardedCards());
    }

    // ==================== hasAnyValidMove Tests ====================

    @Test
    void hasAnyValidMove_shouldReturnTrueWhenStockCanPlay() {
        computer.getStockPile().addToTop(Card.numbered(CardColor.RED, 1));

        assertTrue(computer.hasAnyValidMove(buildingPiles));
    }

    @Test
    void hasAnyValidMove_shouldReturnTrueWhenHandCanPlay() {
        computer.getHand().add(Card.numbered(CardColor.RED, 1));

        assertTrue(computer.hasAnyValidMove(buildingPiles));
    }

    @Test
    void hasAnyValidMove_shouldReturnTrueWhenDiscardCanPlay() {
        computer.getDiscardPile(0).discard(Card.numbered(CardColor.RED, 1));

        assertTrue(computer.hasAnyValidMove(buildingPiles));
    }

    @Test
    void hasAnyValidMove_shouldReturnFalseWhenNoValidMoves() {
        computer.getStockPile().addToTop(Card.numbered(CardColor.RED, 5));
        computer.getHand().add(Card.numbered(CardColor.BLUE, 7));

        assertFalse(computer.hasAnyValidMove(buildingPiles));
    }

    @Test
    void hasAnyValidMove_shouldReturnFalseWhenAllEmpty() {
        assertFalse(computer.hasAnyValidMove(buildingPiles));
    }

    // ==================== Priority Tests ====================

    @Test
    void playOneCard_shouldPrioritizeStockOverHand() {
        // Both stock and hand can play
        computer.getStockPile().addToTop(Card.numbered(CardColor.RED, 1));
        computer.getHand().add(Card.numbered(CardColor.BLUE, 1));

        computer.playOneCard(buildingPiles);

        // Stock should be played first (priority)
        assertTrue(computer.getStockPile().isEmpty());
        assertEquals(1, computer.getHand().size()); // Hand untouched
    }

    @Test
    void playOneCard_shouldPrioritizeHandOverDiscard() {
        // Stock can't play, but hand and discard can
        computer.getStockPile().addToTop(Card.numbered(CardColor.RED, 5)); // Can't play
        computer.getHand().add(Card.numbered(CardColor.BLUE, 1)); // Can play
        computer.getDiscardPile(0).discard(Card.numbered(CardColor.GREEN, 1)); // Can play

        computer.playOneCard(buildingPiles);

        // Hand should be played before discard
        assertTrue(computer.getHand().isEmpty());
        assertFalse(computer.getDiscardPile(0).isEmpty()); // Discard untouched
    }

    // ==================== Edge Cases ====================

    @Test
    void playTurn_shouldHandleEmptyHand() {
        // No cards anywhere except stock that can't play
        computer.getStockPile().addToTop(Card.numbered(CardColor.RED, 5));

        boolean result = computer.playTurn(buildingPiles);

        // Can't discard with empty hand
        assertFalse(result);
    }

    @Test
    void playOneCard_shouldHandleAllEmptyPiles() {
        // Nothing to play from
        boolean played = computer.playOneCard(buildingPiles);

        assertFalse(played);
    }

    @Test
    void playTurn_shouldHandleSkipBoCards() {
        computer.getStockPile().addToTop(Card.skipBo());
        computer.getHand().add(Card.numbered(CardColor.RED, 5)); // For discard

        computer.playTurn(buildingPiles);

        assertTrue(computer.getStockPile().isEmpty());
    }

    // ==================== toString Tests ====================

    @Test
    void toString_shouldContainComputerInfo() {
        computer.getHand().add(Card.numbered(CardColor.RED, 1));
        computer.getStockPile().addToTop(Card.numbered(CardColor.BLUE, 5));

        String result = computer.toString();

        assertTrue(result.contains("Bot"));
        assertTrue(result.contains("handSize=1"));
        assertTrue(result.contains("stockPileSize=1"));
    }

    // ==================== Helper Methods ====================

    private int countDiscardedCards() {
        int total = 0;
        for (int i = 0; i < computer.getDiscardPiles().size(); i++) {
            total += computer.getDiscardPile(i).size();
        }
        return total;
    }
}
