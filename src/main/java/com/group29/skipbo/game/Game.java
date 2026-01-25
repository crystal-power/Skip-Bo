package com.group29.skipbo.game;

import com.group29.skipbo.card.BuildingPile;
import com.group29.skipbo.card.Deck;
import com.group29.skipbo.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// we use this class to run the game, its the main game logic (Model in MVC)
public class Game {

    // how many players we allow
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 6;
    public static final int NUM_BUILDING_PILES = 4;

    private List<Player> players;
    private BuildingPile[] buildingPiles;
    private Deck drawPile;
    private GameState state;
    private int currentPlayerIndex;
    private Map<Player, Integer> scores;

    // we use this to create a new game, starts in waiting state
    public Game() {
        this.players = new ArrayList<>();
        this.buildingPiles = new BuildingPile[NUM_BUILDING_PILES];
        this.state = GameState.WAITING_FOR_PLAYERS;
        this.currentPlayerIndex = 0;
        this.scores = new HashMap<>();

        // we make the 4 building piles here
        for (int i = 0; i < NUM_BUILDING_PILES; i++) {
            buildingPiles[i] = new BuildingPile();
        }
    }

    // we use this to add a player before game starts
    // throws exception if game already started or too many players
    public void addPlayer(Player player) {
        if (state != GameState.WAITING_FOR_PLAYERS) {
            throw new IllegalStateException("Game already started!");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cant be null");
        }
        if (players.size() >= MAX_PLAYERS) {
            throw new IllegalStateException("Game is full");
        }

        players.add(player);
        scores.put(player, 0);
    }

    // we use this to remove a player before game starts
    public void removePlayer(Player player) {
        if (state != GameState.WAITING_FOR_PLAYERS) {
            throw new IllegalStateException("Cant remove, game started");
        }
        players.remove(player);
        scores.remove(player);
    }

    // we call this to start the game, need at least 2 players
    public void startGame() {
        if (state != GameState.WAITING_FOR_PLAYERS) {
            throw new IllegalStateException("Already started");
        }
        if (players.size() < MIN_PLAYERS) {
            throw new IllegalStateException("Need " + MIN_PLAYERS + " players");
        }

        // we create the deck and shuffle it
        this.drawPile = Deck.createStandardDeck();

        // we figure out how many stock cards each player gets
        // 2 players = 30 cards, 3-4 players = 20 cards, 5+ players = 15 cards
        int stockSize = getStockSize(players.size());

        // we deal stock cards to each player
        for (Player player : players) {
            for (int i = 0; i < stockSize; i++) {
                player.getStockPile().addToBottom(drawPile.draw());
            }
        }

        // we deal 5 cards to each players hand
        for (Player player : players) {
            player.refillHand(drawPile);
        }

        this.state = GameState.IN_PROGRESS;
        this.currentPlayerIndex = 0;
    }

    // we use this to get whos turn it is
    public Player getCurrentPlayer() {
        if (state != GameState.IN_PROGRESS) {
            throw new IllegalStateException("Game not running");
        }
        return players.get(currentPlayerIndex);
    }

    // we call this to go to the next player, wraps around after last player
    public void nextTurn() {
        if (state != GameState.IN_PROGRESS) {
            throw new IllegalStateException("Game not running");
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    // we use this to check if its a specific players turn
    public boolean isPlayerTurn(Player player) {
        if (state != GameState.IN_PROGRESS) {
            return false;
        }
        return players.get(currentPlayerIndex).equals(player);
    }

    // we use this to get a building pile by index 0-3 (matches protocol B.0 B.1
    // etc)
    public BuildingPile getBuildingPile(int index) {
        if (index < 0 || index >= NUM_BUILDING_PILES) {
            throw new IndexOutOfBoundsException("Bad pile index: " + index);
        }
        return buildingPiles[index];
    }

    // returns all the building piles
    public BuildingPile[] getBuildingPiles() {
        return buildingPiles;
    }

    public GameState getState() {
        return state;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public Deck getDrawPile() {
        return drawPile;
    }

    // we use this to get a players score
    public int getScore(Player player) {
        if (!scores.containsKey(player)) {
            throw new IllegalArgumentException("Player not in game");
        }
        return scores.get(player);
    }

    public Map<Player, Integer> getScores() {
        return scores;
    }

    // we use this to add points to someone
    public void addScore(Player player, int points) {
        if (!scores.containsKey(player)) {
            throw new IllegalArgumentException("Player not in game");
        }
        int current = scores.get(player);
        scores.put(player, current + points);
    }

    // we call this when someone empties their stock pile
    public void endRound(Player winner) {
        if (state != GameState.IN_PROGRESS) {
            throw new IllegalStateException("Game not running");
        }
        this.state = GameState.ROUND_OVER;
        // TODO: we need to calculate scores based on cards left
    }

    // we use this to end the game completely
    public void endGame() {
        this.state = GameState.GAME_OVER;
    }

    // we use this to find who has the highest score
    public Player getWinner() {
        if (players.isEmpty()) {
            return null;
        }

        Player winner = players.get(0);
        int best = scores.get(winner);

        for (Player p : players) {
            if (scores.get(p) > best) {
                best = scores.get(p);
                winner = p;
            }
        }
        return winner;
    }

    // we use this to figure out how many stock cards per player
    // based on number of players (skip-bo rules)
    private int getStockSize(int playerCount) {
        if (playerCount <= 2) {
            return 30;
        } else if (playerCount <= 4) {
            return 20;
        } else {
            return 15;
        }
    }
}
