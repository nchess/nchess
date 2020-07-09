package com.github.elementbound.nchess.montecarlo.agent.montecarlo;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Player;
import com.github.elementbound.nchess.game.operator.Operator;
import com.github.elementbound.nchess.montecarlo.GameTreeNode;
import com.github.elementbound.nchess.montecarlo.agent.AdvisorAgent;
import com.github.elementbound.nchess.montecarlo.policy.Policy;
import com.github.elementbound.nchess.montecarlo.policy.RandomPolicy;

/**
 * Template implementation of the Monte Carlo tree search algorithm.
 */
public abstract class BaseMonteCarloAgent implements AdvisorAgent {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonteCarloUctAgent.class);

    private static final int ITERATION_TIME = 10000;

    private final Policy policy = new RandomPolicy();

    protected GameTreeNode gameTree;

    /**
     * Selects the most promising node for expansion
     *
     * @return node to simulate
     */
    protected abstract GameTreeNode select(GameTreeNode at);

    /**
     * Descend towards most promising node
     * @param at starting node
     * @return most promising child node
     */
    protected abstract GameTreeNode selectNode(GameTreeNode at);

    /**
     * Select most promising operator
     * @param at starting node
     * @return most promising operator
     */
    private Operator selectOperator(GameTreeNode at) {
        GameTreeNode desiredNode = selectNode(at);

        LOGGER.info("Selecting operator for {} at ~{}% win rate", at, 100 * at.getWinCount() / at.getSimulationCount());

        return at.getChildren().entrySet().stream()
                .filter(p -> p.getValue().equals(desiredNode))
                .map(Map.Entry::getKey)
                .findAny()
                .get();
    }

    /**
     * Expands node with a chosen operator
     *
     * @param node to expand
     * @return node to simulate
     */
    protected abstract GameTreeNode expand(GameTreeNode node);


    /**
     * Plays the game with random moves until the current player wins or loses.
     *
     * @param node to play from
     */
    private void playout(GameTreeNode node) {
        GameState at = node.getState();
        int length = 0;
        StopWatch stopWatch = StopWatch.createStarted();

        while (!at.getWinner().isPresent()) {
            Set<Operator> applicableOperators = gatherApplicableOperators(at);
            Operator operator = policy.apply(at, applicableOperators);

            at = operator.apply(at);
            ++length;
        }

        Player originalPlayer = node.getState().getCurrentPlayer();
        boolean isWin = originalPlayer.equals(at.getWinner().get());
        node.backpropagate(isWin);

        stopWatch.stop();
        LOGGER.info("Playouts for {} with {} steps in {} ms", new Object[]{node, length, stopWatch});
    }

    /**
     * Gathers all applicable operators for a given state
     * @param state state
     * @return a set of applicable operators
     */
    protected abstract Set<Operator> gatherApplicableOperators(GameState state);

    public Operator advise(GameState state) {
        // Set new root for game tree
        gameTree = new GameTreeNode(state, null);

        StopWatch stopWatch = StopWatch.createStarted();
        for (int i = 0; stopWatch.getTime() < ITERATION_TIME; i++) {

            GameTreeNode toExpand = select(gameTree);
            GameTreeNode toSimulate = expand(toExpand);
            playout(toSimulate);

            LOGGER.info("Iteration {} at {}", i, stopWatch);
        }

        return selectOperator(gameTree);
    }
}
