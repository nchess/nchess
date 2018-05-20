package com.github.elementbound.nchess.util;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;

/**
 * Helper functions to deal with {@link GameState} instances.
 */
public final class GameStateUtils {
    public static boolean isAllyAtNode(GameState state, Node at, Piece me) {
        return state.getPieceAt(at)
                .filter(piece -> me.getPlayer().equals(piece.getPlayer()))
                .isPresent();
    }

    public static boolean isEnemyAtNode(GameState state, Node at, Piece me) {
        return state.getPieceAt(at)
                .filter(piece -> !me.getPlayer().equals(piece.getPlayer()))
                .isPresent();
    }

    public static boolean isValidTargetNode(GameState state, Node target, Piece me) {
        return target != null && target.isVisible() && !GameStateUtils.isAllyAtNode(state, target, me);
    }

    private GameStateUtils() {
        // No public constructor.
    }
}
