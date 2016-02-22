package com.github.elementbound.nchess.game.pieces;

import java.util.ArrayList;
import java.util.List;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Table;

public class Queen extends Piece {

	private Rook helperRook;
	private Bishop helperBishop; 
	
	public Queen(long player, long at) {
		super(player, at);
		
		helperRook = new Rook(player, at);
		helperBishop = new Bishop(player, at);
	}

	@Override
	public String getName() {
		return "queen"; 
	}

	@Override
	public List<Move> getMoves(Table table) {
		helperRook.at(this.at); 
		helperBishop.at(this.at);
		
		List<Move> moves = new ArrayList<>();
		moves.addAll(helperRook.getMoves(table));
		moves.addAll(helperBishop.getMoves(table));

		return moves;
	}

}
