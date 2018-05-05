package com.github.elementbound.nchess.game;

import java.util.*;

/**
 * A node representing a cell in the game table.
 */
public class Node {
	private final Table parent;
	private final long id;

	private final double x;
	private final double y;
	private final boolean visible;
	
	private List<Node> neighbors;
	private List<Node> secondaryNeighbors;

	public Node(Table parent, long id, double x, double y, boolean visible) {
		this.parent = parent;
		this.id = id;
		this.x = x;
		this.y = y;
		this.visible = visible;

		this.neighbors = new ArrayList<>();
		this.secondaryNeighbors = new ArrayList<>();
	}

	// TODO: Move to builder, nodes shouldn't be mutable
    @Deprecated
	public void link(Node node) {
		if(!neighbors.contains(node)) {
			neighbors.add(node);
		}
	}

	// TODO: Should be moved to some kind of preprocessor
	@Deprecated
	public void gatherSecondaryNeighbors() {
		secondaryNeighbors.clear();
		for(int i = 0; i < neighbors.size(); i++) {
			int j = (i+1) % neighbors.size();

			Set<Node> neighborsA = new HashSet<>();
			Set<Node> neighborsB = new HashSet<>();
			
			Node nodeA = neighbors.get(i);
			Node nodeB = neighbors.get(j);
			
			neighborsA.addAll(nodeA.neighbors);
			neighborsB.addAll(nodeB.neighbors);

			Set<Node> secondary = new HashSet<>(neighborsA);
			secondary.retainAll(neighborsB);
			secondary.remove(this);
			
			secondaryNeighbors.addAll(secondary);
		}
	}

    public Table getParent() {
        return parent;
    }

    public long getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isVisible() {
        return visible;
    }

    public List<Node> getNeighbors() {
        return Collections.unmodifiableList(neighbors);
    }

    public List<Node> getSecondaryNeighbors() {
        return Collections.unmodifiableList(secondaryNeighbors);
    }
}
