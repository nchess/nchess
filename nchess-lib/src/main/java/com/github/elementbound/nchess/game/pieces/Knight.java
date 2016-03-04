package com.github.elementbound.nchess.game.pieces;

import java.util.ArrayList;
import java.util.List;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Table;

public class Knight extends Piece {

	public Knight(long player, long at) {
		super(player, at);
	}

	@Override
	public String getName() {
		return "knight";
	}

	@Override
	public List<Move> getMoves(Table table) {
		//TODO: No duplicate moves
		List<Move> moves = new ArrayList<>();
		Node node = table.getNode(this.at);
		
		for(int i = 0; i < node.neighborCount(); i++) {
			double direction = table.linkDirection(this.at, node.neighbor(i));
			
			long to = this.at;
			//Take two steps in direction
			for(int j = 0; j < 2; j++) {
				to = table.nodeTowardsDirection(to, direction);
				if(to < 0)
					break;
			}
			
			if(to < 0)
				continue; 
			
			//Exclude third step node
			long excluded = table.nodeTowardsDirection(to, direction);
			
			Node toNode = table.getNode(to);
			for(int j = 0; j < toNode.neighborCount(); j++) {
				if(table.isLink(this.at, toNode.neighbor(j)))
					continue; 
				
				if(table.isSecondaryLink(this.at, toNode.neighbor(j)))
					continue; 
				
				if(toNode.neighbor(j) == excluded)
					continue;
				
				if(table.isNodeOccupiedByAlly(toNode.neighbor(j), this.player))
					continue;
				
				moves.add(new Move(this.at, toNode.neighbor(j)));
			}
		}
		
		return moves;
	}

}
