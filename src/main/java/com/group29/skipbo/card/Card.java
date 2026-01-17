package com.group29.skipbo.card;

public class Card {

    private final CardColor color;
    private final Integer number; // null = skipbo

    private Card(CardColor color, Integer number) {
        this.color = color;
        this.number = number;
    }

    // numbered cards
    public static Card numbered(CardColor color, int number) {
        if (number < 1 || number > 12) {
            throw new IllegalArgumentException("Card number must be between 1 and 12");
        }
        return new Card(color, number);
    }

    // skipbo cards
    public static Card skipBo() {
        return new Card(CardColor.SKIPBO, null);
    }

    public boolean isSkipBo() {
        return number == null;
    }

    public int getNumber() {
        if (isSkipBo()) {
            throw new IllegalStateException("Skip Bo card has no number");
        }
        return number;
    }

    public CardColor getColor() {
        return color;
    }
}