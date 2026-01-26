package com.group29.skipbo.net;

import java.util.Arrays;
import java.util.List;

// we use this to handle messages from the server
public class ServerMessageHandler {

    private final ClientState state;

    public ServerMessageHandler(ClientState state) {
        this.state = state;
    }

    public void handle(ServerMessage msg) {
        String cmd = msg.command();

        if (cmd.equals("WELCOME")) {
            System.out.println("[SERVER] " + msg.raw());
        } else if (cmd.equals("QUEUE")) {
            System.out.println("[SERVER] QUEUE (waiting for players)");
        } else if (cmd.equals("START")) {
            List<String> players = Arrays.asList(msg.args()[0].split(","));
            state.players = players;
            for (String p : players) {
                state.tablePlayers.putIfAbsent(p, new ClientState.PlayerView());
            }
            System.out.println("[SERVER] START players=" + state.players);
        } else if (cmd.equals("TURN")) {
            state.currentTurnPlayer = msg.args()[0];
            System.out.println("[SERVER] TURN " + state.currentTurnPlayer +
                    (state.isYourTurn() ? " (YOUR TURN)" : ""));
        } else if (cmd.equals("HAND")) {
            state.hand = Arrays.asList(msg.args()[0].split(","));
            System.out.println("[SERVER] HAND " + state.hand);
        } else if (cmd.equals("STOCK")) {
            String player = msg.args()[0];
            String top = msg.args()[1];
            state.tablePlayers.putIfAbsent(player, new ClientState.PlayerView());
            state.tablePlayers.get(player).stockTop = top;
            System.out.println("[SERVER] STOCK " + player + "=" + top);
        } else if (cmd.equals("TABLE")) {
            parseTable(msg.args());
            System.out.println("[SERVER] TABLE updated");
        } else if (cmd.equals("PLAY")) {
            // Parse: PLAY~playerName~from~to~newBuildingValue
            if (msg.args().length >= 3) {
                String playerName = msg.args()[0];
                String from = msg.args()[1];
                String to = msg.args()[2];

                // Update building pile if we have the new value
                if (to.toUpperCase().startsWith("B.") && msg.args().length >= 4) {
                    try {
                        int pileIndex = Integer.parseInt(to.substring(2));
                        String newValue = msg.args()[3];

                        if (pileIndex >= 0 && pileIndex < 4) {
                            if (newValue.equals("12")) {
                                // Pile completed, will be cleared
                                state.buildingTops[pileIndex] = "X";
                            } else {
                                state.buildingTops[pileIndex] = newValue;
                            }
                        }
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
            System.out.println("[SERVER] " + msg.raw());
        } else if (cmd.equals("DISCARD")) {
        String playerName = msg.args()[0];
        String card = msg.args()[1];
        int pileIndex = Integer.parseInt(msg.args()[2]);

        // Update the player's discard pile in our state
        state.tablePlayers.putIfAbsent(playerName, new ClientState.PlayerView());
        state.tablePlayers.get(playerName).discards[pileIndex] = card;

        System.out.println("[SERVER] " + playerName + " discarded " + card + " to pile " + pileIndex);
        } else if (cmd.equals("ERROR")) {
            String code = msg.args()[0];
            String errorName = decodeError(code);
            System.out.println("[SERVER] ERROR " + code + (errorName != null ? " (" + errorName + ")" : ""));
        } else if (cmd.equals("ROUND") || cmd.equals("WINNER")) {
            System.out.println("[SERVER] " + msg.raw());
        } else {
            System.out.println("[SERVER] " + msg.raw());
        }
    }

    private void parseTable(String[] args) {
        if (args.length < 1)
            return;

        // args[0]: building tops "X.SB4.X.2"
        String[] b = args[0].split("\\.");
        for (int i = 0; i < 4; i++) {
            state.buildingTops[i] = (i < b.length ? b[i] : "X");
        }

        if (args.length < 2)
            return;

        // args[1]: players "LEON.X.12.3.1,WALLACE.3.6.4.10"
        String[] players = args[1].split(",");
        for (String pinfo : players) {
            String[] parts = pinfo.split("\\.");
            if (parts.length < 5)
                continue;

            String name = parts[0];
            state.tablePlayers.putIfAbsent(name, new ClientState.PlayerView());
            ClientState.PlayerView pv = state.tablePlayers.get(name);

            // server Table provides only discards (4 items)
            pv.discards[0] = parts.length > 1 ? parts[1] : "X";
            pv.discards[1] = parts.length > 2 ? parts[2] : "X";
            pv.discards[2] = parts.length > 3 ? parts[3] : "X";
            pv.discards[3] = parts.length > 4 ? parts[4] : "X";
        }
    }

    // we decode error codes - simple version without external dependency
    private String decodeError(String code) {
        // common error codes based on protocol
        if (code.equals("E001"))
            return "INVALID_MOVE";
        if (code.equals("E002"))
            return "NOT_YOUR_TURN";
        if (code.equals("E003"))
            return "GAME_NOT_STARTED";
        if (code.equals("E004"))
            return "INVALID_CARD";
        if (code.equals("E005"))
            return "INVALID_PILE";
        return null;
    }
}