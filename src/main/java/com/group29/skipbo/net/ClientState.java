package com.group29.skipbo.net;

import java.util.*;

public class ClientState {
    public String yourName;
    public String currentTurnPlayer;

    public List<String> players = new ArrayList<>();

    // hand tokens as sent by server: "1","12","SB"
    public List<String> hand = new ArrayList<>();

    // building top tokens from TABLE: length 4, values like "X","1","SB4"
    public String[] buildingTops = new String[]{"X","X","X","X"};

    public static class PlayerView {
        public String stockTop = "X";                 // from STOCK messages
        public String[] discards = new String[]{"X","X","X","X"}; // from TABLE
    }

    public Map<String, PlayerView> tablePlayers = new HashMap<>();

    public boolean isYourTurn() {
        return yourName != null && yourName.equals(currentTurnPlayer);
    }
}