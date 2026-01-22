package com.group29.skipbo.view;

import com.group29.skipbo.card.Card;
import com.group29.skipbo.card.BuildingPile;
import com.group29.skipbo.game.Game;
import com.group29.skipbo.player.Player;

import java.util.List;

// we use this class to display the game in the terminal (View in MVC)
public class GameView {

    // we call this to show the whole game state
    public static void displayGame(Game game, Player currentPlayer) {
        clearScreen();

        System.out.println(ANSI.CYAN_BOLD + "=== Skip-Bo Game ===" + ANSI.RESET);
        System.out.println();

        displayBuildingPiles(game);
        System.out.println();

        displayCurrentPlayer(currentPlayer);
        System.out.println();

        displayOtherPlayers(game, currentPlayer);
    }

    // we use this to show the 4 building piles
    private static void displayBuildingPiles(Game game) {
        System.out.println(ANSI.YELLOW + "Building Piles:" + ANSI.RESET);
        System.out.print("  ");

        for (int i = 0; i < Game.NUM_BUILDING_PILES; i++) {
            BuildingPile pile = game.getBuildingPile(i);
            Card top = pile.getTopCard();
            System.out.print("B" + i + ": " + CardRenderer.render(top) + "  ");
        }
        System.out.println();
    }

    // we use this to show the current players hand, stock, and discards
    private static void displayCurrentPlayer(Player player) {
        System.out.println(ANSI.GREEN_BOLD + "Your Turn: " + player.getName() + ANSI.RESET);

        // show hand
        System.out.print("  Hand: ");
        List<Card> handCards = player.getHand().getCards();
        if (handCards.isEmpty()) {
            System.out.print("(empty)");
        } else {
            for (Card card : handCards) {
                System.out.print(CardRenderer.renderWithLabel(card) + " ");
            }
        }
        System.out.println();

        // show stock pile
        if (!player.getStockPile().isEmpty()) {
            Card stockTop = player.getStockPile().peekTop();
            int stockSize = player.getStockPile().size();
            System.out.println("  Stock: " + CardRenderer.render(stockTop) + " (" + stockSize + " cards left)");
        } else {
            System.out.println("  Stock: (empty)");
        }

        // show discard piles
        System.out.print("  Discards: ");
        for (int i = 0; i < 4; i++) {
            Card top = player.getDiscardPile(i).getTopCard();
            System.out.print("D" + i + ": " + CardRenderer.render(top) + "  ");
        }
        System.out.println();
    }

    // we use this to show other players basic info
    private static void displayOtherPlayers(Game game, Player current) {
        List<Player> players = game.getPlayers();

        System.out.println(ANSI.CYAN + "Other Players:" + ANSI.RESET);
        for (Player p : players) {
            if (p.equals(current)) {
                continue;
            }

            System.out
                    .println("  " + p.getName() + ": Stock=" + p.getStockPile().size() + " Hand=" + p.getHand().size());
        }
    }

    // we use this to show a message
    public static void displayMessage(String msg) {
        System.out.println(ANSI.YELLOW + msg + ANSI.RESET);
    }

    // we use this to show an error
    public static void displayError(String error) {
        System.out.println(ANSI.RED_BOLD + "ERROR: " + error + ANSI.RESET);
    }

    // we use this to show who won
    public static void displayWinner(Player winner) {
        System.out.println();
        System.out.println(ANSI.GREEN_BOLD + "========================================" + ANSI.RESET);
        System.out.println(ANSI.GREEN_BOLD + "   WINNER: " + winner.getName() + "!" + ANSI.RESET);
        System.out.println(ANSI.GREEN_BOLD + "========================================" + ANSI.RESET);
        System.out.println();
    }

    // we use this to clear the screen
    private static void clearScreen() {
        // just print newlines, simple way to do it
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    // we use this to show available commands
    public static void displayHelp() {
        System.out.println(ANSI.CYAN + "Commands:" + ANSI.RESET);
        System.out.println("  play <from> <to>  - play a card");
        System.out.println("    from: S (stock), H<card> (hand), D0-D3 (discard)");
        System.out.println("    to: B0-B3 (building pile)");
        System.out.println("  end               - end your turn");
        System.out.println("  help              - show this help");
        System.out.println("  quit              - exit game");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  play S B0         - play stock card to building 0");
        System.out.println("  play H 5 B1       - play 5 from hand to building 1");
        System.out.println("  play D2 B3        - play discard 2 to building 3");
    }
}
