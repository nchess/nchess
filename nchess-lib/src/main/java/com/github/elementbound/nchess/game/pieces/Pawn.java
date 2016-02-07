package com.github.elementbound.nchess.game.pieces;

import java.util.ArrayList;
import java.util.List;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Table;

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
	public List<Move> getMoves(Table table) {
		//TODO: Take this.hasMoved into account 
		Node node = table.getNode(at);
		
		List<Move> moves = new ArrayList<>();
		
		for(int i = 0; i < node.neighborCount(); i++)
			moves.add(new Move(at, node.neighbor(i)));
		
		return moves;
	}
	
}