package com.github.elementbound.nchess.game;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.*;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * A node representing a cell in the game table.
 */
public class Node {
	private final long id;

	private final double x;
	private final double y;
	private final boolean visible;
	
	private List<Node> neighbors;
	private List<Node> secondaryNeighbors;

	public Node(long id, double x, double y, boolean visible) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.visible = visible;

		this.neighbors = new ArrayList<>();
		this.secondaryNeighbors = new ArrayList<>();
	}

    public Node(Builder builder) {
        this.id = builder.id;
        this.x = builder.x;
        this.y = builder.y;
        this.visible = builder.visible;
        this.neighbors = builder.neighbors;
        this.secondaryNeighbors = builder.secondaryNeighbors;
    }

    public static Builder builder() {
	    return new Builder();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
	    return new ReflectionToStringBuilder(this)
                .setExcludeFieldNames("neighbors", "secondaryNeighbors")
                .build();
    }

    public static class Builder {
        private long id;
        private double x;
        private double y;
        private boolean visible;
        private List<Node> neighbors;
        private List<Node> secondaryNeighbors;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder x(double x) {
            this.x = x;
            return this;
        }

        public Builder y(double y) {
            this.y = y;
            return this;
        }

        public Builder visible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public Builder neighbors(List<Node> neighbors) {
            this.neighbors = neighbors;
            return this;
        }

        public Builder secondaryNeighbors(List<Node> secondaryNeighbors) {
            this.secondaryNeighbors = secondaryNeighbors;
            return this;
        }

        public Node build() {
            return new Node(this);
        }
    }
}
