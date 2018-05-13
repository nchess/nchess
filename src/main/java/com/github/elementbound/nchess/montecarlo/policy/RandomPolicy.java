package com.github.elementbound.nchess.montecarlo.policy;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.operator.Operator;
import com.github.elementbound.nchess.montecarlo.GameTreeNode;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * Policy that returns a random operator.
 */
public class RandomPolicy implements Policy {
    private final Random random = new Random();


    @Override
    public Operator apply(GameState state, Set<Operator> applicableOperators) {
        int index = random.nextInt(applicableOperators.size());

        Iterator<Operator> iterator = applicableOperators.iterator();
        for(int i = 0; i < index; i++) {
            iterator.next();
        }

        return iterator.next();
    }
}
