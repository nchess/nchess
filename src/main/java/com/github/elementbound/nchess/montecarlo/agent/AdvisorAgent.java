package com.github.elementbound.nchess.montecarlo.agent;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.operator.Operator;

public interface AdvisorAgent {
    Operator advise(GameState state);
}
