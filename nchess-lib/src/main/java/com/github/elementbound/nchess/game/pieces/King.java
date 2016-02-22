package com.github.elementbound.nchess.game.pieces;

import java.util.ArrayList;
import java.util.List;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Table;

public class King extends Piece {
	public King(long player, long at) {
		super(player, at);
	}

	@Override
	public String getName() {
		return "king"; 
	}

	@Override
	public List<Move> getMoves(Table table) {
		Node node = table.getNode(at);
		
		List<Move> moves = new ArrayList<>();
		
		for(int i = 0; i < node.neighborCount(); i++) {
			if(!table.isNodeOccupied(node.neighbor(i)));
				moves.add(new Move(at, node.neighbor(i)));
		}	
		
		for(int i = 0; i < node.secondaryNeighborCount(); i++) {
			if(!table.isNodeOccupied(node.secondaryNeighbor(i)))
				moves.add(new Move(at, node.secondaryNeighbor(i)));
		}
		
		return moves;
	}

}
