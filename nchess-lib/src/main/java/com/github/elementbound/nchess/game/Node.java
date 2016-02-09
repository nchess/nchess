package com.github.elementbound.nchess.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Node {
	//TODO: avoid circular dependencies in some nice way
	private Table parent = null;
	private long id; 
	
	private double x;
	private double y; 
	private boolean visible; 
	
	private List<Long> neighbors;
	private List<Long> secondaryNeighbors; 
	
	private boolean dirty = true; 
	
	private void sortLinks() {
		final Table table = this.parent; 
		final long at = this.id; 
		
		//POLISH: Maybe make this a less anonymous class? 
		Collections.sort(neighbors, new Comparator<Long>() {
			@Override
			public int compare(Long o1, Long o2) {
				double dir1 = table.linkDirection(at, o1);
				double dir2 = table.linkDirection(at, o2);
				return Double.compare(dir1, dir2);
			}
		});
	}
	
	public Node(Table parent, long id, double x, double y, boolean visible) {
		this.parent = parent; 
		this.id = id;
		this.x = x; 
		this.y = y; 
		this.visible = visible; 
		
		//TODO: Organize neighbors on some criteria 
		this.neighbors = new ArrayList<>();
		this.secondaryNeighbors = new ArrayList<>();
	}
	
	public void link(long toId) {
		if(!neighbors.contains(toId)) {
			neighbors.add(toId);
			dirty = true;
		}
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
	
	public int secondaryNeighborCount() {
		return secondaryNeighbors.size();
	}
	
	public long secondaryNeighbor(int i) {
		return secondaryNeighbors.get(i);
	}
	
	public void gatherSecondaryNeighbors() {
		secondaryNeighbors.clear();
		for(int i = 0; i < neighbors.size(); i++) {
			int j = (i+1) % neighbors.size(); 
			
			Set<Long> neighborsA = new TreeSet<>();
			Set<Long> neighborsB = new TreeSet<>();
			
			Node nodeA = parent.getNode(this.neighbor(i));
			Node nodeB = parent.getNode(this.neighbor(j));
			
			neighborsA.addAll(nodeA.neighbors);
			neighborsB.addAll(nodeB.neighbors);
			Set<Long> secondary = new TreeSet<>(neighborsA);
			secondary.retainAll(neighborsB);
			secondary.remove(id);
			
			secondaryNeighbors.addAll(secondary);
		}
		
		dirty = false; 
	}
}
