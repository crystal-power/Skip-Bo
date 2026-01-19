package com.group29.skipbo.net;

import protocol.common.Card;
import protocol.common.position.HandPosition;
import protocol.common.position.NumberedPilePosition;
import protocol.common.position.Position;
import protocol.common.position.StockPilePosition;

import java.net.ProtocolException;

public final class PositionFactory {

    private PositionFactory() {}

    public static Position stock() {
        return new StockPilePosition();
    }

    public static Position building(int index) {
        return new NumberedPilePosition(NumberedPilePosition.Pile.BUILDING_PILE, index);
    }

    public static Position discard(int index) {
        return new NumberedPilePosition(NumberedPilePosition.Pile.DISCARD_PILE, index);
    }

    /**
     * token is "1".."12" or "SB" (from the HAND display).
     */
    public static Position handToken(String token) throws ProtocolException {
        token = token.trim().toUpperCase();
        if (token.equals("SB")) {
            return new HandPosition(new Card((Integer) null)); // no number
        }
        int n = Integer.parseInt(token);
        return new HandPosition(new Card(n));
    }
}