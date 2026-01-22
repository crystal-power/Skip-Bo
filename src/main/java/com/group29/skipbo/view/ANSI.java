package com.group29.skipbo.view;

// we use this for coloring terminal output
public interface ANSI {
    String RESET = "\033[0m";

    // normal colors
    String BLACK = "\033[0;30m";
    String RED = "\033[0;31m";
    String GREEN = "\033[0;32m";
    String YELLOW = "\033[0;33m";
    String BLUE = "\033[0;34m";
    String PURPLE = "\033[0;35m";
    String CYAN = "\033[0;36m";
    String WHITE = "\033[0;37m";

    // bold colors
    String RED_BOLD = "\033[1;31m";
    String GREEN_BOLD = "\033[1;32m";
    String YELLOW_BOLD = "\033[1;33m";
    String BLUE_BOLD = "\033[1;34m";
    String PURPLE_BOLD = "\033[1;35m";
    String CYAN_BOLD = "\033[1;36m";
    String WHITE_BOLD = "\033[1;37m";

    // backgrounds
    String RED_BACKGROUND = "\033[41m";
    String GREEN_BACKGROUND = "\033[42m";
    String BLUE_BACKGROUND = "\033[44m";
    String WHITE_BACKGROUND = "\033[47m";
}
