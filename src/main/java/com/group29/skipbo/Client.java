// src/main/java/com/group29/skipbo/client/Client.java

package com.group29.skipbo;

import com.group29.skipbo.net.ClientState;
import com.group29.skipbo.net.ServerMessage;
import com.group29.skipbo.net.ServerMessageHandler;
import com.group29.skipbo.net.ServerMessageParser;
import com.group29.skipbo.net.SkipBoNetworkClient;
import protocol.Command;
import protocol.client.Hello;
import protocol.common.Feature;

import java.io.IOException;
import java.util.Scanner;


public class Client {

    private final String host;
    private final int port;
    private final String playerName;

    private SkipBoNetworkClient networkClient;
    private ClientState state;
    private ServerMessageHandler messageHandler;
    private volatile boolean running;
    private volatile boolean gameOver;

    public Client(String host, int port, String playerName) {
        this.host = host;
        this.port = port;
        this.playerName = playerName;
        this.state = new ClientState();
        this.messageHandler = new ServerMessageHandler(state);
        this.running = false;
        this.gameOver = false;
    }

    // connects to the server and starts the client.
    public void start() {
        try {
            System.out.println("Connecting to " + host + ":" + port + "...");
            networkClient = new SkipBoNetworkClient(host, port);
            running = true;

            // start the reader thread
            Thread readerThread = new Thread(this::readFromServer);
            readerThread.setDaemon(true);
            readerThread.start();

            // send HELLO to register
            sendHello();

            // start interactive input loop
            handleUserInput();

        } catch (IOException e) {
            System.out.println("Failed to connect: " + e.getMessage());
        } finally {
            stop();
        }
    }

    // reads messages from the server in a loop
    private void readFromServer() {
        try {
            networkClient.readLoop(line -> {
                ServerMessage msg = ServerMessageParser.parse(line);
                handleServerMessage(msg);
            });
        } catch (IOException e) {
            if (running) {
                System.out.println("Connection lost: " + e.getMessage());
            }
        }
        running = false;
    }

    // handles a message from the server.
    private void handleServerMessage(ServerMessage msg) {
        // let the handler update state and print
        messageHandler.handle(msg);

        // handle special cases
        String cmd = msg.command();

        if (cmd.equals("WELCOME")) {
            state.yourName = playerName;
            System.out.println("\n=== Connected as " + playerName + " ===");
            printHelp();
        } else if (cmd.equals("TURN")) {
            if (state.isYourTurn()) {
                System.out.println("\n>>> It's YOUR turn! <<<");
            }
        } else if (cmd.equals("HAND")) {
            if (state.isYourTurn()) {
                printTurnOptions();
            }
        } else if (cmd.equals("WINNER")) {
            gameOver = true;
            String winner = msg.args()[0];
            if (winner.equals(playerName)) {
                System.out.println("\n*** CONGRATULATIONS! YOU WON! ***");
            } else {
                System.out.println("\n*** " + winner + " won the game! ***");
            }
        } else if (cmd.equals("BOT_ADDED")) {
            System.out.println("[INFO] Bot added successfully!");
        }
    }

    // handles user input from the console.
    private void handleUserInput() {
        Scanner scanner = new Scanner(System.in);

        while (running && !gameOver) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }

            processCommand(input);
        }

        scanner.close();
    }

    // processes a user command
    private void processCommand(String input) {
        String[] parts = input.split("\\s+");
        String cmd = parts[0].toLowerCase();

        switch (cmd) {
            case "help":
                printHelp();
                break;

            case "game":
                handleGameCommand(parts);
                break;

            case "addbot":
            case "bot":
                sendRaw("ADDBOT");
                break;

            case "hand":
                sendRaw("HAND");
                break;

            case "table":
                sendRaw("TABLE");
                break;

            case "play":
                handlePlayCommand(parts);
                break;

            case "end":
            case "discard":
                sendRaw("END");
                break;

            case "status":
                printStatus();
                break;

            case "quit":
            case "exit":
                running = false;
                break;

            default:
                System.out.println("Unknown command. Type 'help' for available commands.");
                break;
        }
    }

    // handles the game command to start/join a game.
    private void handleGameCommand(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: game <number_of_players>");
            System.out.println("Example: game 2");
            return;
        }

        try {
            int playerCount = Integer.parseInt(parts[1]);
            if (playerCount < 2 || playerCount > 6) {
                System.out.println("Player count must be between 2 and 6");
                return;
            }
            sendRaw("GAME~" + playerCount);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number: " + parts[1]);
        }
    }

    // handles the play command
    private void handlePlayCommand(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Usage: play <from> <to>");
            System.out.println("  from: S (stock), H.0-H.4 (hand), D.0-D.3 (discard)");
            System.out.println("  to:   B.0-B.3 (building pile)");
            System.out.println("Examples:");
            System.out.println("  play S B.0      - Play from stock to building pile 0");
            System.out.println("  play H.0 B.1    - Play hand card 0 to building pile 1");
            System.out.println("  play D.2 B.3    - Play from discard pile 2 to building pile 3");
            return;
        }

        String from = parts[1].toUpperCase();
        String to = parts[2].toUpperCase();

        sendRaw("PLAY~" + from + "~" + to);
    }

    // sends the hello command to register with the server
    private void sendHello() {
        Hello hello = new Hello(playerName, new Feature[]{});
        networkClient.send(hello);
    }

    // sends a raw string to the server
    private void sendRaw(String message) {
        networkClient.send(new RawCommand(message));
    }

    // prints help information
    private void printHelp() {
        System.out.println("\n=== Skip-Bo Client Commands ===");
        System.out.println("  game <n>        - Start/join a game with n players (2-6)");
        System.out.println("  addbot          - Add a computer player");
        System.out.println("  hand            - Show your hand");
        System.out.println("  table           - Show the table state");
        System.out.println("  play <from> <to>- Play a card (see below)");
        System.out.println("  end             - End your turn (discard a card)");
        System.out.println("  status          - Show current game status");
        System.out.println("  help            - Show this help");
        System.out.println("  quit            - Exit the client");
        System.out.println("\n=== Play Command Format ===");
        System.out.println("  from: S (stock), H.0-H.4 (hand), D.0-D.3 (discard)");
        System.out.println("  to:   B.0-B.3 (building pile)");
        System.out.println("  Example: play H.0 B.1");
        System.out.println();
    }

    // prints options during the player's turn
    private void printTurnOptions() {
        System.out.println("\nYour hand: " + state.hand);
        System.out.println("Building piles: " + formatBuildingPiles());
        System.out.println("\nCommands: play <from> <to>, end, hand, table");
    }

    // prints current game status
    private void printStatus() {
        System.out.println("\n=== Game Status ===");
        System.out.println("Your name: " + state.yourName);
        System.out.println("Players: " + state.players);
        System.out.println("Current turn: " + state.currentTurnPlayer);
        System.out.println("Is your turn: " + state.isYourTurn());
        System.out.println("Your hand: " + state.hand);
        System.out.println("Building piles: " + formatBuildingPiles());

        System.out.println("\nPlayer info:");
        for (String player : state.players) {
            ClientState.PlayerView pv = state.tablePlayers.get(player);
            if (pv != null) {
                System.out.println("  " + player + ": stock=" + pv.stockTop +
                        ", discards=" + String.join(",", pv.discards));
            }
        }
        System.out.println();
    }

    // formats building piles for display
    private String formatBuildingPiles() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 4; i++) {
            if (i > 0) sb.append(", ");
            sb.append("B.").append(i).append("=").append(state.buildingTops[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    // stops the client
    public void stop() {
        running = false;
        if (networkClient != null) {
            try {
                networkClient.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    // helper class to send raw protocol strings
    private static class RawCommand implements Command {
        private final String message;

        public RawCommand(String message) {
            this.message = message;
        }

        @Override
        public String transformToProtocolString() {
            return message;
        }
    }


    public static void main(String[] args) {
        String host = "localhost";
        int port = 7777;
        String name = "Player" + System.currentTimeMillis() % 1000;

        // Parse command line arguments
        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port, using default 7777");
            }
        }
        if (args.length >= 3) {
            name = args[2];
        }

        System.out.println("=================================");
        System.out.println("    Skip-Bo Game Client");
        System.out.println("=================================");
        System.out.println("Host: " + host);
        System.out.println("Port: " + port);
        System.out.println("Name: " + name);
        System.out.println();

        Client client = new Client(host, port, name);
        client.start();
    }
}
