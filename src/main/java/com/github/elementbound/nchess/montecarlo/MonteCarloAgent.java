package com.github.elementbound.nchess.montecarlo;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Player;
import com.github.elementbound.nchess.game.operator.MoveOperator;
import com.github.elementbound.nchess.game.operator.Operator;
import com.github.elementbound.nchess.montecarlo.policy.Policy;
import com.github.elementbound.nchess.montecarlo.policy.RandomPolicy;
import com.github.elementbound.nchess.util.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of the Monte Carlo tree search algorithm.
 */
public class MonteCarloAgent {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonteCarloAgent.class);

    private static final int ITERATION_COUNT = 32;
    public static final int ITERATION_TIME = 30000;

    private final Policy policy = new RandomPolicy();

    private GameTreeNode gameTree;

    @Deprecated
    public MonteCarloAgent(Set<Operator> operators) {

    }

    /**
     * Selects the most promising node for expansion
     *
     * @return node to simulate
     */
    private GameTreeNode select(GameTreeNode at) {
        while(!at.getChildren().isEmpty()) {
            at = selectNode(at);
        }

        return at;
    }

    private GameTreeNode selectNode(GameTreeNode at) {
        Function<GameTreeNode, Double> uctScore = node ->
                (node.getWinCount() / (double)node.getSimulationCount()) +
                1.41421356 * Math.sqrt((Math.log(gameTree.getSimulationCount())) / node.getSimulationCount());

        return at.getChildren().values().stream()
                .sorted((a, b) -> (int)Math.signum(uctScore.apply(b) - uctScore.apply(a)))
                .findFirst()
                .get();
    }

    private Operator selectOperator(GameTreeNode at) {
        GameTreeNode desiredNode = selectNode(at);

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
    private GameTreeNode expand(GameTreeNode node) {
        GameState state = node.getState();

        Set<Operator> applicableOperators = gatherApplicableOperators(state);
        Operator operator = CollectionUtils.getRandomFrom(applicableOperators);

        return node.expand(operator);
    }

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

    private Set<Operator> gatherApplicableOperators(GameState state) {
        return state.getPieces().stream()
                .filter(piece -> piece.getPlayer().equals(state.getCurrentPlayer()))
                .flatMap(piece -> piece.getMoves(state).stream())
                .map(MoveOperator::new)
                .filter(operator -> operator.isApplicable(state))
                .collect(Collectors.toSet());
    }
}
