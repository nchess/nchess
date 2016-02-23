package com.github.elementbound.nchess.view;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.List;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Table;

public class DefaultTablePanelListener implements TablePanelListener, MouseWheelListener, MouseMotionListener, MouseListener {
	private long moveFrom = -1;
	private Point dragFrom = null;

	//=========================================================================================
	//TablePanelListener
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

	//=========================================================================================
	//MouseWheelListener
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int clicks = e.getWheelRotation();
		TablePanel panel = (TablePanel)e.getSource();
		
		if(clicks < 0) { //Wheel rotated upwards, zoom in
			for(int i = 0; i > clicks; i--)
				panel.viewZoom *= Math.pow(2.0, 1.0/4.0);
		}
		else { //Wheel rotated downwards, zoom out
			for(int i = 0; i < clicks; i++)
				panel.viewZoom /= Math.pow(2.0, 1.0/4.0);
		}
		
		panel.repaint();
	}

	//=========================================================================================
	//MouseMotionListener
	
	@Override
	public void mouseDragged(MouseEvent e) {
		System.out.printf("Mouse drag\n\tPosition: %d,%d\n\tModifiers: %s\n", 
							e.getX(), e.getY(), MouseEvent.getMouseModifiersText(e.getModifiers()));
		
		while(true) {
			if((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
				break;
			
			if((e.getModifiers() & MouseEvent.SHIFT_MASK) != 0 && 
			   (e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
				break;
			
			return;
		}
		
		if(dragFrom == null)
			return;
		
		int dx = e.getX() - (int)dragFrom.getX();
		int dy = e.getY() - (int)dragFrom.getY();
		
		System.out.printf("\tTranslate: %d,%d\n", dx,dy);
		TablePanel panel = (TablePanel)e.getSource();
		panel.viewOffset.setLocation(panel.viewOffset.getX() + dx, 
									 panel.viewOffset.getY() + dy);
		dragFrom = e.getPoint();
		panel.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	//=========================================================================================
	//MouseListener

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(dragFrom == null)
			dragFrom = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dragFrom = null;
	}

}
