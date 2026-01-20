package com.group29.skipbo.player;

import com.group29.skipbo.card.BuildingPile;
import com.group29.skipbo.card.Card;
import com.group29.skipbo.card.DiscardPile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ComputerPlayer extends Player {

    private final Random random;

    public ComputerPlayer(String name, StockPile stockPile) {
        super(name, stockPile);
        this.random = new Random();
    }


    public static ComputerPlayer create(String name) {
        return new ComputerPlayer(name, new StockPile());
    }


    public boolean hasWon() {
        return getStockPile().isEmpty();
    }

    // plays a full turn automatically.
    // returns true if the turn was completed (ended with a discard).
    public boolean playTurn(BuildingPile[] buildingPiles) {
        while (playOneCard(buildingPiles)) {
            if (hasWon()) {
                return true;
            }
        }
        return discardRandomCard();
    }


     // attempts to play one card from any source (stock, hand, or discard piles).
     // prioritizes stock pile plays since emptying it wins the game.
     // returns true if a card was played.

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

     // tries to play the top card from stock pile onto any building pile.
     // returns true if successful.
    private boolean tryPlayFromStock(BuildingPile[] buildingPiles) {
        if (getStockPile().isEmpty()) {
            return false;
        }

        Card stockCard = getStockPile().peekTop();
        List<Integer> validPiles = findValidBuildingPiles(stockCard, buildingPiles);

        if (validPiles.isEmpty()) {
            return false;
        }

        int pileIndex = validPiles.get(random.nextInt(validPiles.size()));
        playFromStock(buildingPiles[pileIndex]);
        return true;
    }

     // tries to play any card from hand onto any building pile.
     // returns true if successful.
    private boolean tryPlayFromHand(BuildingPile[] buildingPiles) {
        if (getHand().isEmpty()) {
            return false;
        }

        List<int[]> validMoves = new ArrayList<>();

        for (int handIndex = 0; handIndex < getHand().size(); handIndex++) {
            Card card = getHand().get(handIndex);
            List<Integer> validPiles = findValidBuildingPiles(card, buildingPiles);

            for (int pileIndex : validPiles) {
                validMoves.add(new int[]{handIndex, pileIndex});
            }
        }

        if (validMoves.isEmpty()) {
            return false;
        }

        int[] move = validMoves.get(random.nextInt(validMoves.size()));
        playFromHand(move[0], buildingPiles[move[1]]);
        return true;
    }

     // tries to play the top card from any discard pile onto any building pile.
     // returns true if successful.
    private boolean tryPlayFromDiscard(BuildingPile[] buildingPiles) {
        List<int[]> validMoves = new ArrayList<>();

        for (int discardIndex = 0; discardIndex < getDiscardPiles().size(); discardIndex++) {
            DiscardPile discardPile = getDiscardPile(discardIndex);

            if (discardPile.isEmpty()) {
                continue;
            }

            Card topCard = discardPile.getTopCard();
            List<Integer> validPiles = findValidBuildingPiles(topCard, buildingPiles);

            for (int pileIndex : validPiles) {
                validMoves.add(new int[]{discardIndex, pileIndex});
            }
        }

        if (validMoves.isEmpty()) {
            return false;
        }

        int[] move = validMoves.get(random.nextInt(validMoves.size()));
        playFromDiscard(move[0], buildingPiles[move[1]]);
        return true;
    }

     // finds all building piles where a card can be legally played.
    private List<Integer> findValidBuildingPiles(Card card, BuildingPile[] buildingPiles) {
        List<Integer> valid = new ArrayList<>();

        for (int i = 0; i < buildingPiles.length; i++) {
            if (buildingPiles[i].canPlay(card)) {
                valid.add(i);
            }
        }

        return valid;
    }

     // discards a random card from hand to a random discard pile.
     // returns true if successful, false if hand is empty.
    private boolean discardRandomCard() {
        if (getHand().isEmpty()) {
            return false;
        }

        int handIndex = random.nextInt(getHand().size());
        int discardIndex = random.nextInt(getDiscardPiles().size());

        discardFromHand(handIndex, discardIndex);
        return true;
    }

     // checks if the computer has any valid move available.
    public boolean hasAnyValidMove(BuildingPile[] buildingPiles) {
        if (!getStockPile().isEmpty()) {
            Card stockCard = getStockPile().peekTop();
            if (!findValidBuildingPiles(stockCard, buildingPiles).isEmpty()) {
                return true;
            }
        }

        for (int i = 0; i < getHand().size(); i++) {
            Card card = getHand().get(i);
            if (!findValidBuildingPiles(card, buildingPiles).isEmpty()) {
                return true;
            }
        }

        for (int i = 0; i < getDiscardPiles().size(); i++) {
            DiscardPile pile = getDiscardPile(i);
            if (!pile.isEmpty()) {
                Card topCard = pile.getTopCard();
                if (!findValidBuildingPiles(topCard, buildingPiles).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
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
