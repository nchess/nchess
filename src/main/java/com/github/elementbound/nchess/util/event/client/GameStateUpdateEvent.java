package com.github.elementbound.nchess.util.event.client;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.net.Client;

public class GameStateUpdateEvent extends ClientEvent {
    private final GameState gameState;

    public GameStateUpdateEvent(Client client, GameState gameState) {
        super(client);
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }
}
