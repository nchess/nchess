package com.github.elementbound.nchess.game;

import java.util.ArrayList;
import java.util.List;

class Node {
	private long id; 
	
	private double x;
	private double y; 
	
	private List<Long> neighbors;
	
	public Node(long id, double x, double y) {
		this.id = id;
		this.x = x; 
		this.y = y; 
		
		//TODO: Organize neighbors on some criteria 
		this.neighbors = new ArrayList<>();
	}
	
	public void link(long toId) {
		if(!neighbors.contains(toId))
			neighbors.add(toId);
	}
}
