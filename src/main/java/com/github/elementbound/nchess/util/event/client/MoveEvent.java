package com.github.elementbound.nchess.util.event.client;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.net.Client;

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
