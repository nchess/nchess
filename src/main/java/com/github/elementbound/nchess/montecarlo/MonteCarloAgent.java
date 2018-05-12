package com.github.elementbound.nchess.montecarlo;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.operator.Operator;
import com.github.elementbound.nchess.montecarlo.policy.Policy;
import com.github.elementbound.nchess.montecarlo.policy.RandomPolicy;
import com.github.elementbound.nchess.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the Monte Carlo tree search algorithm.
 */
public class MonteCarloAgent {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonteCarloAgent.class);

    private static final int ITERATION_COUNT = 32;

    private final Set<Operator> operators;
    private final Policy policy = new RandomPolicy();

    private GameTreeNode gameTree;

    public MonteCarloAgent(Set<Operator> operators) {
        this.operators = operators;
    }

    /**
     * Selects the most promising node for expansion
     * @param root to inspect
     * @return node to simulate
     */
    private GameTreeNode selectNode(GameTreeNode root) {
        // TODO: UCB selection
        return CollectionUtils.getRandomFrom(root.getChildren().values());
    }

    private Operator selectOperator(GameTreeNode root) {
        // TODO: UCB selection
        return CollectionUtils.getRandomFrom(root.getChildren().keySet());
    }

    /**
     * Expands node with all immediately applicable operators
     * @param node to expand
     */
    private void expand(GameTreeNode node) {
        GameState state = node.getState();

        Set<Operator> applicableOperators = gatherApplicableOperators(state);

        applicableOperators.forEach(operator -> node.expand(operator));
    }

    /**
     * Plays the game with random moves until the current player wins or loses.
     * @param node to play from
     */
    private void playout(GameTreeNode node) {
        GameTreeNode at = node;

        while(!isEndNode(at)) {
            Set<Operator> applicableOperators = gatherApplicableOperators(at.getState());
            Operator operator = policy.apply(at, applicableOperators);

            at = at.expand(operator);
        }
    }

    private boolean isEndNode(GameTreeNode at) {
        return at.getState().getWinner().isPresent();
    }

    public Operator advise(GameState state) {
        // Set new root for game tree
        gameTree = new GameTreeNode(state, null);

        // Do playouts for each
        expand(gameTree);
        gameTree.getChildren().values()
                .forEach(this::playout);

        // Do a few iterations
        for(int i = 0; i < ITERATION_COUNT; i++) {
            //select
            GameTreeNode toExpand = selectNode(gameTree);

            //expand
            expand(toExpand);

            //simulate
            toExpand.getChildren().values().forEach(this::playout);
        }

        return selectOperator(gameTree);
    }

    private Set<Operator> gatherApplicableOperators(GameState state) {
        return operators.stream()
                    .filter(operator -> operator.isApplicable(state))
                    .collect(Collectors.toSet());
    }
}
