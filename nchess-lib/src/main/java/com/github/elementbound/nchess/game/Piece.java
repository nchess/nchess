package com.github.elementbound.nchess.game;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public abstract class Piece {
	protected final Node at;
	protected final Player player;

    public Piece(Node at, Player player) {
        this.at = at;
        this.player = player;
    }

    public abstract String getName();
	public abstract Set<Move> getMoves(Table table);

    public Node getAt() {
        return at;
    }

    public Player getPlayer() {
        return player;
    }
	
	public boolean hasMove(Move moveCandidate, Table table) {
		return getMoves(table).stream()
                .anyMatch(moveCandidate::equals);
	}

    /**
     * <p>Returns a new piece of the same kind, now standing on a different node.
     * <p>This does not modify the original instance.
     * @param to where to move
     * @return modified instance
     */
	public abstract Piece move(Node to);

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, SHORT_PREFIX_STYLE);
	}

	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
