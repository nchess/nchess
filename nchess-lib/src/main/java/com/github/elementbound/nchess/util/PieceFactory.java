package com.github.elementbound.nchess.util;

import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Player;


@FunctionalInterface
public interface PieceFactory {
    Piece from(Node at, Player player);
}
