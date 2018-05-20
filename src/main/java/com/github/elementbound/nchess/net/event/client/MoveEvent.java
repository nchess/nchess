package com.github.elementbound.nchess.net.event.client;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.net.Client;

/**
 * <p>Event describing a move.
 * <p>Emitted when a player makes a move.
 */
public class MoveEvent extends ClientEvent {
    private final GameState gameState;
    private final Move move;

    public MoveEvent(Client client, GameState gameState, Move move) {
        super(client);
        this.gameState = gameState;
        this.move = move;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Move getMove() {
        return move;
    }
}
