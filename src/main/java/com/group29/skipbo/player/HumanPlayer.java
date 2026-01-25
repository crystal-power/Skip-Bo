package com.group29.skipbo.player;

import com.group29.skipbo.card.BuildingPile;
import com.group29.skipbo.card.Card;
import com.group29.skipbo.card.DiscardPile;
import com.group29.skipbo.rules.MoveValidator;

import java.util.List;

// we use this for human players, extends Player
public class HumanPlayer extends Player {

    // we use this to check moves
    private final MoveValidator validator = new MoveValidator();

    public HumanPlayer(String name, StockPile stockPile) {
        super(name, stockPile);
    }

    // we use this to create a human player with empty stock pile
    public static HumanPlayer create(String name) {
        return new HumanPlayer(name, new StockPile());
    }

    // we check if player won (stock pile empty)
    public boolean hasWon() {
        return getStockPile().isEmpty();
    }

    // we use this to check if a hand card can play to building pile
    public boolean canPlayFromHand(int handIndex, BuildingPile buildingPile) {
        return validator.canPlayFromHand(getHand(), handIndex, buildingPile);
    }

    // we use this to check if any hand card can play to building pile
    public boolean canPlayAnyFromHand(BuildingPile buildingPile) {
        for (int i = 0; i < getHand().size(); i++) {
            if (validator.canPlayFromHand(getHand(), i, buildingPile)) {
                return true;
            }
        }
        return false;
    }

    // we use this to check if stock can play to building pile
    public boolean canPlayFromStock(BuildingPile buildingPile) {
        return validator.canPlayFromStock(getStockPile(), buildingPile);
    }

    // we use this to check if discard pile can play to building pile
    public boolean canPlayFromDiscard(int discardPileIndex, BuildingPile buildingPile) {
        return validator.canPlayFromDiscard(getDiscardPiles(), discardPileIndex, buildingPile);
    }

    // we check if player has cards to discard
    public boolean canDiscard() {
        return !getHand().isEmpty();
    }

    // we use this to find which building piles stock can play to
    public List<Integer> findPlayableFromStock(BuildingPile[] buildingPiles) {
        return validator.findPlayableFromStock(getStockPile(), buildingPiles);
    }

    // we use this to find which building piles a hand card can play to
    public List<Integer> findPlayableFromHand(int handIndex, BuildingPile[] buildingPiles) {
        return validator.findPlayableFromHand(getHand(), handIndex, buildingPiles);
    }

    // we use this to find which building piles a discard can play to
    public List<Integer> findPlayableFromDiscard(int discardPileIndex, BuildingPile[] buildingPiles) {
        return validator.findPlayableFromDiscard(getDiscardPiles(), discardPileIndex, buildingPiles);
    }

    // we use this to check if player has any valid move
    public boolean hasAnyValidMove(BuildingPile[] buildingPiles) {
        return validator.hasAnyValidMove(getStockPile(), getHand(), getDiscardPiles(), buildingPiles);
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
