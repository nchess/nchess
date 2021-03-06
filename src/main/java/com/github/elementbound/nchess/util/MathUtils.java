package com.github.elementbound.nchess.util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class MathUtils {
	public static Point2D intersectLines(Line2D la, Line2D lb, boolean asSegment) {
		//The following is a port of the script at: 
		//http://www.gmlscripts.com/script/lines_intersect
		
	    double ua = 0;
	    double ux = la.getX2() - la.getX1();
	    double uy = la.getY2() - la.getY1();
	    double vx = lb.getX2() - lb.getX1();
	    double vy = lb.getY2() - lb.getY1();
	    double wx = la.getX1() - lb.getX1();
	    double wy = la.getY1() - lb.getY1();
	    double ud = vy * ux - vx * uy;
	    
	    if (ud != 0) 
	    {
	        ua = (vx * wy - vy * wx) / ud;
	        if (asSegment) 
	        {
	            double ub = (ux * wy - uy * wx) / ud;
	            if (ua < 0 || ua > 1 || ub < 0 || ub > 1) 
	                return null;
	        }
	    }
	    
	    if(ua != 0)
	        return new Point2D.Double(la.getX1() + ua*ux, la.getY1() + ua*uy);
	    else
	        return null; 
	}
	
	//TODO: Table depends on this/these; core shouldn't depend on util, refactor
	public static double vectorDirection(double x, double y) {
		return Math.toDegrees(Math.atan2(y, x))+180;
	}
	
	public static double vectorDirection(double x1, double y1, double x2, double y2) {
		return vectorDirection(x2-x1, y2-y1);
	}
	
	public static double directionSimilarity(double angleA, double angleB) {
		//Currently a dot-product
		double radA = Math.toRadians(angleA);
		double radB = Math.toRadians(angleB);
		
		return Math.cos(radA)*Math.cos(radB) + Math.sin(radA)*Math.sin(radB) ;
	}
}
