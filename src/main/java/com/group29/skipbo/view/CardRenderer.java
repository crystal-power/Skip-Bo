package com.group29.skipbo.view;

import com.group29.skipbo.card.Card;
import com.group29.skipbo.card.CardColor;

// we use this to draw cards with colors in the terminal
public class CardRenderer {

    // we render a card as a string with color
    public static String render(Card card) {
        if (card == null) {
            return ANSI.WHITE + "[X]" + ANSI.RESET;
        }

        if (card.isSkipBo()) {
            return ANSI.PURPLE_BOLD + "[SB]" + ANSI.RESET;
        }

        String color = getColorCode(card.getColor());
        int num = card.getNumber();
        String cardStr = String.format("[%d]", num);

        return color + cardStr + ANSI.RESET;
    }

    // we use this to render with color abbreviation like [5-R]
    public static String renderWithLabel(Card card) {
        if (card == null) {
            return "[X]";
        }

        if (card.isSkipBo()) {
            return ANSI.PURPLE_BOLD + "[SB]" + ANSI.RESET;
        }

        String color = getColorCode(card.getColor());
        String label = getColorLabel(card.getColor());
        int num = card.getNumber();
        String cardStr = String.format("[%d-%s]", num, label);

        return color + cardStr + ANSI.RESET;
    }

    // get ANSI color code for card color
    private static String getColorCode(CardColor cardColor) {
        if (cardColor == CardColor.RED) {
            return ANSI.RED_BOLD;
        } else if (cardColor == CardColor.GREEN) {
            return ANSI.GREEN_BOLD;
        } else if (cardColor == CardColor.BLUE) {
            return ANSI.BLUE_BOLD;
        } else {
            return ANSI.PURPLE_BOLD;
        }
    }

    // get single letter for color
    private static String getColorLabel(CardColor cardColor) {
        if (cardColor == CardColor.RED) {
            return "R";
        } else if (cardColor == CardColor.GREEN) {
            return "G";
        } else if (cardColor == CardColor.BLUE) {
            return "B";
        } else {
            return "SB";
        }
    }

    // render just the number of a card (no color)
    public static String renderSimple(Card card) {
        if (card == null) {
            return "X";
        }
        if (card.isSkipBo()) {
            return "SB";
        }
        return String.valueOf(card.getNumber());
    }
}
