package com.github.elementbound.nchess.montecarlo;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Player;
import com.github.elementbound.nchess.game.operator.Operator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GameTreeNode {
    private final GameState state;
    private final GameTreeNode parent;

    private final Map<Operator, GameTreeNode> children = new HashMap<>();

    private Map<Player, Long> winCounts = new HashMap<>();

    public GameTreeNode(GameState state, GameTreeNode parent) {
        this.state = state;
        this.parent = parent;
    }

    /**
     * Applies operator to the node's game state, and creates a new child from the result.
     * @param operator to use
     */
    public GameTreeNode expand(Operator operator) {
        if(!operator.isApplicable(state))
            return null;

        if(children.containsKey(operator)) {
            return children.get(operator);
        }

        GameState newState = operator.apply(state);
        GameTreeNode result = new GameTreeNode(newState, this);
        children.put(operator, result);

        return result;
    }

    public GameState getState() {
        return state;
    }

    public GameTreeNode getParent() {
        return parent;
    }

    public Map<Operator, GameTreeNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this).setExcludeFieldNames("children", "parent").toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameTreeNode that = (GameTreeNode) o;
        return Objects.equals(state, that.state) &&
                Objects.equals(parent, that.parent) &&
                Objects.equals(children, that.children) &&
                Objects.equals(winCounts, that.winCounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, parent);
    }
}
