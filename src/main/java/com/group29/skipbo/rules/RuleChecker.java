package com.group29.skipbo.rules;

import com.group29.skipbo.card.BuildingPile;
import com.group29.skipbo.game.Game;
import com.group29.skipbo.game.GameState;
import com.group29.skipbo.player.Player;

import java.util.Objects;

// we use this to check if moves are allowed based on game rules
public class RuleChecker {

    private final MoveValidator moveValidator;

    public RuleChecker() {
        this.moveValidator = new MoveValidator();
    }

    public RuleChecker(MoveValidator moveValidator) {
        this.moveValidator = Objects.requireNonNull(moveValidator, "moveValidator");
    }

    // we check if game is currently in progress
    public boolean isGameInProgress(Game game) {
        Objects.requireNonNull(game, "game");
        return game.getState() == GameState.IN_PROGRESS;
    }

    // we check if game is waiting for players
    public boolean isWaitingForPlayers(Game game) {
        Objects.requireNonNull(game, "game");
        return game.getState() == GameState.WAITING_FOR_PLAYERS;
    }

    // we check if game is over (round over or game over)
    public boolean isGameOver(Game game) {
        Objects.requireNonNull(game, "game");
        GameState state = game.getState();
        return state == GameState.ROUND_OVER || state == GameState.GAME_OVER;
    }

    // we check if its this players turn
    public boolean isPlayerTurn(Game game, Player player) {
        Objects.requireNonNull(game, "game");
        Objects.requireNonNull(player, "player");

        // can only check turn when game is running
        if (!isGameInProgress(game)) {
            return false;
        }

        return game.isPlayerTurn(player);
    }

    // we check if player is in the game
    public boolean isPlayerInGame(Game game, Player player) {
        Objects.requireNonNull(game, "game");
        Objects.requireNonNull(player, "player");

        return game.getPlayers().contains(player);
    }

    // we check if player can play from stock to building pile
    public boolean canPlayFromStock(Game game, Player player, int buildingPileIndex) {
        if (!canPlayerAct(game, player)) {
            return false;
        }

        if (!isValidBuildingPileIndex(buildingPileIndex)) {
            return false;
        }

        BuildingPile buildingPile = game.getBuildingPile(buildingPileIndex);
        return moveValidator.canPlayFromStock(player.getStockPile(), buildingPile);
    }

    // we check if player can play from hand to building pile
    public boolean canPlayFromHand(Game game, Player player, int handIndex, int buildingPileIndex) {
        if (!canPlayerAct(game, player)) {
            return false;
        }

        if (!isValidBuildingPileIndex(buildingPileIndex)) {
            return false;
        }

        BuildingPile buildingPile = game.getBuildingPile(buildingPileIndex);
        return moveValidator.canPlayFromHand(player.getHand(), handIndex, buildingPile);
    }

    // we check if player can play from discard to building pile
    public boolean canPlayFromDiscard(Game game, Player player, int discardPileIndex, int buildingPileIndex) {
        if (!canPlayerAct(game, player)) {
            return false;
        }

        if (!isValidBuildingPileIndex(buildingPileIndex)) {
            return false;
        }

        if (!isValidDiscardPileIndex(discardPileIndex)) {
            return false;
        }

        BuildingPile buildingPile = game.getBuildingPile(buildingPileIndex);
        return moveValidator.canPlayFromDiscard(player.getDiscardPiles(), discardPileIndex, buildingPile);
    }

    // we check if player can discard from hand to end turn
    public boolean canDiscard(Game game, Player player, int handIndex, int discardPileIndex) {
        if (!canPlayerAct(game, player)) {
            return false;
        }

        if (!isValidDiscardPileIndex(discardPileIndex)) {
            return false;
        }

        // check hand index is valid
        if (handIndex < 0 || handIndex >= player.getHand().size()) {
            return false;
        }

        return true;
    }

    // we check if player has any card to discard
    public boolean canEndTurn(Game game, Player player) {
        if (!canPlayerAct(game, player)) {
            return false;
        }

        return !player.getHand().isEmpty();
    }

    // we check if player has won (stock pile empty)
    public boolean hasPlayerWon(Player player) {
        Objects.requireNonNull(player, "player");
        return player.getStockPile().isEmpty();
    }

    // we check if any player has won, returns winner or null
    public Player checkForWinner(Game game) {
        Objects.requireNonNull(game, "game");

        // can only have winner when game is running
        if (!isGameInProgress(game)) {
            return null;
        }

        for (Player player : game.getPlayers()) {
            if (hasPlayerWon(player)) {
                return player;
            }
        }

        return null;
    }

    // we check if player has any valid move
    public boolean hasAnyValidMove(Game game, Player player) {
        if (!canPlayerAct(game, player)) {
            return false;
        }

        return moveValidator.hasAnyValidMove(
                player.getStockPile(),
                player.getHand(),
                player.getDiscardPiles(),
                game.getBuildingPiles());
    }

    // we check if player must discard (no other moves)
    public boolean mustDiscard(Game game, Player player) {
        if (!canPlayerAct(game, player)) {
            return false;
        }

        return !hasAnyValidMove(game, player) && !player.getHand().isEmpty();
    }

    // we check if player can be added to game
    public boolean canAddPlayer(Game game) {
        Objects.requireNonNull(game, "game");

        if (!isWaitingForPlayers(game)) {
            return false;
        }

        return game.getPlayerCount() < Game.MAX_PLAYERS;
    }

    // we check if player can act (game running and their turn)
    private boolean canPlayerAct(Game game, Player player) {
        Objects.requireNonNull(game, "game");
        Objects.requireNonNull(player, "player");

        // game must be running
        if (!isGameInProgress(game)) {
            return false;
        }

        // player must be in game
        if (!isPlayerInGame(game, player)) {
            return false;
        }

        // must be their turn
        return isPlayerTurn(game, player);
    }

    // we check if building pile index is valid (0-3)
    private boolean isValidBuildingPileIndex(int index) {
        return index >= 0 && index < Game.NUM_BUILDING_PILES;
    }

    // we check if discard pile index is valid (0-3)
    private boolean isValidDiscardPileIndex(int index) {
        return index >= 0 && index < 4;
    }

    public MoveValidator getMoveValidator() {
        return moveValidator;
    }
}