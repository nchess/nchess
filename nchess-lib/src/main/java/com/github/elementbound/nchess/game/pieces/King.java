package com.github.elementbound.nchess.game.pieces;

import com.github.elementbound.nchess.game.*;
import com.github.elementbound.nchess.util.GameStateUtils;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                .filter(to -> !GameStateUtils.isAllyAtNode(state, to, this))
                .map(to -> new Move(at, to))
                .collect(Collectors.toSet());
	}

    @Override
    public Piece move(Node to) {
        return new King(to, player);
    }

}
