package com.github.elementbound.nchess.game;

import com.github.elementbound.nchess.util.MathUtils;
import com.github.elementbound.nchess.util.TableUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Class representing the game table.
 */
public class Table {
    private final Set<Node> nodes;

    public Table(Set<Node> nodes) {
        this.nodes = nodes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Set<Node> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    // TODO: Where to move this even? Probably to Node?
    @Deprecated
    public boolean isLink(Node from, Node to) {
        return from.getNeighbors().contains(to);
    }

    // TODO: Move to own component?
    // TODO: Refactor and unit test; move?

    // TODO: Move to Node?
    @Deprecated
    public boolean isSecondaryLink(Node from, Node to) {
        return from.getSecondaryNeighbors().contains(to);
    }

    // TODO: Refactor and unit test; and move?

    /**
     * <p>Return {@code from}'s neighbor that is closest to {@code direction}.
     *
     * @param from starting node; can be null
     * @param dir  target direction
     * @return best fitting node or null if none found
     */
    public Node nodeTowardsDirection(Node from, double dir) {
        if (from == null) {
            return null;
        }

        Function<Node, Double> directionSimilarity =
                to -> MathUtils.directionSimilarity(dir, TableUtils.linkDirection(from, to));

        return from.getNeighbors().stream()
                .max((a, b) -> (int) Math.signum(directionSimilarity.apply(a) - directionSimilarity.apply(b)))
                .orElse(null);
    }

    /**
     * <p>Return {@code from}'s secondary neighbor that is closest to {@code direction}.
     *
     * @param from starting node
     * @param dir  target direction
     * @return best fitting node or null if none found
     */
    public Node secondaryNodeTowardsDirection(Node from, double dir) {
        Function<Node, Double> directionSimilarity =
                to -> MathUtils.directionSimilarity(dir, TableUtils.linkDirection(from, to));

        return from.getSecondaryNeighbors().stream()
                .max((a, b) -> (int) Math.signum(directionSimilarity.apply(a) - directionSimilarity.apply(b)))
                .orElse(null);
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this)
                .setExcludeFieldNames("nodes")
                .build();
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Builder for {@link Table}.
     */
    public static final class Builder {
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