package com.group29.skipbo.server;

import com.group29.skipbo.card.BuildingPile;
import com.group29.skipbo.card.Card;
import com.group29.skipbo.card.DiscardPile;
import com.group29.skipbo.game.Game;
import com.group29.skipbo.game.GameState;
import com.group29.skipbo.player.HumanPlayer;
import com.group29.skipbo.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

// we use this as the Controller in MVC
// it manages the game state and handles all game logic
public class GameController {

    // we track players waiting for a game
    private final Map<String, ClientHandler> waitingPlayers;
    private int requestedPlayerCount;

    // we track the actual game
    private Game game;
    private final Map<String, ClientHandler> gamePlayers;
    private final Map<String, Player> playerObjects;

    public GameController() {
        this.waitingPlayers = new HashMap<>();
        this.gamePlayers = new HashMap<>();
        this.playerObjects = new HashMap<>();
        this.requestedPlayerCount = 0;
        this.game = null;
    }

    // ===== PLAYER REGISTRATION =====

    // we register a new player, returns error code or null if success
    public synchronized String registerPlayer(String name, ClientHandler handler) {
        // we check if name is already taken
        if (waitingPlayers.containsKey(name) || gamePlayers.containsKey(name)) {
            return "002"; // NAME_IN_USE
        }
        waitingPlayers.put(name, handler);
        return null;
    }

    // we remove a player who disconnected
    public synchronized void removePlayer(String name) {
        waitingPlayers.remove(name);
        gamePlayers.remove(name);
        playerObjects.remove(name);
        // we could handle mid-game disconnection here
    }

    // ===== GAME REQUEST =====

    // we handle a player requesting a game
    public synchronized String requestGame(String name, int playerCount, ClientHandler handler) {
        // we validate player count
        if (playerCount < 2 || playerCount > 6) {
            return "204"; // INVALID_COMMAND
        }

        // we check if a game is already running
        if (game != null && game.getState() == GameState.IN_PROGRESS) {
            return "205"; // COMMAND_NOT_ALLOWED
        }

        // we set or validate requested player count
        if (requestedPlayerCount == 0) {
            requestedPlayerCount = playerCount;
        }

        // we send QUEUE to this player
        handler.send("QUEUE");

        // we check if we have enough players
        if (waitingPlayers.size() >= requestedPlayerCount) {
            startGame();
        }

        return null;
    }

    // we start the game when we have enough players
    private void startGame() {
        game = new Game();

        // we take players from waiting list
        List<String> playerNames = new ArrayList<>();
        int count = 0;
        for (Map.Entry<String, ClientHandler> entry : waitingPlayers.entrySet()) {
            if (count >= requestedPlayerCount)
                break;

            String name = entry.getKey();
            ClientHandler handler = entry.getValue();

            // we create a player object
            Player player = HumanPlayer.create(name);
            game.addPlayer(player);
            playerObjects.put(name, player);
            gamePlayers.put(name, handler);
            playerNames.add(name);
            count++;
        }

        // we clear waiting list
        for (String name : playerNames) {
            waitingPlayers.remove(name);
        }

        // we start the game
        game.startGame();
        ServerView.logGameStarted(playerNames.size());

        // we send START to all players
        String playerList = String.join(",", playerNames);
        broadcast("START~" + playerList);

        // we send initial state to each player
        for (String name : playerNames) {
            sendPlayerHand(name);
            sendPlayerStock(name);
        }

        // we send TURN to indicate whose turn
        String currentPlayer = game.getCurrentPlayer().getName();
        broadcast("TURN~" + currentPlayer);
        ServerView.logTurn(currentPlayer);
    }

    // ===== GAME QUERIES =====

    // we get a player's hand as a string
    public synchronized String getPlayerHand(String name) {
        if (game == null || game.getState() != GameState.IN_PROGRESS) {
            return null;
        }

        Player player = playerObjects.get(name);
        if (player == null)
            return null;

        return formatHand(player);
    }

    // we get the table state
    public synchronized String getTableState(String name) {
        if (game == null || game.getState() != GameState.IN_PROGRESS) {
            return null;
        }

        // we format building piles
        StringBuilder building = new StringBuilder();
        for (int i = 0; i < Game.NUM_BUILDING_PILES; i++) {
            if (i > 0)
                building.append(".");
            Card top = game.getBuildingPile(i).getTopCard();
            building.append(formatCard(top));
        }

        // we format each player's discards
        StringBuilder players = new StringBuilder();
        boolean first = true;
        for (Player p : game.getPlayers()) {
            if (!first)
                players.append(",");
            first = false;

            players.append(p.getName());
            for (DiscardPile dp : p.getDiscardPiles()) {
                players.append(".");
                players.append(formatCard(dp.getTopCard()));
            }
        }

        return building.toString() + "~" + players.toString();
    }

    // ===== GAME ACTIONS =====

    // we handle a PLAY command
    public synchronized String handlePlay(String name, String from, String to) {
        if (game == null || game.getState() != GameState.IN_PROGRESS) {
            return "205"; // COMMAND_NOT_ALLOWED
        }

        Player player = playerObjects.get(name);
        if (player == null)
            return "205";

        // we check if it's their turn
        if (!game.isPlayerTurn(player)) {
            return "205"; // NOT_YOUR_TURN
        }

        // we parse the destination (must be building pile)
        int buildingIndex;
        try {
            if (!to.toUpperCase().startsWith("B.")) {
                return "206"; // INVALID_MOVE
            }
            buildingIndex = Integer.parseInt(to.substring(2));
            if (buildingIndex < 0 || buildingIndex >= Game.NUM_BUILDING_PILES) {
                return "206";
            }
        } catch (Exception e) {
            return "206";
        }

        BuildingPile targetPile = game.getBuildingPile(buildingIndex);

        // we parse the source and make the move
        try {
            if (from.toUpperCase().equals("S")) {
                // play from stock
                player.playFromStock(targetPile);
            } else if (from.toUpperCase().startsWith("H.")) {
                // play from hand
                int handIndex = Integer.parseInt(from.substring(2));
                player.playFromHand(handIndex, targetPile);
            } else if (from.toUpperCase().startsWith("D.")) {
                // play from discard
                int discardIndex = Integer.parseInt(from.substring(2));
                player.playFromDiscard(discardIndex, targetPile);
            } else {
                return "206";
            }
        } catch (Exception e) {
            return "206"; // INVALID_MOVE
        }

        // we broadcast the play
        broadcast("PLAY~" + name + "~" + from + "~" + to);
        ServerView.logMove(name, from, to);

        // we check for winner
        if (player.getStockPile().isEmpty()) {
            game.endRound(player);
            broadcast("WINNER~" + name);
            ServerView.logWinner(name);
            return null;
        }

        // we refill hand if empty
        if (player.getHand().isEmpty()) {
            player.refillHand(game.getDrawPile());
            sendPlayerHand(name);
        }

        return null;
    }

    // we handle END turn (discard to discard pile)
    public synchronized String handleEndTurn(String name) {
        if (game == null || game.getState() != GameState.IN_PROGRESS) {
            return "205";
        }

        Player player = playerObjects.get(name);
        if (player == null)
            return "205";

        if (!game.isPlayerTurn(player)) {
            return "205";
        }

        // we need at least one card in hand to discard
        if (player.getHand().isEmpty()) {
            return "206";
        }

        // we discard the first card to first non-full discard pile
        // (simple implementation - real game would let player choose)
        Card card = player.getHand().removeAt(0);
        player.getDiscardPile(0).discard(card);

        // we go to next turn
        game.nextTurn();

        // we refill new player's hand
        Player nextPlayer = game.getCurrentPlayer();
        nextPlayer.refillHand(game.getDrawPile());

        // we broadcast turn change
        broadcast("TURN~" + nextPlayer.getName());
        ServerView.logTurn(nextPlayer.getName());

        // we send updated hand to new player
        sendPlayerHand(nextPlayer.getName());

        return null;
    }

    // ===== HELPER METHODS =====

    // we broadcast a message to all game players
    private void broadcast(String message) {
        for (ClientHandler handler : gamePlayers.values()) {
            handler.send(message);
        }
    }

    // we send a player their hand
    private void sendPlayerHand(String name) {
        ClientHandler handler = gamePlayers.get(name);
        Player player = playerObjects.get(name);
        if (handler != null && player != null) {
            handler.send("HAND~" + formatHand(player));
        }
    }

    // we send a player their stock info
    private void sendPlayerStock(String name) {
        Player player = playerObjects.get(name);
        if (player != null && !player.getStockPile().isEmpty()) {
            Card top = player.getStockPile().peekTop();
            broadcast("STOCK~" + name + "~" + formatCard(top));
        }
    }

    // we format a hand as comma-separated cards
    private String formatHand(Player player) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Card card : player.getHand().getCards()) {
            if (!first)
                sb.append(",");
            first = false;
            sb.append(formatCard(card));
        }
        return sb.toString();
    }

    // we format a card as a string
    private String formatCard(Card card) {
        if (card == null)
            return "X";
        if (card.isSkipBo())
            return "SB";
        return String.valueOf(card.getNumber());
    }
}
