package com.github.elementbound.nchess.game;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private long id; 
	
	private double x;
	private double y; 
	private boolean visible; 
	
	private List<Long> neighbors;
	
	public Node(long id, double x, double y, boolean visible) {
		this.id = id;
		this.x = x; 
		this.y = y; 
		this.visible = visible; 
		
		//TODO: Organize neighbors on some criteria 
		this.neighbors = new ArrayList<>();
	}
	
	public void link(long toId) {
		if(!neighbors.contains(toId))
			neighbors.add(toId);
	}
	
	public long id() {
		return this.id; 
	}
	
	public double x() {
		return this.x;
	}
	
	public double y() {
		return this.y;
	}
	
	public boolean visible() {
		return this.visible; 
	}
	
	public int neighborCount() {
		return neighbors.size();
	}
	
	public long neighbor(int i) {
		return neighbors.get(i);
	}
}
