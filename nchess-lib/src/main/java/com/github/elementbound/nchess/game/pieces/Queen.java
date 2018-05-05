package com.github.elementbound.nchess.game.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.elementbound.nchess.game.*;

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
