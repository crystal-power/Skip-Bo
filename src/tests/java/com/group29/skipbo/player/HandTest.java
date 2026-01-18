package com.group29.skipbo.player;

import com.group29.skipbo.card.Card;
import com.group29.skipbo.card.CardColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    @Test
    void newHand_isEmptyAndNotFull() {
        Hand hand = new Hand();
        assertTrue(hand.isEmpty());
        assertEquals(0, hand.size());
        assertFalse(hand.isFull());
        assertEquals(Hand.DEFAULT_MAX_SIZE, hand.getMaxSize());
    }

    @Test
    void addNull_throws() {
        Hand hand = new Hand();
        assertThrows(IllegalArgumentException.class, () -> hand.add(null));
    }

    @Test
    void addCards_untilFull() {
        Hand hand = new Hand(2);

        hand.add(Card.numbered(CardColor.RED, 1));
        assertFalse(hand.isFull());

        hand.add(Card.numbered(CardColor.GREEN, 2));
        assertTrue(hand.isFull());
        assertEquals(2, hand.size());
    }

    @Test
    void addWhenFull_throws() {
        Hand hand = new Hand(1);
        hand.add(Card.numbered(CardColor.RED, 1));

        assertThrows(IllegalStateException.class, () ->
                hand.add(Card.numbered(CardColor.BLUE, 2)));
    }

    @Test
    void getByIndex_returnsCorrectCard() {
        Hand hand = new Hand(5);
        Card c1 = Card.numbered(CardColor.RED, 3);
        Card c2 = Card.skipBo();

        hand.add(c1);
        hand.add(c2);

        assertSame(c1, hand.get(0));
        assertSame(c2, hand.get(1));
    }

    @Test
    void removeAt_removesAndReturns() {
        Hand hand = new Hand(5);
        Card c1 = Card.numbered(CardColor.RED, 3);
        Card c2 = Card.numbered(CardColor.GREEN, 4);

        hand.add(c1);
        hand.add(c2);

        Card removed = hand.removeAt(0);
        assertSame(c1, removed);
        assertEquals(1, hand.size());
        assertSame(c2, hand.get(0));
    }

    @Test
    void clear_emptiesHand() {
        Hand hand = new Hand(5);
        hand.add(Card.numbered(CardColor.RED, 1));
        hand.add(Card.skipBo());

        hand.clear();
        assertTrue(hand.isEmpty());
        assertEquals(0, hand.size());
    }
}
