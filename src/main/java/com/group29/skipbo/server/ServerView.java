package com.group29.skipbo.server;

// we use this for logging server events to console (View in MVC)
public class ServerView {

    // we log general server messages
    public static void log(String message) {
        System.out.println("[SERVER] " + message);
    }

    // we log game-related messages
    public static void logGame(String message) {
        System.out.println("[GAME] " + message);
    }

    // we log errors
    public static void logError(String message) {
        System.out.println("[ERROR] " + message);
    }

    // we log when a player connects
    public static void logPlayerJoined(String name) {
        System.out.println("[SERVER] Player joined: " + name);
    }

    // we log when a player disconnects
    public static void logPlayerLeft(String name) {
        System.out.println("[SERVER] Player left: " + name);
    }

    // we log when game starts
    public static void logGameStarted(int playerCount) {
        System.out.println("[GAME] Game started with " + playerCount + " players");
    }

    // we log whose turn it is
    public static void logTurn(String playerName) {
        System.out.println("[GAME] Turn: " + playerName);
    }

    // we log a move
    public static void logMove(String playerName, String from, String to) {
        System.out.println("[GAME] " + playerName + " played " + from + " -> " + to);
    }

    // we log the winner
    public static void logWinner(String playerName) {
        System.out.println("[GAME] *** WINNER: " + playerName + " ***");
    }
}
