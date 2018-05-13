package com.github.elementbound.nchess.montecarlo.agent.montecarlo;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.operator.MoveOperator;
import com.github.elementbound.nchess.game.operator.Operator;
import com.github.elementbound.nchess.game.operator.PassOperator;
import com.github.elementbound.nchess.montecarlo.GameTreeNode;
import com.github.elementbound.nchess.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the Monte Carlo tree search algorithm with UCT.
 */
public class MonteCarloUctAgent extends BaseMonteCarloAgent {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonteCarloUctAgent.class);
    private static final Operator PASS = new PassOperator();

    protected GameTreeNode select(GameTreeNode at) {
        while(!at.getChildren().isEmpty()) {
            at = selectNode(at);
        }

        LOGGER.info("Selected {} with {} confidence", at, calculateConfidence(at));
        return at;
    }

    protected GameTreeNode selectNode(GameTreeNode at) {
        return at.getChildren().values().stream()
                .min((a, b) -> (int) Math.signum(calculateConfidence(b) - calculateConfidence(b)))
                .get();
    }

    protected GameTreeNode expand(GameTreeNode node) {
        GameState state = node.getState();

        Set<Operator> applicableOperators = gatherApplicableOperators(state);

        Operator operator = CollectionUtils.getRandomFrom(applicableOperators);
        return node.expand(operator);
    }

    protected Set<Operator> gatherApplicableOperators(GameState state) {
        if(PASS.isApplicable(state)) {
            return Collections.singleton(PASS);
        } else {
            return state.getPieces().stream()
                    .filter(piece -> piece.getPlayer().equals(state.getCurrentPlayer()))
                    .flatMap(piece -> piece.getMoves(state).stream())
                    .map(MoveOperator::new)
                    .collect(Collectors.toSet());
        }
    }

    /**
     * Calculates confidence in node with the UCT formula. The higher the better chances of winning.
     * @return confidence
     */
    private double calculateConfidence(GameTreeNode node) {
        return (node.getWinCount() / (double)node.getSimulationCount()) +
                1.41421356 * Math.sqrt((Math.log(gameTree.getSimulationCount())) / node.getSimulationCount());
    }
}
