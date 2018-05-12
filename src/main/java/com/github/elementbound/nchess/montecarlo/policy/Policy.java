package com.github.elementbound.nchess.montecarlo.policy;

import com.github.elementbound.nchess.game.operator.Operator;
import com.github.elementbound.nchess.montecarlo.GameTreeNode;

import java.util.Set;

/**
 * A policy defines how to play during playouts.
 */
public interface Policy {
    /**
     * Applies the policy to a game tree node, and returns the operator to use for expansion.
     * @param node to expand
     * @param applicableOperators to choose from
     * @return operator to use
     */
    Operator apply(GameTreeNode node, Set<Operator> applicableOperators);
}
