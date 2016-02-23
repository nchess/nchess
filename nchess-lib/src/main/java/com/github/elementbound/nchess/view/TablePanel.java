package com.github.elementbound.nchess.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JPanel;

import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.util.MathUtils;

public class TablePanel extends JPanel {
	private Table table; 
	private Map<Long, Path2D> polys = new HashMap<>();
	private Rectangle2D bounds = new Rectangle2D.Double();
	private Set<Long> highlitNodes = new HashSet<>();
	private AffineTransform viewTransform = new AffineTransform();
	private AffineTransform inverseViewTransform = new AffineTransform();
	
	private List<TablePanelListener> listeners = new ArrayList<>();
	
	public Color cellColor = Color.lightGray;
	public Color cellOutlineColor = Color.black; 
	public Color cellHighlightColor = Color.cyan; //MY EYES
	
	public Point2D viewOffset = new Point2D.Double(0.0, 0.0);
	public double viewZoom = 1.0;
	
	private TintedImageSet tintedPieceImages = new TintedImageSet();
	public Map<String, Image> pieceImages = new HashMap<>();
	public Map<Long, Color> playerColors = new HashMap<>();
	
	public TablePanel() {
		//Make it fancy
		this.setDoubleBuffered(true);
		
		this.addMouseListener(new NodeSelectListener(this));
	}
	
	public void fitView() {
		double minView = (double)Math.min(this.getWidth(), this.getHeight());
		double maxTable = (double)Math.max(this.bounds.getWidth(), this.bounds.getHeight());
		double scale = minView/maxTable;
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
			System.out.println("Warning: View transform not invertible");
			e.printStackTrace();
		}
	}

	public void assignTable(Table table) {
		this.table = table; 
		
		//Create polys
		polys.clear();
		int intersects = 0;
		for(Entry<Long, Node> e: this.table.allNodes()) {
			Node node = e.getValue();
			if(!node.visible())
				continue; 
			
			Path2D poly = new Path2D.Double();
			for(int i = 0; i < node.neighborCount(); i++) {
				Node na = this.table.getNode(node.neighbor(i));
				Node nb = this.table.getNode(node.neighbor((i + 1) % node.neighborCount()));
				
				//Deltas
				double adx = na.x() - node.x(); double ady = na.y() - node.y();
				double bdx = nb.x() - node.x(); double bdy = nb.y() - node.y(); 
				
				//Midpoints
				double amx = (na.x() + node.x()) / 2; double amy = (na.y() + node.y()) / 2;
				double bmx = (nb.x() + node.x()) / 2; double bmy = (nb.y() + node.y()) / 2;

				if(i == 0)
					poly.moveTo(amx,amy);
				else 
					poly.lineTo(amx,amy);

				Line2D la = new Line2D.Double(amx,amy, amx-ady, amy+adx);
				Line2D lb = new Line2D.Double(bmx,bmy, bmx-bdy, bmy+bdx);
				
				Point2D intersect = MathUtils.intersectLines(la, lb, false);
				if(intersect != null) {
					poly.lineTo(intersect.getX(), intersect.getY());
					intersects++;
				}
				
				poly.lineTo(bmx,bmy);
			}

			poly.closePath();
			polys.put(node.id(), poly);
		}
		
		//Calculate table bounds based on polys
		boolean firstBoundIter = true;
		double minx = Double.NaN; double miny = Double.NaN;
		double maxx = Double.NaN; double maxy = Double.NaN;
		for(Path2D p : polys.values()) {
			if(firstBoundIter) {
				minx = p.getBounds2D().getMinX();
				miny = p.getBounds2D().getMinY();
				
				maxx = p.getBounds2D().getMaxX();
				maxy = p.getBounds2D().getMaxY();
				
				firstBoundIter = false;
			}
			else {
				minx = Math.min(minx, p.getBounds2D().getMinX());
				miny = Math.min(miny, p.getBounds2D().getMinY());
				
				maxx = Math.max(maxx, p.getBounds2D().getMaxX());
				maxy = Math.max(maxy, p.getBounds2D().getMaxY());
			}
		}
		
		this.bounds = new Rectangle2D.Double(minx, miny, maxx-minx, maxy-miny);//.setRect(minx, miny, maxx-minx, maxy-miny);
		
		System.out.printf("Got %d polys, with %d intersects, from %d nodes\n", polys.size(), intersects, this.table.allNodes().size());
		System.out.printf("Bounds: %s\n", bounds.toString());
		
		//Calculate view transform based on bounds 
		//( Just center the view around the map )
		this.fitView();
		
		System.out.printf("Table bounds: %s\nView bounds: %s\n", bounds.toString(), this.getBounds().toString());
		System.out.printf("Table center: %f %f\n", bounds.getCenterX(), bounds.getCenterY());
		
		//Assign colors to players
		playerColors.clear();
		
		int i = 0;
		for(long p: table.allPlayers()) {
			if(i == 0)
				playerColors.put(p, Color.white);
			else if(i == 1)
				playerColors.put(p, Color.black);
			else 
				playerColors.put(p, Color.getHSBColor(((i-2) * 0.618033988749895f) % 1.0f, 1.0f, 1.0f));
			
			i++;
		}
		
		//Create piece images in needed colors
		tintedPieceImages.clear(); 
		
		for(Entry<String, Image> e: pieceImages.entrySet()) {
			String name = e.getKey();
			Image image = e.getValue();
			
			tintedPieceImages.addImage(name, image);
			
			for(Color tint: playerColors.values()) {
				System.out.printf("Creating tint %s for %s\n", tint.toString(), name);
				tintedPieceImages.createTinted(name, tint);
			}
		}
	}

	@Override 
	public void paint(Graphics g) {
		this.fitView();
		
		Rectangle clip = g.getClipBounds();
		
		g.setColor(this.getBackground());
		g.fillRect(clip.x, clip.y, clip.width, clip.height);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.transform(viewTransform);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setColor(this.cellColor);
		for(Path2D p : polys.values()) 
			g2.fill(p);
		
		g2.setColor(this.cellHighlightColor);
		for(long id : highlitNodes)
			g2.fill(polys.get(id));

		g2.setColor(this.cellOutlineColor);
		for(Path2D p : polys.values()) 
			g2.draw(p);
		
		g.setColor(Color.black);
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
		g2.setComposite(ac);
			
		for(Entry<Long, Piece> e : table.allPieces()) {
			Piece piece = e.getValue();
			long atId = piece.at();
			Path2D poly = polys.get(atId);
			
			if(poly == null)
				continue; //silent wtf
			
			double midX = poly.getBounds().getCenterX();
			double midY = poly.getBounds().getCenterY();
			
			Image image = pieceImages.get(piece.getName());
			if(image == null)
				g2.drawString(piece.getName(), (float)midX, (float)midY);
			else {
				double targetSize = Math.min(poly.getBounds().getWidth(), poly.getBounds().getHeight());
				double targetScale = targetSize/Math.max(image.getWidth(null), image.getHeight(null));
				
				double targetWidth = image.getWidth(null) * targetScale;
				double targetHeight = image.getHeight(null) * targetScale;
				
				g2.drawImage(tintedPieceImages.getImage(piece.getName(), playerColors.get(piece.player())), 
								(int)(midX - targetWidth/2), (int)(midY - targetHeight/2), 
								(int)targetWidth, (int)targetHeight, null);
			}
		}
	}
	
	//=========================================================================================
	//Node highlights
	
	public void clearHighlights() {
		highlitNodes.clear();
	}
	
	public void highlightNode(long id) {
		if(!polys.containsKey(id))
			return; 
		
		highlitNodes.add(id);
	}
	
	//TODO: Naming could be ambigious, once a node can have more flags
	public void resetNode(long id) {
		highlitNodes.remove(id);
	}
	
	public void toggleNode(long id) {
		if(isNodeHighlit(id))
			resetNode(id);
		else 
			highlightNode(id);
	}
	
	public boolean isNodeHighlit(long id) {
		return highlitNodes.contains(id);
	}
	
	//=========================================================================================
	//Other node functions
	
	//Looks up node based on world-space position ( i.e. before view transform )
	public long nodeAt(double x, double y) {
		for(Entry<Long, Path2D> e : polys.entrySet()) 
			if(e.getValue().contains(x, y))
				return e.getKey();
		
		return -1; 
	}
	
	//Looks up node based on screen-space position ( i.e. after view transform )
	public long nodeAtScreen(double x, double y) {
		Point2D worldPos = new Point2D.Double(x,y);
		inverseViewTransform.transform(worldPos, worldPos);
		
		return this.nodeAt(worldPos.getX(), worldPos.getY());
	}
	
	//=========================================================================================
	//Events 
	public void addListener(TablePanelListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(TablePanelListener listener) {
		this.listeners.remove(listener);
	}
	
	//Perform a node select event
	public void selectNode(long id) {
		if(!table.hasNode(id))
			return; 
		
		for(TablePanelListener listener: this.listeners) 
			listener.nodeSelect(this, id);
	}
	
	public Table getTable() {
		return this.table; 
	}
	
	//=========================================================================================
	//Inner types 
	class NodeSelectListener implements MouseListener {
		private TablePanel parent = null;
		
		public NodeSelectListener(TablePanel parent) {
			this.parent = parent; 
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			long node = parent.nodeAtScreen(e.getX(), e.getY());
			System.out.printf("Node at [%d,%d] is %d\n", e.getX(), e.getY(), node);
			if(node >= 0)
				parent.selectNode(node);
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
