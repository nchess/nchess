package com.github.elementbound.nchess.game.operator;

import com.github.elementbound.nchess.game.GameState;

public interface Operator {
    boolean isApplicable(GameState state);
    GameState apply(GameState state);
}
