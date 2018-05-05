package com.github.elementbound.nchess.game;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;

import com.github.elementbound.nchess.util.MathUtils;
import javafx.scene.control.Tab;

public class Table {
	private final Set<Node> nodes;

    public Table(Set<Node> nodes) {
        this.nodes = nodes;
    }

	public Set<Node> getNodes() {
		return Collections.unmodifiableSet(nodes);
	}

	public double linkDirection(Node a, Node b) {
		return MathUtils.vectorDirection(a.getX(), a.getY(), b.getX(), b.getY());
	}

	// TODO: Where to move this even? Probably to Node?
    @Deprecated
	public boolean isLink(Node from, Node to) {
		return from.getNeighbors().contains(to);
	}

	// TODO: Move to Node?
	@Deprecated
	public boolean isSecondaryLink(Node from, Node to) {
        return from.getSecondaryNeighbors().contains(to);
	}

	// TODO: Move to own component?
	// TODO: Refactor and unit test; move?
	public Node nodeTowardsDirection(Node from, double dir) {
        Function<Node, Double> directionSimilarity =
                to -> MathUtils.directionSimilarity(dir, linkDirection(from, to));

        return from.getNeighbors().stream()
                .max((a, b) -> (int) (directionSimilarity.apply(a) - directionSimilarity.apply(b)))
                .orElse(null);
	}

    // TODO: Refactor and unit test; and move?
	public Node secondaryNodeTowardsDirection(Node from, double dir) {
        Function<Node, Double> directionSimilarity =
                to -> MathUtils.directionSimilarity(dir, linkDirection(from, to));

        return from.getSecondaryNeighbors().stream()
                .max((a, b) -> (int) (directionSimilarity.apply(a) - directionSimilarity.apply(b)))
                .orElse(null);
	}

    public static Builder builder() {
	    return new Builder();
    }

    public static class Builder {
	    private Set<Node> nodes = new HashSet<>();

	    private Builder() {
        }

        public Builder withNode(Node node) {
	        nodes.add(node);
	        return this;
        }

        public Table build() {
            nodes.forEach(Node::gatherSecondaryNeighbors);
            return new Table(nodes);
        }
    }
}
