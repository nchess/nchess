package com.github.elementbound.nchess.view;

import java.util.List;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Table;

public class DefaultTablePanelListener implements TablePanelListener {
	private long moveFrom = -1;

	@Override
	public void nodeSelect(TablePanel source, long nodeId) {
		source.clearHighlights();
		
		Table table = source.getTable();
		
		if(moveFrom >= 0) {
			long moveTo = nodeId; 

			Piece piece = table.getPiece(table.pieceAt(moveFrom));
			List<Move> moves = piece.getMoves(table);
			
			for(Move move: moves) {
				if(move.to() == moveTo) {
					table.applyMove(move);
					break;
				}
			}
			
			moveFrom = -1;
			source.clearHighlights();
		} 
		else {
			if(table.pieceAt(nodeId) >= 0) {
				source.highlightNode(nodeId);
				
				Piece piece = table.getPiece(table.pieceAt(nodeId));
				List<Move> moves = piece.getMoves(table);
				moveFrom = nodeId;
				
				for(Move move: moves) {
					//From is already highlit
					source.highlightNode(move.to());
				}
			}
		}
		
		source.repaint(); 
	}

}
