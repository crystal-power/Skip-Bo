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
        String label = getColorLabel(card.getColor());
        int num = card.getNumber();

        // we show [5-R] format so color is visible even without ANSI support
        return color + "[" + num + "-" + label + "]" + ANSI.RESET;
    }

    // we use this to render with color and letter like [5-R]
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

        return color + "[" + num + "-" + label + "]" + ANSI.RESET;
    }

    // we use this to get the ansi color for a card
    private static String getColorCode(CardColor cardColor) {
        if (cardColor == CardColor.RED) {
            return ANSI.RED_BOLD;
        } else if (cardColor == CardColor.GREEN) {
            return ANSI.GREEN_BOLD;
        } else if (cardColor == CardColor.BLUE) {
            return ANSI.BLUE_BOLD;
        } else if (cardColor == CardColor.YELLOW) {
            return ANSI.YELLOW_BOLD;
        } else {
            return ANSI.PURPLE_BOLD;
        }
    }

    // we use this to get a single letter for the color
    private static String getColorLabel(CardColor cardColor) {
        if (cardColor == CardColor.RED) {
            return "R";
        } else if (cardColor == CardColor.GREEN) {
            return "G";
        } else if (cardColor == CardColor.BLUE) {
            return "B";
        } else if (cardColor == CardColor.YELLOW) {
            return "Y";
        } else {
            return "SB";
        }
    }

    // we use this to get text without ANSI colors but still show color label
    public static String renderSimple(Card card) {
        if (card == null) {
            return "X";
        }
        if (card.isSkipBo()) {
            return "SB";
        }
        String label = getColorLabel(card.getColor());
        return card.getNumber() + "-" + label;
    }
}
