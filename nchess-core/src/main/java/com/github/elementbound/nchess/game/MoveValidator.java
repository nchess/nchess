package com.github.elementbound.nchess.game;

import com.github.elementbound.nchess.util.GameStateUtils;

/**
 * Class to validate {@link Move} instances on given {@link GameState GameStates}.
 */
public class MoveValidator {
    public boolean validate(GameState state, Move move) {
        return isPiecePresent(state, move)
                && isPieceBelongingToPlayer(state, move)
                && isTargetNodeValid(state, move)
                && isPieceAbleToDoMove(state, move);
    }

    private boolean isPiecePresent(GameState state, Move move) {
        return state.getPieceAt(move.getFrom()).isPresent();
    }

    private boolean isPieceBelongingToPlayer(GameState state, Move move) {
        return state.getPieceAt(move.getFrom())
                .filter(p -> p.getPlayer().equals(state.getCurrentPlayer()))
                .isPresent();
    }

    private boolean isTargetNodeValid(GameState state, Move move) {
        return state.getPieceAt(move.getFrom())
                .filter(p -> GameStateUtils.isValidTargetNode(state, move.getTo(), p))
                .isPresent();
    }

    private boolean isPieceAbleToDoMove(GameState state, Move move) {
        return state.getPieceAt(move.getFrom()).filter(piece ->
                piece.getMoves(state).stream()
                        .anyMatch(validMove -> validMove.equals(move))
        ).isPresent();
    }
}
