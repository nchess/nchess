package com.github.elementbound.nchess.game.pieces;

import com.github.elementbound.nchess.game.*;
import com.github.elementbound.nchess.util.GameStateUtils;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Class to represent the King piece.
 * <p>
 *     The king can take a single step, either diagonally or straight. In the implementation's terms, this means that
 *     the king can either step on a neighboring node, or a secondary neighbor node.
 */
public class King extends Piece {
    public King(Node at, Player player) {
        super(at, player);
    }

    @Override
    public String getName() {
        return "king";
    }

    @Override
    public Set<Move> getMoves(GameState state) {
        return Stream.of(at.getNeighbors(), at.getSecondaryNeighbors())
                .flatMap(Collection::stream)
                .filter(to -> GameStateUtils.isValidTargetNode(state, to, this))
                .map(to -> new Move(at, to))
                .collect(Collectors.toSet());
    }

    @Override
    public Piece move(Node to) {
        return new King(to, player);
    }

}
