package com.group29.skipbo.game;

// we use this enum to track what state the game is in
public enum GameState {
    WAITING_FOR_PLAYERS, // waiting for players to join
    IN_PROGRESS, // we are playing
    ROUND_OVER, // someone won the round
    GAME_OVER // game is done
}
