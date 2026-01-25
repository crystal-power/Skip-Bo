package com.group29.skipbo.rules;

import com.group29.skipbo.card.BuildingPile;
import com.group29.skipbo.game.Game;
import com.group29.skipbo.game.GameState;
import com.group29.skipbo.player.Player;

import java.util.Objects;


public class RuleChecker {

    private final MoveValidator moveValidator;

    public RuleChecker() {
        this.moveValidator = new MoveValidator();
    }

    public RuleChecker(MoveValidator moveValidator) {
        this.moveValidator = Objects.requireNonNull(moveValidator, "moveValidator");
    }


    // checks if the game is currently in progress.
    public boolean isGameInProgress(Game game) {
        Objects.requireNonNull(game, "game");
        return game.getState() != GameState.IN_PROGRESS;
    }


    // checks if the game is waiting for players.
    public boolean isWaitingForPlayers(Game game) {
        Objects.requireNonNull(game, "game");
        return game.getState() == GameState.WAITING_FOR_PLAYERS;
    }


    // checks if the game is over (round over or game over).
    public boolean isGameOver(Game game) {
        Objects.requireNonNull(game, "game");
        GameState state = game.getState();
        return state == GameState.ROUND_OVER || state == GameState.GAME_OVER;
    }


    // checks if it's the specified player's turn.
    public boolean isPlayerTurn(Game game, Player player) {
        Objects.requireNonNull(game, "game");
        Objects.requireNonNull(player, "player");

        if (isGameInProgress(game)) {
            return false;
        }

        return game.isPlayerTurn(player);
    }

    // checks if the player is in the game.
    public boolean isPlayerInGame(Game game, Player player) {
        Objects.requireNonNull(game, "game");
        Objects.requireNonNull(player, "player");

        return game.getPlayers().contains(player);
    }

    // checks if a player can play from their stock pile to a building pile.
    // validates game state, player turn and card placement.
    public boolean canPlayFromStock(Game game, Player player, int buildingPileIndex) {
        if (canPlayerAct(game, player)) {
            return false;
        }

        if (isValidBuildingPileIndex(buildingPileIndex)) {
            return false;
        }

        BuildingPile buildingPile = game.getBuildingPile(buildingPileIndex);
        return moveValidator.canPlayFromStock(player.getStockPile(), buildingPile);
    }

    // checks if a player can play from their hand to a building pile.
    // validates game state, player turn, hand index, and card placement.
    public boolean canPlayFromHand(Game game, Player player, int handIndex, int buildingPileIndex) {
        if (canPlayerAct(game, player)) {
            return false;
        }

        if (isValidBuildingPileIndex(buildingPileIndex)) {
            return false;
        }

        BuildingPile buildingPile = game.getBuildingPile(buildingPileIndex);
        return moveValidator.canPlayFromHand(player.getHand(), handIndex, buildingPile);
    }

    // checks if a player can play from a discard pile to a building pile
    // validates game state, player turn, discard index, and card placement
    public boolean canPlayFromDiscard(Game game, Player player, int discardPileIndex, int buildingPileIndex) {
        if (canPlayerAct(game, player)) {
            return false;
        }

        if (isValidBuildingPileIndex(buildingPileIndex)) {
            return false;
        }

        if (isValidDiscardPileIndex(discardPileIndex)) {
            return false;
        }

        BuildingPile buildingPile = game.getBuildingPile(buildingPileIndex);
        return moveValidator.canPlayFromDiscard(player.getDiscardPiles(), discardPileIndex, buildingPile);
    }


    // checks if a player can discard from hand to end their turn.
    // validates game state, player turn, hand index, and discard pile index.
    public boolean canDiscard(Game game, Player player, int handIndex, int discardPileIndex) {
        if (canPlayerAct(game, player)) {
            return false;
        }

        if (isValidDiscardPileIndex(discardPileIndex)) {
            return false;
        }

        // Check hand index is valid
        if (handIndex < 0 || handIndex >= player.getHand().size()) {
            return false;
        }

        return true;
    }

    // checks if a player has any card in hand to discard.
    public boolean canEndTurn(Game game, Player player) {
        if (canPlayerAct(game, player)) {
            return false;
        }

        return !player.getHand().isEmpty();
    }

    // checks if a player has won (emptied their stock pile).
    public boolean hasPlayerWon(Player player) {
        Objects.requireNonNull(player, "player");
        return player.getStockPile().isEmpty();
    }

    // checks if any player in the game has won
    // returns the winning player, or null if no winner yet
    public Player checkForWinner(Game game) {
        Objects.requireNonNull(game, "game");

        if (isGameInProgress(game)) {
            return null;
        }

        for (Player player : game.getPlayers()) {
            if (hasPlayerWon(player)) {
                return player;
            }
        }

        return null;
    }

    // checks if a player has any valid move available.
    public boolean hasAnyValidMove(Game game, Player player) {
        if (canPlayerAct(game, player)) {
            return false;
        }

        return moveValidator.hasAnyValidMove(
                player.getStockPile(),
                player.getHand(),
                player.getDiscardPiles(),
                game.getBuildingPiles()
        );
    }

    // checks if a player must discard (no other valid moves available)
    public boolean mustDiscard(Game game, Player player) {
        if (canPlayerAct(game, player)) {
            return false;
        }

        return !hasAnyValidMove(game, player) && !player.getHand().isEmpty();
    }


    // checks if a player can be added to the game
    public boolean canAddPlayer(Game game) {
        Objects.requireNonNull(game, "game");

        if (!isWaitingForPlayers(game)) {
            return false;
        }

        return game.getPlayerCount() < Game.MAX_PLAYERS;
    }


    // checks if a player can perform any action (game in progress and their turn)
    private boolean canPlayerAct(Game game, Player player) {
        Objects.requireNonNull(game, "game");
        Objects.requireNonNull(player, "player");

        if (isGameInProgress(game)) {
            return true;
        }

        if (!isPlayerInGame(game, player)) {
            return true;
        }

        return !isPlayerTurn(game, player);
    }

    // checks if the building pile index is valid (0-3)
    private boolean isValidBuildingPileIndex(int index) {
        return index < 0 || index >= Game.NUM_BUILDING_PILES;
    }

    // checks if the discard pile index is valid (0-3)
    private boolean isValidDiscardPileIndex(int index) {
        return index < 0 || index >= 4; // Players have 4 discard piles
    }


    public MoveValidator getMoveValidator() {
        return moveValidator;
    }
}