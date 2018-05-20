package com.github.elementbound.nchess.game.pieces;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Player;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Class to represent the Queen piece.
 * <p>The moveset of the queen is defined as the union of the rook's and the bishop's.
 */
public class Queen extends Piece {

    private Rook helperRook;
    private Bishop helperBishop;

    public Queen(Node at, Player player) {
        super(at, player);

        helperRook = new Rook(at, player);
        helperBishop = new Bishop(at, player);
    }

    @Override
    public String getName() {
        return "queen";
    }

    @Override
    public Set<Move> getMoves(GameState state) {
        return Stream.of(helperBishop.getMoves(state), helperRook.getMoves(state))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public Piece move(Node to) {
        return new Queen(to, player);
    }

}
