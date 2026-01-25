package com.group29.skipbo.player;

import com.group29.skipbo.card.BuildingPile;
import com.group29.skipbo.card.Card;
import com.group29.skipbo.card.DiscardPile;
import com.group29.skipbo.rules.MoveValidator;

import java.util.List;
import java.util.Random;

// we use this for computer players, extends Player
public class ComputerPlayer extends Player {

    private final Random random;
    // we use this to check moves
    private final MoveValidator moveValidator;

    public ComputerPlayer(String name, StockPile stockPile) {
        super(name, stockPile);
        this.random = new Random();
        this.moveValidator = new MoveValidator();
    }

    // we use this to create computer player with empty stock pile
    public static ComputerPlayer create(String name) {
        return new ComputerPlayer(name, new StockPile());
    }

    // we check if computer won
    public boolean hasWon() {
        return getStockPile().isEmpty();
    }

    // we use this to play a full turn automatically
    // returns true if turn was completed
    public boolean playTurn(BuildingPile[] buildingPiles) {
        while (playOneCard(buildingPiles)) {
            if (hasWon()) {
                return true;
            }
        }
        return discardRandomCard();
    }

    // we try to play one card from any source
    // prioritizes stock because emptying it wins
    public boolean playOneCard(BuildingPile[] buildingPiles) {
        if (tryPlayFromStock(buildingPiles)) {
            return true;
        }
        if (tryPlayFromHand(buildingPiles)) {
            return true;
        }
        if (tryPlayFromDiscard(buildingPiles)) {
            return true;
        }
        return false;
    }

    // we try to play from stock pile
    private boolean tryPlayFromStock(BuildingPile[] buildingPiles) {
        if (getStockPile().isEmpty()) {
            return false;
        }

        List<Integer> validPiles = moveValidator.findPlayableFromStock(getStockPile(), buildingPiles);

        if (validPiles.isEmpty()) {
            return false;
        }

        int pileIndex = validPiles.get(random.nextInt(validPiles.size()));
        playFromStock(buildingPiles[pileIndex]);
        return true;
    }

    // we try to play from hand
    private boolean tryPlayFromHand(BuildingPile[] buildingPiles) {
        if (getHand().isEmpty()) {
            return false;
        }

        // we check each hand card to find valid moves
        for (int handIndex = 0; handIndex < getHand().size(); handIndex++) {
            List<Integer> validPiles = moveValidator.findPlayableFromHand(getHand(), handIndex, buildingPiles);

            if (!validPiles.isEmpty()) {
                int pileIndex = validPiles.get(random.nextInt(validPiles.size()));
                playFromHand(handIndex, buildingPiles[pileIndex]);
                return true;
            }
        }
        return false;
    }

    // we try to play from discard piles
    private boolean tryPlayFromDiscard(BuildingPile[] buildingPiles) {
        // we check each discard pile
        for (int discardIndex = 0; discardIndex < getDiscardPiles().size(); discardIndex++) {
            if (getDiscardPile(discardIndex).isEmpty()) {
                continue;
            }

            List<Integer> validPiles = moveValidator.findPlayableFromDiscard(
                    getDiscardPiles(), discardIndex, buildingPiles);

            if (!validPiles.isEmpty()) {
                int pileIndex = validPiles.get(random.nextInt(validPiles.size()));
                playFromDiscard(discardIndex, buildingPiles[pileIndex]);
                return true;
            }
        }
        return false;
    }

    // we discard a random card to end turn
    private boolean discardRandomCard() {
        if (getHand().isEmpty()) {
            return false;
        }

        int handIndex = random.nextInt(getHand().size());
        int discardIndex = random.nextInt(getDiscardPiles().size());

        discardFromHand(handIndex, discardIndex);
        return true;
    }

    // we check if computer has any valid move
    public boolean hasAnyValidMove(BuildingPile[] buildingPiles) {
        return moveValidator.hasAnyValidMove(getStockPile(), getHand(), getDiscardPiles(), buildingPiles);
    }

    @Override
    public String toString() {
        return "ComputerPlayer{" +
                "name='" + getName() + '\'' +
                ", handSize=" + getHand().size() +
                ", stockPileSize=" + getStockPile().size() +
                '}';
    }
}
