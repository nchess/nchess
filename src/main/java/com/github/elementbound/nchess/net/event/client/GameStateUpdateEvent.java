package com.github.elementbound.nchess.net.event.client;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.net.Client;

/**
 * <p>Game state update event.
 * <p>Event emitted when the server sends the full state of the game after
 * the client connects.
 *
 * @see GameState
 */
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
