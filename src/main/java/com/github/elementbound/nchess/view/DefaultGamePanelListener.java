package com.github.elementbound.nchess.view;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.exception.InvalidMoveException;
import com.github.elementbound.nchess.view.event.NodeSelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.Optional;

/**
 * Class implementing default {@link GamePanel} functionality. This includes:
 * <ul>
 *     <li>Piece selection and movement</li>
 *     <li>Zooming</li>
 *     <li>Panning</li>
 * </ul>
 */
public class DefaultGamePanelListener implements MouseWheelListener, MouseMotionListener, MouseListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGamePanelListener.class);
    public static final double ZOOM_FACTOR = Math.pow(2.0, 1.0 / 4.0);

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
        GameState gameState = gamePanel.getGameState();
        Node node = event.getNode();
        Optional<Piece> pieceAtNode = gameState.getPieceAt(node);

        gamePanel.getHighlitNodes().clear();

        if (moveFrom.isPresent()) {
            try {
                Move move = new Move(moveFrom.get(), node);
                GameState newState = gameState.applyMove(move);

                gamePanel.setGameState(newState);
                gamePanel.getHighlitNodes().clear();
                moveFrom = Optional.empty();
            } catch (InvalidMoveException e) {
                LOGGER.info("Invalid move", e);
            }
        } else if (pieceAtNode.isPresent()) {
            Piece piece = pieceAtNode.get();

            // Higlight piece
            gamePanel.getHighlitNodes().add(node);

            // Higlight possible moves
            piece.getMoves(gameState).stream()
                    .map(Move::getTo)
                    .forEach(gamePanel.getHighlitNodes()::add);

            moveFrom = Optional.of(node);
        } else {
            moveFrom = Optional.empty();
        }

        gamePanel.repaint();
    }

    //=========================================================================================
    //MouseWheelListener
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int clicks = e.getWheelRotation();
        GamePanel panel = (GamePanel) e.getSource();

        double viewZoom = panel.getViewZoom();
        if (clicks < 0) { //Wheel rotated upwards, zoom in
            for (int i = 0; i > clicks; i--) {
                viewZoom *= ZOOM_FACTOR;
            }
        } else { //Wheel rotated downwards, zoom out
            for (int i = 0; i < clicks; i++) {
                viewZoom /= ZOOM_FACTOR;
            }
        }

        panel.setViewZoom(viewZoom);
        panel.repaint();
    }

    //=========================================================================================
    //MouseMotionListener

    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO: Refactor to single if statement, move condition to method
        while (true) {
            if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
                break;
            }

            if ((e.getModifiers() & MouseEvent.SHIFT_MASK) != 0
                    && (e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
                break;
            }

            return;
        }

        if (dragFrom == null) {
            return;
        }

        int dx = e.getX() - (int) dragFrom.getX();
        int dy = e.getY() - (int) dragFrom.getY();

        //System.out.printf("\tTranslate: %d,%d\n", dx,dy);
        GamePanel panel = (GamePanel) e.getSource();
        Point2D viewOffset = panel.getViewOffset();
        double viewZoom = panel.getViewZoom();

        viewOffset.setLocation(viewOffset.getX() + dx / viewZoom,
                viewOffset.getY() + dy / viewZoom);
        dragFrom = e.getPoint();
        panel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
        // Do nothing

    }

    //=========================================================================================
    //MouseListener

    @Override
    public void mouseClicked(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Do nothing

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Do nothing

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (dragFrom == null) {
            dragFrom = e.getPoint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragFrom = null;
    }

}
