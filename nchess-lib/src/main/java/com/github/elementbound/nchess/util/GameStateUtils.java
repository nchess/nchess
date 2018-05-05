package com.github.elementbound.nchess.util;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;

public class GameStateUtils {
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
}
