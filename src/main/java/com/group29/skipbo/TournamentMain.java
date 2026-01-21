package com.group29.skipbo;

import com.group29.skipbo.net.*;
import protocol.client.*;
import protocol.common.Feature;
import protocol.common.position.Position;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.Locale;
import java.util.Scanner;

public class TournamentMain {

    public static void main(String[] args) throws IOException {
        // host port name features
        // example: localhost 7777 imavampire CLM
        if (args.length < 3) {
            System.out.println("Usage: <host> <port> <name> [featuresLetters]");
            System.out.println("Example: localhost 7777 imavampire CLM");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String name = args[2];
        String featuresLetters = (args.length >= 4) ? args[3] : "";

        ClientState state = new ClientState();
        state.yourName = name;

        try (SkipBoNetworkClient client = new SkipBoNetworkClient(host, port)) {
            ServerMessageHandler handler = new ServerMessageHandler(state);

            // reader thread
            Thread reader = new Thread(() -> {
                try {
                    client.readLoop(line -> {
                        ServerMessage msg = ServerMessageParser.parse(line);
                        handler.handle(msg);

                        // simple auto-resync strategy
                        if (msg.command().equals("PLAY")) {
                            client.send(new Table());
                            // requesting to be safe
                            client.send(new protocol.client.Hand());
                        }
                        if (msg.command().equals("ERROR")) {
                            client.send(new Table());
                            client.send(new protocol.client.Hand());
                        }
                    });
                } catch (IOException e) {
                    System.out.println("[NET] Disconnected: " + e.getMessage());
                }
            });
            reader.setDaemon(true);
            reader.start();

            Feature[] features = parseFeatures(featuresLetters);
            client.send(new Hello(name, features));

            System.out.println("Connected. Type commands: game N | hand | table | play ... | end | state | quit");
            printHelp();

            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print("> ");
                if (!sc.hasNextLine()) break;
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                String cmd = parts[0].toLowerCase(Locale.ROOT);

                try {
                    switch (cmd) {
                        case "quit", "exit" -> { return; }
                        case "help" -> printHelp();
                        case "state" -> printState(state);

                        case "game" -> {
                            int n = Integer.parseInt(parts[1]);
                            client.send(new Game(n));
                        }
                        case "hand" -> client.send(new protocol.client.Hand());
                        case "table" -> client.send(new Table());
                        case "end" -> {
                            if (!state.isYourTurn()) {
                                System.out.println("Not your turn (TURN=" + state.currentTurnPlayer + ")");
                            } else {
                                client.send(new End());
                            }
                        }
                        case "play" -> {
                            if (!state.isYourTurn()) {
                                System.out.println("Not your turn (TURN=" + state.currentTurnPlayer + ")");
                                break;
                            }
                            Play play = parsePlay(parts);
                            client.send(play);
                        }
                        default -> System.out.println("Unknown command. Type 'help'.");
                    }
                } catch (Exception e) {
                    System.out.println("Command error: " + e.getMessage());
                }
            }
        }
    }

    private static void printHelp() {
        System.out.println("""
                Commands:
                  game N              (2..6)
                  hand
                  table
                  state               (prints current cached state)
                  end
                  play s b0
                  play h 8 b2
                  play h sb b1
                  play d2 b3
                  help
                  quit
                """);
    }

    private static void printState(ClientState s) {
        System.out.println("You: " + s.yourName);
        System.out.println("Turn: " + s.currentTurnPlayer + (s.isYourTurn() ? " (YOU)" : ""));
        System.out.println("Players: " + s.players);
        System.out.println("Hand: " + s.hand);
        System.out.println("Building tops: B0=" + s.buildingTops[0] + " B1=" + s.buildingTops[1] +
                " B2=" + s.buildingTops[2] + " B3=" + s.buildingTops[3]);
        for (var entry : s.tablePlayers.entrySet()) {
            String p = entry.getKey();
            var pv = entry.getValue();
            System.out.println(" - " + p + " stock=" + pv.stockTop +
                    " discards=[" + pv.discards[0] + "," + pv.discards[1] + "," + pv.discards[2] + "," + pv.discards[3] + "]");
        }
    }

    private static Feature[] parseFeatures(String letters) {
        letters = letters == null ? "" : letters.trim().toUpperCase(Locale.ROOT);
        // include just supported ones, order doesnt matter because Hello sorts
        java.util.ArrayList<Feature> list = new java.util.ArrayList<>();
        for (char c : letters.toCharArray()) {
            if (c == 'C') list.add(Feature.CHAT);
            if (c == 'L') list.add(Feature.LOBBY);
            if (c == 'M') list.add(Feature.MASTER);
        }
        return list.toArray(new Feature[0]);
    }

    private static Play parsePlay(String[] parts) throws ProtocolException {
        // play <from> <to>
        // from:
        //   s
        //   h <token>(token: 1..12 or sb)
        //   d<idx>(like d2)
        // to:
        //   b<idx>(like b0)
        if (parts.length < 3) throw new IllegalArgumentException("Usage: play <from> <to> OR play h <card> <to>");

        int i = 1;

        Position from;
        String fromToken = parts[i].toLowerCase(Locale.ROOT);

        if (fromToken.equals("s")) {
            from = PositionFactory.stock();
            i++;
        } else if (fromToken.equals("h")) {
            if (parts.length < 4) throw new IllegalArgumentException("Usage: play h <card> <to>");
            String cardToken = parts[i + 1];
            from = PositionFactory.handToken(cardToken);
            i += 2;
        } else if (fromToken.startsWith("d")) {
            int idx = Integer.parseInt(fromToken.substring(1));
            from = PositionFactory.discard(idx);
            i++;
        } else {
            throw new IllegalArgumentException("Unknown FROM. Use: s | h <card> | d0..d3");
        }

        String toToken = parts[i].toLowerCase(Locale.ROOT);
        if (!toToken.startsWith("b")) throw new IllegalArgumentException("TO must be b0..b3");
        int bIdx = Integer.parseInt(toToken.substring(1));
        Position to = PositionFactory.building(bIdx);

        return new Play(from, to);
    }
}