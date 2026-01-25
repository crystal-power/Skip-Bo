package com.group29.skipbo.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// we use this to handle one client connection (one thread per client)
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final GameController gameController;
    private BufferedReader in;
    private PrintWriter out;
    private String playerName;
    private boolean connected;

    public ClientHandler(Socket socket, GameController gameController) {
        this.socket = socket;
        this.gameController = gameController;
        this.playerName = null;
        this.connected = true;
    }

    @Override
    public void run() {
        try {
            // we set up the input/output streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            String line;
            while (connected && (line = in.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    handleCommand(line.trim());
                }
            }

        } catch (IOException e) {
            if (connected) {
                ServerView.logError("Connection error for " + (playerName != null ? playerName : "unknown") + ": "
                        + e.getMessage());
            }
        } finally {
            disconnect();
        }
    }

    // we parse and handle a command from the client
    private void handleCommand(String line) {
        String[] parts = line.split("~");
        String command = parts[0].toUpperCase();

        switch (command) {
            case "HELLO":
                handleHello(parts);
                break;
            case "GAME":
                handleGame(parts);
                break;
            case "HAND":
                handleHand();
                break;
            case "TABLE":
                handleTable();
                break;
            case "PLAY":
                handlePlay(parts);
                break;
            case "END":
                handleEnd();
                break;
            case "ADDBOT":
                handleAddBot();
                break;
            default:
                sendError("204"); // INVALID_COMMAND
                break;
        }
    }

    // ADDBOT - client wants to add a computer player
    private void handleAddBot() {
        if (playerName == null) {
            sendError("205");
            return;
        }
        String result = gameController.addBot();
        if (result != null) {
            sendError(result);
        } else {
            send("BOT_ADDED");
        }
    }

    // HELLO~name~features - client wants to register
    private void handleHello(String[] parts) {
        if (parts.length < 2) {
            sendError("001"); // INVALID_PLAYER_NAME
            return;
        }

        String name = parts[1].trim();
        if (name.isEmpty()) {
            sendError("001");
            return;
        }

        // we try to register with the game controller
        String result = gameController.registerPlayer(name, this);
        if (result == null) {
            this.playerName = name;
            send("WELCOME~" + name);
            ServerView.logPlayerJoined(name);
        } else {
            sendError(result);
        }
    }

    // GAME~n - client wants to start/join a game with n players
    private void handleGame(String[] parts) {
        if (playerName == null) {
            sendError("205"); // COMMAND_NOT_ALLOWED
            return;
        }

        if (parts.length < 2) {
            sendError("204"); // INVALID_COMMAND
            return;
        }

        try {
            int playerCount = Integer.parseInt(parts[1]);
            String result = gameController.requestGame(playerName, playerCount, this);
            if (result != null) {
                sendError(result);
            }
            // if successful, controller will send QUEUE or START
        } catch (NumberFormatException e) {
            sendError("204");
        }
    }

    // HAND - client wants to see their hand
    private void handleHand() {
        if (playerName == null) {
            sendError("205");
            return;
        }

        String hand = gameController.getPlayerHand(playerName);
        if (hand != null) {
            send("HAND~" + hand);
        } else {
            sendError("205"); // game not started
        }
    }

    // TABLE - client wants to see the table
    private void handleTable() {
        if (playerName == null) {
            sendError("205");
            return;
        }

        String table = gameController.getTableState(playerName);
        if (table != null) {
            send("TABLE~" + table);
        } else {
            sendError("205");
        }
    }

    // PLAY~from~to - client makes a move
    private void handlePlay(String[] parts) {
        if (playerName == null) {
            sendError("205");
            return;
        }

        if (parts.length < 3) {
            sendError("204");
            return;
        }

        String from = parts[1];
        String to = parts[2];
        String result = gameController.handlePlay(playerName, from, to);
        if (result != null) {
            sendError(result);
        }
        // if successful, controller broadcasts PLAY to all
    }

    // END - client ends their turn (must discard)
    private void handleEnd() {
        if (playerName == null) {
            sendError("205");
            return;
        }

        String result = gameController.handleEndTurn(playerName);
        if (result != null) {
            sendError(result);
        }
    }

    // we send a message to this client
    public void send(String message) {
        if (out != null && connected) {
            out.println(message);
        }
    }

    // we send an error to this client
    public void sendError(String code) {
        send("ERROR~" + code);
    }

    // we get the player name
    public String getPlayerName() {
        return playerName;
    }

    // we disconnect this client
    public void disconnect() {
        connected = false;
        if (playerName != null) {
            gameController.removePlayer(playerName);
            ServerView.logPlayerLeft(playerName);
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }
}
