package com.github.elementbound.nchess.view;

import com.github.elementbound.nchess.game.*;
import com.github.elementbound.nchess.util.MathUtils;
import com.github.elementbound.nchess.util.event.EventSource;
import com.github.elementbound.nchess.view.event.NodeSelectEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

public class GamePanel extends JPanel {
    private static final long serialVersionUID = -6705016017912569702L;
    private static final Logger LOGGER = LoggerFactory.getLogger(GamePanel.class);

    private static final Color CELL_COLOR = Color.lightGray;
    private static final Color OUTLINE_COLOR = Color.black;
    private static final Color HIGHLIGHT_COLOR = Color.cyan; //MY EYES

    private final Map<Pair<Player, String>, Image> tintedPieceImages = new HashMap<>();
    private final EventSource<NodeSelectEvent> nodeSelectEventEventSource = new EventSource<>();

    private GameState gameState;
    private Map<Node, Path2D> polygons = new HashMap<>();
    private Rectangle2D bounds = new Rectangle2D.Double();
    private final Set<Node> highlitNodes = new HashSet<>();
    private AffineTransform viewTransform = new AffineTransform();
    private AffineTransform inverseViewTransform = new AffineTransform();

    public Point2D viewOffset = new Point2D.Double(0.0, 0.0);
    public double viewZoom = 1.0;

    public Map<String, Image> pieceImages = new HashMap<>();
    private Map<Player, Color> playerColors = new HashMap<>();

    public GamePanel() {
        //Make it fancy
        setDoubleBuffered(true);
        addMouseListener(new NodeSelectListener());

        // Initialize with empty game state
        gameState = GameState.builder()
                .table(Table.builder().build())
                .players(emptyList())
                .pieces(emptySet())
                .build();
    }

    @Override
    public void paint(Graphics g) {
        fitView();

        Rectangle clip = g.getClipBounds();

        g.setColor(this.getBackground());
        g.fillRect(clip.x, clip.y, clip.width, clip.height);

        Graphics2D g2 = (Graphics2D) g;
        g2.transform(viewTransform);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Render cells
        g2.setColor(CELL_COLOR);
        polygons.values().forEach(g2::fill);

        // Render highlit cells
        g2.setColor(HIGHLIGHT_COLOR);
        highlitNodes.stream()
                .filter(polygons::containsKey)
                .map(polygons::get)
                .forEach(g2::fill);

        // Render cell outlines
        g2.setColor(OUTLINE_COLOR);
        polygons.values().forEach(g2::draw);

        // Render pieces with alpha blending
        g.setColor(Color.black);
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
        g2.setComposite(ac);

        for (Piece piece : gameState.getPieces()) {
            Path2D poly = polygons.get(piece.getAt());

            if (poly == null) {
                LOGGER.warn("Found no polygon for {}", piece);
                continue;
            }

            double midX = poly.getBounds().getCenterX();
            double midY = poly.getBounds().getCenterY();

            if (!pieceImages.containsKey(piece.getName())) {
                LOGGER.warn("Found no image for {}", piece);
                g2.drawString(piece.getName(), (float) midX, (float) midY);
            }
            else {
                Image image = tintedPieceImages.get(ImmutablePair.of(piece.getPlayer(), piece.getName()));

                double targetSize = Math.min(poly.getBounds().getWidth(), poly.getBounds().getHeight());
                double targetScale = targetSize / Math.max(image.getWidth(null), image.getHeight(null));

                double targetWidth = image.getWidth(null) * targetScale;
                double targetHeight = image.getHeight(null) * targetScale;

                g2.drawImage(image,
                        (int) (midX - targetWidth / 2), (int) (midY - targetHeight / 2),
                        (int) targetWidth, (int) targetHeight, null);
            }
        }
    }

    public EventSource<NodeSelectEvent> getNodeSelectEventEventSource() {
        return nodeSelectEventEventSource;
    }

    public Set<Node> getHighlitNodes() {
        return highlitNodes;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        LOGGER.info("Updating game state");
        updateState(this.gameState, gameState);

        this.gameState = gameState;
        this.repaint();
    }

    private void fitView() {
        double minView = Math.min(getWidth(), getHeight());
        double maxTable = Math.max(bounds.getWidth(), bounds.getHeight());
        double scale = minView / maxTable;
        scale *= viewZoom;

        //Calculate view transform based on bounds
        //( Just center the view around the map )
        viewTransform.setToIdentity();
        viewTransform.translate(this.getBounds().getCenterX(), this.getBounds().getCenterY());
        viewTransform.scale(scale, scale);
        viewTransform.translate(viewOffset.getX(), viewOffset.getY());
        viewTransform.translate(-bounds.getCenterX(), -bounds.getCenterY());

        //Also calculate inverse view transform for mouse hit checks
        try {
            inverseViewTransform = viewTransform.createInverse();
        } catch (NoninvertibleTransformException e) {
            LOGGER.warn("View transform not invertible; setting inverse to identity", e);
            inverseViewTransform.setToIdentity();
        }
    }

    private void updateState(GameState oldState, GameState newState) {
        //Create polygons
        Table oldTable = oldState != null ? oldState.getTable() : null;
        Table newTable = newState.getTable();

        if(newTable != oldTable) {
            updatePolygons(newTable);
        }

        //Calculate table bounds based on polygons
        bounds = calculateBounds(polygons.values());
        LOGGER.info("Updated bounds to {}", bounds);

        //Calculate view transform based on bounds
        //( Just center the view around the map )
        fitView();

        LOGGER.info("Table bounds: {}", bounds);
        LOGGER.info("View bounds: {}", getBounds());
        LOGGER.info("Table center: [{};{}]", bounds.getCenterX(), bounds.getCenterY());

        //Assign colors to players
        List<Player> oldPlayers = oldState != null ? oldState.getPlayers() : null;
        List<Player> newPlayers = newState.getPlayers();

        if(!newPlayers.equals(oldPlayers)) {
            updatePlayerColors(newState);

            //Create piece images in needed colors
            updateTintedPieces(newState);
        }
    }

    private void updateTintedPieces(GameState gameState) {
        TintedImageRenderer tinter = new TintedImageRenderer();

        for (Entry<String, Image> e : pieceImages.entrySet()) {
            String pieceName = e.getKey();
            Image pieceImage = e.getValue();

            LOGGER.info("Creating {} piece tints for {}", gameState.getPlayers().size(), pieceName);

            gameState.getPlayers().stream()
                    .map(player -> ImmutablePair.of(
                            ImmutablePair.of(player, pieceName),
                            tinter.render(pieceImage, playerColors.get(player))
                    ))
                    .forEach(p -> tintedPieceImages.put(p.getKey(), p.getValue()));
        }
    }

    private void updatePlayerColors(GameState gameState) {
        playerColors.clear();
        for (int i = 0; i < gameState.getPlayers().size(); i++) {
            Player player = gameState.getPlayers().get(i);
            Color color = calculatePlayerColor(i);

            playerColors.put(player, color);
        }
    }

    private void updatePolygons(Table table) {
        LOGGER.info("Recalculating polygons; table={}", table);

        polygons.clear();
        int intersects = 0;

        for (Node node : table.getNodes()) {

            if (!node.isVisible())
                continue;

            Path2D poly = new Path2D.Double();
            for (int i = 0; i < node.getNeighbors().size(); i++) {
                int j = (i + 1) % node.getNeighbors().size();

                Node na = node.getNeighbors().get(i);
                Node nb = node.getNeighbors().get(j);

                //Deltas
                double adx = na.getX() - node.getX();
                double ady = na.getY() - node.getY();

                double bdx = nb.getX() - node.getX();
                double bdy = nb.getY() - node.getY();

                //Midpoints
                double amx = (na.getX() + node.getX()) / 2;
                double amy = (na.getY() + node.getY()) / 2;

                double bmx = (nb.getX() + node.getX()) / 2;
                double bmy = (nb.getY() + node.getY()) / 2;

                if (i == 0)
                    poly.moveTo(amx, amy);
                else
                    poly.lineTo(amx, amy);

                Line2D la = new Line2D.Double(amx, amy, amx - ady, amy + adx);
                Line2D lb = new Line2D.Double(bmx, bmy, bmx - bdy, bmy + bdx);

                Point2D intersection = MathUtils.intersectLines(la, lb, false);
                if (intersection != null) {
                    poly.lineTo(intersection.getX(), intersection.getY());
                    intersects++;
                }

                poly.lineTo(bmx, bmy);
            }

            poly.closePath();
            polygons.put(node, poly);
        }
        LOGGER.info("Got {} polygons, with {} intersections, from {} nodes", new Object[]{
                polygons.size(), intersects, table.getNodes().size()
        });
    }

    private Color calculatePlayerColor(long i) {
        if (i == 0)
            return Color.white;
        else if (i == 1)
            return Color.black;
        else
            return Color.getHSBColor(((i - 2) * 0.618033988749895f) % 1.0f, 1.0f, 1.0f);
    }

    private Rectangle2D calculateBounds(Collection<Path2D> polygons) {
        boolean firstBoundIter = true;

        double minx = Double.NaN;
        double miny = Double.NaN;
        double maxx = Double.NaN;
        double maxy = Double.NaN;

        /*for (Path2D p : polygons) {
            if (firstBoundIter) {
                minx = p.getBounds2D().getMinX();
                miny = p.getBounds2D().getMinY();

                maxx = p.getBounds2D().getMaxX();
                maxy = p.getBounds2D().getMaxY();

                firstBoundIter = false;
            } else {
                minx = Math.min(minx, p.getBounds2D().getMinX());
                miny = Math.min(miny, p.getBounds2D().getMinY());

                maxx = Math.max(maxx, p.getBounds2D().getMaxX());
                maxy = Math.max(maxy, p.getBounds2D().getMaxY());
            }
        }*/

        minx = polygons.stream()
                .map(Path2D::getBounds)
                .mapToDouble(Rectangle::getMinX)
                .min().getAsDouble();

        maxx = polygons.stream()
                .map(Path2D::getBounds)
                .mapToDouble(Rectangle::getMaxX)
                .max().getAsDouble();

        miny = polygons.stream()
                .map(Path2D::getBounds)
                .mapToDouble(Rectangle::getMinY)
                .min().getAsDouble();

        maxy = polygons.stream()
                .map(Path2D::getBounds)
                .mapToDouble(Rectangle::getMaxY)
                .max().getAsDouble();

        return new Rectangle2D.Double(minx, miny, maxx - minx, maxy - miny);
    }

    /**
     * Look up node based on world-space position ( i.e. before view transform )
     * @param x world-space x coordinate
     * @param y world-space y coordinate
     * @return node hit or null
     */
    private Node nodeAt(double x, double y) {
        for (Entry<Node, Path2D> e : polygons.entrySet())
            if (e.getValue().contains(x, y))
                return e.getKey();

        return null;
    }

    /**
     * Looks up node based on screen-space position ( i.e. after view transform )
     * @param x screen-space x coordinate
     * @param y screen-space y coordinate
     * @return node hit or null
     */
    private Node nodeAtScreen(double x, double y) {
        Point2D worldPos = new Point2D.Double(x, y);
        inverseViewTransform.transform(worldPos, worldPos);

        return nodeAt(worldPos.getX(), worldPos.getY());
    }

    /**
     * <p>Inner class to manage mouse clicks, so the enclosing class can emit node select events.
     * <p>Contains multiple empty methods, since those events are not needed, but require an implementation.
     */
    class NodeSelectListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            Node node = nodeAtScreen(e.getX(), e.getY());
            LOGGER.info("Node at [{};{}] is {}", new Object[] {e.getX(), e.getY(), node});
            if (node != null)
                nodeSelectEventEventSource.emit(new NodeSelectEvent(GamePanel.this, node));
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
}
