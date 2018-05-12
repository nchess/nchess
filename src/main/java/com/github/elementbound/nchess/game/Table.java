package com.github.elementbound.nchess.game;

import com.github.elementbound.nchess.util.MathUtils;
import com.github.elementbound.nchess.util.TableUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class Table {
	private final Set<Node> nodes;

    public Table(Set<Node> nodes) {
        this.nodes = nodes;
    }

	public Set<Node> getNodes() {
		return Collections.unmodifiableSet(nodes);
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
    /**
     * <p>Return {@code from}'s neighbor that is closest to {@code direction}.
     * @param from starting node; can be null
     * @param dir target direction
     * @return best fitting node or null if none found
     */
	public Node nodeTowardsDirection(Node from, double dir) {
        if(from == null) {
            return null;
        }

        Function<Node, Double> directionSimilarity =
                to -> MathUtils.directionSimilarity(dir, TableUtils.linkDirection(from, to));

        return from.getNeighbors().stream()
                .max((a, b) -> (int) Math.signum(directionSimilarity.apply(a) - directionSimilarity.apply(b)))
                .orElse(null);
	}

    // TODO: Refactor and unit test; and move?
    /**
     * <p>Return {@code from}'s secondary neighbor that is closest to {@code direction}.
     * @param from starting node
     * @param dir target direction
     * @return best fitting node or null if none found
     */
	public Node secondaryNodeTowardsDirection(Node from, double dir) {
        Function<Node, Double> directionSimilarity =
                to -> MathUtils.directionSimilarity(dir, TableUtils.linkDirection(from, to));

        return from.getSecondaryNeighbors().stream()
                .max((a, b) -> (int) Math.signum(directionSimilarity.apply(a) - directionSimilarity.apply(b)))
                .orElse(null);
	}

    public static Builder builder() {
	    return new Builder();
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this)
                .setExcludeFieldNames("nodes")
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(nodes, table.nodes);
    }

    @Override
    public int hashCode() {

        return Objects.hash(nodes);
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
