package com.github.elementbound.nchess.montecarlo;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Player;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameTreeNode {
    private final GameState state;
    private final GameTreeNode parent;
    private final Set<GameTreeNode> children;

    private Map<Player, Long> winCounts = new HashMap<>();

    public GameTreeNode(GameState state, GameTreeNode parent, Set<GameTreeNode> children) {
        this.state = state;
        this.parent = parent;
        this.children = children;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this).setExcludeFieldNames("children").toString();
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
