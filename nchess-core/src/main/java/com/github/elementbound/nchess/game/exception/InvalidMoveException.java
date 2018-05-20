package com.github.elementbound.nchess.game.exception;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Exception thrown when an invalid move is attempted.
 */
public class InvalidMoveException extends RuntimeException {
    private final GameState state;
    private final Move move;

    public InvalidMoveException(GameState state, Move move) {
        this.state = state;
        this.move = move;
    }

    public InvalidMoveException(String message, GameState state, Move move) {
        super(message);
        this.state = state;
        this.move = move;
    }

    public InvalidMoveException(String message, Throwable cause, GameState state, Move move) {
        super(message, cause);
        this.state = state;
        this.move = move;
    }

    public InvalidMoveException(Throwable cause, GameState state, Move move) {
        super(cause);
        this.state = state;
        this.move = move;
    }

    public InvalidMoveException(String message, Throwable cause,
                                boolean enableSuppression, boolean writableStackTrace,
                                GameState state, Move move) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.state = state;
        this.move = move;
    }

    public GameState getState() {
        return state;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
