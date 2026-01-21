package com.group29.skipbo.net;

import protocol.common.ErrorCode;

import java.util.Arrays;
import java.util.List;

public class ServerMessageHandler {

    private final ClientState state;

    public ServerMessageHandler(ClientState state) {
        this.state = state;
    }

    public void handle(ServerMessage msg) {
        switch (msg.command()) {
            case "WELCOME" -> {
                System.out.println("[SERVER] " + msg.raw());
            }
            case "QUEUE" -> System.out.println("[SERVER] QUEUE (waiting for players)");
            case "START" -> {
                List<String> players = Arrays.asList(msg.args()[0].split(","));
                state.players = players;
                players.forEach(p -> state.tablePlayers.putIfAbsent(p, new ClientState.PlayerView()));
                System.out.println("[SERVER] START players=" + state.players);
            }
            case "TURN" -> {
                state.currentTurnPlayer = msg.args()[0];
                System.out.println("[SERVER] TURN " + state.currentTurnPlayer +
                        (state.isYourTurn() ? " (YOUR TURN)" : ""));
            }
            case "HAND" -> {
                state.hand = Arrays.asList(msg.args()[0].split(","));
                System.out.println("[SERVER] HAND " + state.hand);
            }
            case "STOCK" -> {
                String player = msg.args()[0];
                String top = msg.args()[1];
                state.tablePlayers.putIfAbsent(player, new ClientState.PlayerView());
                state.tablePlayers.get(player).stockTop = top;
                System.out.println("[SERVER] STOCK " + player + "=" + top);
            }
            case "TABLE" -> {
                parseTable(msg.args());
                System.out.println("[SERVER] TABLE updated");
            }
            case "PLAY" -> {
                // PLAY~PLAYER~FROM~TO
                System.out.println("[SERVER] " + msg.raw());
            }
            case "ERROR" -> {
                // ERROR~code
                String code = msg.args()[0];
                ErrorCode e = decodeError(code);
                System.out.println("[SERVER] ERROR " + code + (e != null ? " (" + e + ")" : ""));
            }
            case "ROUND", "WINNER" -> System.out.println("[SERVER] " + msg.raw());
            default -> System.out.println("[SERVER] " + msg.raw());
        }
    }

    private void parseTable(String[] args) {
        if (args.length < 1) return;

        // args[0]: building tops "X.SB4.X.2"
        String[] b = args[0].split("\\.");
        for (int i = 0; i < 4; i++) {
            state.buildingTops[i] = (i < b.length ? b[i] : "X");
        }

        if (args.length < 2) return;

        // args[1]: players "LEON.X.12.3.1,WALLACE.3.6.4.10"
        String[] players = args[1].split(",");
        for (String pinfo : players) {
            String[] parts = pinfo.split("\\.");
            if (parts.length < 5) continue;

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

    private ErrorCode decodeError(String code) {
        for (ErrorCode e : ErrorCode.values()) {
            if (e.getCode().equals(code)) return e;
        }
        return null;
    }
}