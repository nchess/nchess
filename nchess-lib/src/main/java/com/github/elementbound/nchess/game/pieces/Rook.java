package com.github.elementbound.nchess.game.pieces;

import java.util.ArrayList;
import java.util.List;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Table;

public class Rook extends Piece {

	public Rook(long player, long at) {
		super(player, at);
	}

	@Override
	public String getName() {
		return "rook";
	}

	@Override
	public List<Move> getMoves(Table table) {
		Node node = table.getNode(at);
		
		List<Move> moves = new ArrayList<>();
		
		for(int i = 0; i < node.neighborCount(); i++) {
			double direction = table.linkDirection(this.at, node.neighbor(i));
			
			long from = at;
			long to = table.nodeTowardsDirection(from, direction);
			
			while(to >= 0) {
				if(table.isNodeOccupiedByAlly(to, this.player))	
					break;
				
				if(table.isNodeOccupiedByEnemy(to, this.player)) {
					moves.add(new Move(this.at, to));
					break;
				}
				
				moves.add(new Move(this.at, to));
				
				from = to;
				to = table.nodeTowardsDirection(from, direction);
			}
		}
		
		return moves; 
	}

}
