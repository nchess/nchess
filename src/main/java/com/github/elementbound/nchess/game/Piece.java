package com.github.elementbound.nchess.game;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Objects;
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

    public abstract Set<Move> getMoves(GameState state);

    public Node getAt() {
        return at;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean hasMove(Move moveCandidate, GameState state) {
        return getMoves(state).stream()
                .anyMatch(moveCandidate::equals);
    }

    /**
     * <p>Returns a new piece of the same kind, now standing on a different node.
     * <p>This does not modify the original instance.
     *
     * @param to where to move
     * @return modified instance
     */
    public abstract Piece move(Node to);

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return Objects.equals(at, piece.at) &&
                Objects.equals(player, piece.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(at, player);
    }
}
