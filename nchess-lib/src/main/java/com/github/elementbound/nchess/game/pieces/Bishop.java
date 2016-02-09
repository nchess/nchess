package com.github.elementbound.nchess.game.pieces;

import java.util.ArrayList;
import java.util.List;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Table;

public class Bishop extends Piece {

	public Bishop(long player, long at) {
		super(player, at);
	}

	@Override
	public String getName() {
		return "bishop"; 
	}

	@Override
	public List<Move> getMoves(Table table) {
		Node node = table.getNode(at);
		
		List<Move> moves = new ArrayList<>();
		
		for(int i = 0; i < node.secondaryNeighborCount(); i++) {
			double direction = table.linkDirection(this.at, node.secondaryNeighbor(i));
			
			long from = at;
			long to = table.secondaryNodeTowardsDirection(from, direction);
			
			while(to >= 0) {
				if(!table.getNode(to).visible())
					break;
				
				if(table.isNodeOccupiedByAlly(to, this.player))	
					break;
				
				if(table.isNodeOccupiedByEnemy(to, this.player)) {
					moves.add(new Move(this.at, to));
					break;
				}
				
				moves.add(new Move(this.at, to));
				
				//from = to;
				long nextTo = table.secondaryNodeTowardsDirection(to, direction);
				if(nextTo == from)
					break;
				
				from = to;
				to = nextTo;
				
				System.out.printf("Direction: %f; %d => %d\n", direction, from, to);
			}
		}
		
		return moves; 
	}

}
