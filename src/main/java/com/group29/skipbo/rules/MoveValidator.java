package com.group29.skipbo.rules;

import com.group29.skipbo.card.BuildingPile;
import com.group29.skipbo.card.Card;
import com.group29.skipbo.card.DiscardPile;
import com.group29.skipbo.player.Hand;
import com.group29.skipbo.player.StockPile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Central place for checking whether moves are possible/valid.
 * This is a lightweight validator: it checks legality (pile rules, indexes, emptiness).
 * Turn/state rules can be handled by RuleChecker or controller.
 */
public class MoveValidator {
    // move from a source to a building pile.
    public record Move(Source source, int sourceIndex, int buildingIndex) {
        public enum Source { STOCK, HAND, DISCARD }
    }

    //check if card can be placed on building pile
    public boolean canPlayCardToBuilding(Card card, BuildingPile buildingPile) {
        return card != null && buildingPile != null && buildingPile.canPlay(card);
    }

   //check if stock can play to building pile
    public boolean canPlayFromStock(StockPile stockPile, BuildingPile buildingPile) {
        Objects.requireNonNull(stockPile, "stockPile");
        Objects.requireNonNull(buildingPile, "buildingPile");
        if (stockPile.isEmpty()) return false;
        return buildingPile.canPlay(stockPile.peekTop());
    }

    //check if specific hand index can be placed onto this building pile
    public boolean canPlayFromHand(Hand hand, int handIndex, BuildingPile buildingPile) {
        Objects.requireNonNull(hand, "hand");
        Objects.requireNonNull(buildingPile, "buildingPile");
        if (handIndex < 0 || handIndex >= hand.size()) return false;
        Card card = hand.get(handIndex);
        return buildingPile.canPlay(card);
    }

    // check if u can play from a specific discard pile on this building pile
    public boolean canPlayFromDiscard(List<DiscardPile> discardPiles, int discardIndex, BuildingPile buildingPile) {
        Objects.requireNonNull(discardPiles, "discardPiles");
        Objects.requireNonNull(buildingPile, "buildingPile");
        if (discardIndex < 0 || discardIndex >= discardPiles.size()) return false;

        DiscardPile pile = discardPiles.get(discardIndex);
        if (pile.isEmpty()) return false;

        Card top = pile.getTopCard();
        return buildingPile.canPlay(top);
    }

    // finds all building pile indices where stock top can be played
    public List<Integer> findPlayableFromStock(StockPile stockPile, BuildingPile[] buildingPiles) {
        Objects.requireNonNull(stockPile, "stockPile");
        Objects.requireNonNull(buildingPiles, "buildingPiles");

        List<Integer> playable = new ArrayList<>();
        if (stockPile.isEmpty()) return playable;

        Card stockCard = stockPile.peekTop();
        for (int i = 0; i < buildingPiles.length; i++) {
            if (buildingPiles[i] != null && buildingPiles[i].canPlay(stockCard)) {
                playable.add(i);
            }
        }
        return playable;
    }

    // finds all building pile indices where a given hand card can be played
    public List<Integer> findPlayableFromHand(Hand hand, int handIndex, BuildingPile[] buildingPiles) {
        Objects.requireNonNull(hand, "hand");
        Objects.requireNonNull(buildingPiles, "buildingPiles");

        List<Integer> playable = new ArrayList<>();
        if (handIndex < 0 || handIndex >= hand.size()) return playable;

        Card card = hand.get(handIndex);
        for (int i = 0; i < buildingPiles.length; i++) {
            if (buildingPiles[i] != null && buildingPiles[i].canPlay(card)) {
                playable.add(i);
            }
        }
        return playable;
    }

    //finds all building pile indices where the top discard card can be played
    public List<Integer> findPlayableFromDiscard(List<DiscardPile> discardPiles, int discardIndex, BuildingPile[] buildingPiles) {
        Objects.requireNonNull(discardPiles, "discardPiles");
        Objects.requireNonNull(buildingPiles, "buildingPiles");

        List<Integer> playable = new ArrayList<>();
        if (discardIndex < 0 || discardIndex >= discardPiles.size()) return playable;

        DiscardPile pile = discardPiles.get(discardIndex);
        if (pile.isEmpty()) return playable;

        Card top = pile.getTopCard();
        for (int i = 0; i < buildingPiles.length; i++) {
            if (buildingPiles[i] != null && buildingPiles[i].canPlay(top)) {
                playable.add(i);
            }
        }
        return playable;
    }

    //true if there exists any valid play from stock/hand/discard to any building pile
    public boolean hasAnyValidMove(StockPile stockPile, Hand hand, List<DiscardPile> discards, BuildingPile[] buildingPiles) {
        if (!findPlayableFromStock(stockPile, buildingPiles).isEmpty()) return true;

        for (int i = 0; i < hand.size(); i++) {
            if (!findPlayableFromHand(hand, i, buildingPiles).isEmpty()) return true;
        }

        for (int i = 0; i < discards.size(); i++) {
            if (!findPlayableFromDiscard(discards, i, buildingPiles).isEmpty()) return true;
        }

        return false;
    }

    /**
     * Collect all currently possible moves, can be prioritized by caller.
     */
    public List<Move> allPossibleMoves(StockPile stockPile, Hand hand, List<DiscardPile> discards, BuildingPile[] buildingPiles) {
        Objects.requireNonNull(stockPile);
        Objects.requireNonNull(hand);
        Objects.requireNonNull(discards);
        Objects.requireNonNull(buildingPiles);

        List<Move> moves = new ArrayList<>();

        // stock moves
        for (int b : findPlayableFromStock(stockPile, buildingPiles)) {
            moves.add(new Move(Move.Source.STOCK, -1, b));
        }

        // hand moves
        for (int h = 0; h < hand.size(); h++) {
            for (int b : findPlayableFromHand(hand, h, buildingPiles)) {
                moves.add(new Move(Move.Source.HAND, h, b));
            }
        }

        // discard moves
        for (int d = 0; d < discards.size(); d++) {
            for (int b : findPlayableFromDiscard(discards, d, buildingPiles)) {
                moves.add(new Move(Move.Source.DISCARD, d, b));
            }
        }

        return moves;
    }
}