package com.github.elementbound.nchess.view;

import com.github.elementbound.nchess.game.*;
import com.github.elementbound.nchess.game.exception.InvalidMoveException;
import com.github.elementbound.nchess.view.event.NodeSelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.*;
import java.util.Optional;

public class DefaultGamePanelListener implements MouseWheelListener, MouseMotionListener, MouseListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGamePanelListener.class);

	private Optional<Node> moveFrom = Optional.empty();
	private Point dragFrom = null;

	public void attachTo(GamePanel gamePanel) {
		gamePanel.addMouseWheelListener(this);
		gamePanel.addMouseMotionListener(this);
		gamePanel.addMouseListener(this);

		gamePanel.getNodeSelectEventEventSource().subscribe(this::nodeSelect);
	}

	public void nodeSelect(NodeSelectEvent event) {
	    GamePanel gamePanel = event.getSource();
	    Node node = event.getNode();

        gamePanel.getHighlitNodes().clear();

        GameState gameState = gamePanel.getGameState();
		
		if(moveFrom.isPresent()) {
            try {
                Move move = new Move(moveFrom.get(), node);
			    GameState newState = gameState.applyMove(move);

			    gamePanel.setGameState(newState);
			    gamePanel.getHighlitNodes().clear();
            } catch(InvalidMoveException e) {
                LOGGER.info("Invalid move", e);
            }
		} 
		else {
		    gameState.getPieceAt(node).ifPresent(piece -> {
		        // Higlight piece
		        gamePanel.getHighlitNodes().add(node);

		        // Higlight possible moves
                piece.getMoves(gameState).stream()
                        .map(Move::getTo)
                        .forEach(gamePanel.getHighlitNodes()::add);
            });
		}
		
		gamePanel.repaint();
	}

	//=========================================================================================
	//MouseWheelListener
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int clicks = e.getWheelRotation();
		GamePanel panel = (GamePanel)e.getSource();
		
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
	    // TODO: Refactor to single if statement, move condition to method
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
		
		//System.out.printf("\tTranslate: %d,%d\n", dx,dy);
		GamePanel panel = (GamePanel)e.getSource();
		panel.viewOffset.setLocation(panel.viewOffset.getX() + dx/panel.viewZoom, 
									 panel.viewOffset.getY() + dy/panel.viewZoom);
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
