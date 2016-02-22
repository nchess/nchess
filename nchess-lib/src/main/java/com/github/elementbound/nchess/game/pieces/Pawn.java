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
		Node node = table.getNode(at);
		
		List<Move> moves = new ArrayList<>();
		
		//TODO: enpassant
		
		if(!hasMoved) {
			for(int i = 0; i < node.neighborCount(); i++) {
				if(!table.isNodeOccupied(node.neighbor(i)))
					moves.add(new Move(at, node.neighbor(i)));
			}
		}
		else {
			long destNode = table.nodeTowardsDirection(this.at(), direction);
			if(destNode >= 0)
				moves.add(new Move(at, destNode));
		}
		
		return moves;
	}
	
	@Override
	public void onMoveApplied(Table table, long fromId, long toId) {
		if(!hasMoved) {
			direction = table.linkDirection(fromId, toId);
			hasMoved = true; 
			
			System.out.printf("%s direction is %f\n", this.toString(), direction);
		}
	}
}
