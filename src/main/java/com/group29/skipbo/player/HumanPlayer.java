package com.group29.skipbo.player;

import com.group29.skipbo.card.BuildingPile;
import com.group29.skipbo.card.Card;
import com.group29.skipbo.card.DiscardPile;

import java.util.ArrayList;
import java.util.List;


public class HumanPlayer extends Player {

    public HumanPlayer(String name, StockPile stockPile) {
        super(name, stockPile);
    }

    // convenience factory method to create a human player with an empty stock pile.
    public static HumanPlayer create(String name) {
        return new HumanPlayer(name, new StockPile());
    }

    // checks if this player has won (stock pile is empty).
    public boolean hasWon() {
        return getStockPile().isEmpty();
    }

    // checks if the player can play a specific hand card to a building pile.
    public boolean canPlayFromHand(int handIndex, BuildingPile buildingPile) {
        if (handIndex < 0 || handIndex >= getHand().size()) {
            return false;
        }
        Card card = getHand().get(handIndex);
        return buildingPile.canPlay(card);
    }

    // checks if the player can make any play from their hand to a given building pile.
    public boolean canPlayAnyFromHand(BuildingPile buildingPile) {
        for (Card card : getHand().getCards()) {
            if (buildingPile.canPlay(card)) {
                return true;
            }
        }
        return false;
    }

    // checks if the player can play their stock pile card to a given building pile.
    public boolean canPlayFromStock(BuildingPile buildingPile) {
        if (getStockPile().isEmpty()) {
            return false;
        }
        return buildingPile.canPlay(getStockPile().peekTop());
    }

    // checks if the player can play from a specific discard pile to a given building pile.
    public boolean canPlayFromDiscard(int discardPileIndex, BuildingPile buildingPile) {
        if (discardPileIndex < 0 || discardPileIndex >= getDiscardPiles().size()) {
            return false;
        }
        DiscardPile discardPile = getDiscardPile(discardPileIndex);
        if (discardPile.isEmpty()) {
            return false;
        }
        return buildingPile.canPlay(discardPile.getTopCard());
    }

    // checks if the player has cards in hand to discard (required to end turn).
    public boolean canDiscard() {
        return !getHand().isEmpty();
    }

    // finds all building pile indices where the stock pile top card can be played.
    public List<Integer> findPlayableFromStock(BuildingPile[] buildingPiles) {
        List<Integer> playable = new ArrayList<>();
        if (getStockPile().isEmpty()) {
            return playable;
        }

        Card stockCard = getStockPile().peekTop();
        for (int i = 0; i < buildingPiles.length; i++) {
            if (buildingPiles[i].canPlay(stockCard)) {
                playable.add(i);
            }
        }
        return playable;
    }

    // finds all building pile indices where a specific hand card can be played.
    public List<Integer> findPlayableFromHand(int handIndex, BuildingPile[] buildingPiles) {
        List<Integer> playable = new ArrayList<>();
        if (handIndex < 0 || handIndex >= getHand().size()) {
            return playable;
        }

        Card handCard = getHand().get(handIndex);
        for (int i = 0; i < buildingPiles.length; i++) {
            if (buildingPiles[i].canPlay(handCard)) {
                playable.add(i);
            }
        }
        return playable;
    }

    // finds all building pile indices where a discard pile top card can be played.
    public List<Integer> findPlayableFromDiscard(int discardPileIndex, BuildingPile[] buildingPiles) {
        List<Integer> playable = new ArrayList<>();
        if (discardPileIndex < 0 || discardPileIndex >= getDiscardPiles().size()) {
            return playable;
        }

        DiscardPile discardPile = getDiscardPile(discardPileIndex);
        if (discardPile.isEmpty()) {
            return playable;
        }

        Card topCard = discardPile.getTopCard();
        for (int i = 0; i < buildingPiles.length; i++) {
            if (buildingPiles[i].canPlay(topCard)) {
                playable.add(i);
            }
        }
        return playable;
    }

    // checks if the player has any valid move on any building pile.
    public boolean hasAnyValidMove(BuildingPile[] buildingPiles) {
        // Check stock pile
        if (!findPlayableFromStock(buildingPiles).isEmpty()) {
            return true;
        }

        // check hand
        for (int i = 0; i < getHand().size(); i++) {
            if (!findPlayableFromHand(i, buildingPiles).isEmpty()) {
                return true;
            }
        }

        // check discard piles
        for (int i = 0; i < getDiscardPiles().size(); i++) {
            if (!findPlayableFromDiscard(i, buildingPiles).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "HumanPlayer{" +
                "name='" + getName() + '\'' +
                ", handSize=" + getHand().size() +
                ", stockPileSize=" + getStockPile().size() +
                '}';
    }
}
