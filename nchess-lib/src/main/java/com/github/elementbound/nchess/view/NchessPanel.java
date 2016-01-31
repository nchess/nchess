package com.github.elementbound.nchess.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JPanel;

import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.util.MathUtils;

public class NchessPanel extends JPanel {
	private Table table; 
	private List<Path2D> polys = new ArrayList<>();
	private Rectangle2D bounds = new Rectangle2D.Double();
	
	public Color cellColor = Color.lightGray;
	public Color cellOutlineColor = Color.black; 
	
	public NchessPanel() {
		//Make it fancy
		this.setDoubleBuffered(true);
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
			polys.add(poly);
		}
		
		//Calculate table bounds based on polys
		boolean firstBoundIter = true;
		double minx = Double.NaN; double miny = Double.NaN;
		double maxx = Double.NaN; double maxy = Double.NaN;
		for(Path2D p : polys) {
			if(firstBoundIter) {
				System.out.println("Bounds is NaN, using values");
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
	}

	@Override 
	public void paint(Graphics g) {
		Rectangle clip = g.getClipBounds();
		
		g.setColor(this.getBackground());
		g.fillRect(clip.x, clip.y, clip.width, clip.height);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.translate(-bounds.getMinX(), -bounds.getMinY());
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2.setColor(this.cellColor);
		for(Path2D p : polys) 
			g2.fill(p);
		
		g2.setColor(this.cellOutlineColor);
		for(Path2D p : polys) 
			g2.draw(p);
	}
}
