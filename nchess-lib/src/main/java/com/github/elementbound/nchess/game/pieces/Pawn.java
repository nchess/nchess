package com.github.elementbound.nchess.game.pieces;

import java.util.List;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Piece;

public class Pawn extends Piece {
	private boolean hasMoved = false; 
	private double direction = 0.0;

	public Pawn(long player, long at) {
		super(player, at);
	}

	@Override
	public String getName() {
		return "pawn";
	}

	@Override
	public List<Move> getMoves() {
		// TODO Auto-generated method stub
		return null;
	}

}
