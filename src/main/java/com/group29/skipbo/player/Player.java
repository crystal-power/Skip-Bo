package com.group29.skipbo.player;

import com.group29.skipbo.card.BuildingPile;
import com.group29.skipbo.card.Card;
import com.group29.skipbo.card.Deck;
import com.group29.skipbo.card.DiscardPile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {

    private static final int DISCARD_PILE_COUNT = 4;

    private final String name;
    private final Hand hand;
    private final StockPile stockPile;
    private final List<DiscardPile> discardPiles;

    public Player(String name, StockPile stockPile) {
        this.name = Objects.requireNonNull(name, "name");
        this.stockPile = Objects.requireNonNull(stockPile, "stockPile");
        this.hand = new Hand();

        this.discardPiles = new ArrayList<>();
        for (int i = 0; i < DISCARD_PILE_COUNT; i++) {
            discardPiles.add(new DiscardPile());
        }
    }

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    public StockPile getStockPile() {
        return stockPile;
    }

    public List<DiscardPile> getDiscardPiles() {
        return discardPiles;
    }

    public DiscardPile getDiscardPile(int index) {
        if (index < 0 || index >= discardPiles.size()) {
            throw new IllegalArgumentException("Invalid discard pile index: " + index);
        }
        return discardPiles.get(index);
    }

    public void refillHand(Deck deck) {
        Objects.requireNonNull(deck, "deck");
        while (!hand.isFull()) {
            if (deck.isEmpty()) {
                throw new IllegalStateException("Deck is empty; cannot refill hand");
            }
            hand.add(deck.draw());
        }
    }

    // Turn action methods
    /**
     * Play a card from the player's hand onto a building pile.
     */
    public void playFromHand(int handIndex, BuildingPile buildingPile) {
        Objects.requireNonNull(buildingPile, "buildingPile");

        Card card = hand.get(handIndex); // throws IndexOutOfBounds if invalid index
        if (!buildingPile.canPlay(card)) {
            throw new IllegalStateException("Cannot play that hand card onto this building pile");
        }

        // Remove then play
        hand.removeAt(handIndex);
        buildingPile.play(card);
    }

    /**
     * Play the top card of the player's stock pile onto a building pile.
     */
    public void playFromStock(BuildingPile buildingPile) {
        Objects.requireNonNull(buildingPile, "buildingPile");

        Card card = stockPile.peekTop(); // throws if empty
        if (!buildingPile.canPlay(card)) {
            throw new IllegalStateException("Cannot play stock card onto this building pile");
        }

        stockPile.drawTop();
        buildingPile.play(card);
    }

    /**
     * Play the top card of one of the player's discard piles onto a building pile.
     */
    public void playFromDiscard(int discardPileIndex, BuildingPile buildingPile) {
        Objects.requireNonNull(buildingPile, "buildingPile");

        DiscardPile pile = getDiscardPile(discardPileIndex);
        Card card = pile.getTopCard();

        if (card == null) {
            throw new IllegalStateException("Discard pile is empty");
        }
        if (!buildingPile.canPlay(card)) {
            throw new IllegalStateException("Cannot play discard card onto this building pile");
        }

        pile.playTopCard();
        buildingPile.play(card);
    }

    /**
     * Discard a card from hand onto one of the player's discard piles (usually ends the turn).
     */
    public void discardFromHand(int handIndex, int discardPileIndex) {
        DiscardPile pile = getDiscardPile(discardPileIndex);
        Card card = hand.removeAt(handIndex);
        pile.discard(card);
    }
}