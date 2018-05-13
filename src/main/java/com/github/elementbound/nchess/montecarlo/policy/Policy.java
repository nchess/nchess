package com.github.elementbound.nchess.montecarlo.policy;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.operator.Operator;

import java.util.Set;

/**
 * A policy defines how to play during playouts.
 */
public interface Policy {
    /**
     * Applies the policy to a game tree node, and returns the operator to use for expansion.
     * @param state game state
     * @param applicableOperators to choose from
     * @return operator to use
     */
    Operator apply(GameState state, Set<Operator> applicableOperators);
}
