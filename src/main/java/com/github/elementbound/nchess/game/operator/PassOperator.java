package com.github.elementbound.nchess.game.operator;

import com.github.elementbound.nchess.game.GameState;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class PassOperator implements Operator {
    @Override
    public boolean isApplicable(GameState state) {
        return state.getPieces().stream()
                .filter(piece -> piece.getName().equals("king"))
                .noneMatch(piece -> piece.getPlayer().equals(state.getCurrentPlayer()));
    }

    @Override
    public GameState apply(GameState state) {
        return GameState.builder()
                .gameState(state)
                .currentPlayer(state.getNextPlayer())
                .build();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
