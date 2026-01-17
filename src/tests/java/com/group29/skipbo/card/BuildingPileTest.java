package com.group29.skipbo.card;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuildingPileTest {

    @Test
    void emptyPile_requiresOne() {
        BuildingPile pile = new BuildingPile();

        assertEquals(1, pile.getNextRequiredNumber());
        assertTrue(pile.isEmpty());
    }

    @Test
    void canPlayOneOnEmptyPile() {
        BuildingPile pile = new BuildingPile();
        Card one = Card.numbered(CardColor.RED, 1);

        assertTrue(pile.canPlay(one));
    }

    @Test
    void cannotPlayTwoOnEmptyPile() {
        BuildingPile pile = new BuildingPile();
        Card two = Card.numbered(CardColor.BLUE, 2);

        assertFalse(pile.canPlay(two));
    }

    @Test
    void skipBoCanAlwaysBePlayed() {
        BuildingPile pile = new BuildingPile();
        Card skipBo = Card.skipBo();

        assertTrue(pile.canPlay(skipBo));
    }

    @Test
    void playingCardsInOrderWorks() {
        BuildingPile pile = new BuildingPile();

        pile.play(Card.numbered(CardColor.RED, 1));
        assertEquals(2, pile.getNextRequiredNumber());

        pile.play(Card.numbered(CardColor.GREEN, 2));
        assertEquals(3, pile.getNextRequiredNumber());
    }

    @Test
    void illegalMoveThrowsException() {
        BuildingPile pile = new BuildingPile();
        Card three = Card.numbered(CardColor.RED, 3);

        assertThrows(IllegalStateException.class, () -> {
            pile.play(three);
        });
    }

    @Test
    void skipBoAdvancesPile() {
        BuildingPile pile = new BuildingPile();

        pile.play(Card.numbered(CardColor.RED, 1));
        pile.play(Card.skipBo());

        assertEquals(3, pile.getNextRequiredNumber());
    }

    @Test
    void pileClearsAfterTwelve() {
        BuildingPile pile = new BuildingPile();

        for (int i = 1; i <= 12; i++) {
            pile.play(Card.numbered(CardColor.BLUE, i));
        }

        assertTrue(pile.isEmpty());
        assertEquals(1, pile.getNextRequiredNumber());
    }
}