package com.github.elementbound.nchess.game.operator;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.util.GameStateUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class MoveOperator implements Operator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoveOperator.class);

    private final Move move;

    public MoveOperator(Move move) {
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public boolean isApplicable(GameState state) {
        return isPiecePresent(state, move) &&
                isPieceBelongingToPlayer(state, move) &&
                isTargetNodeValid(state, move) &&
                isPieceAbleToDoMove(state, move);
    }

    @Override
    public GameState apply(GameState state) {
        LOGGER.info("Applying move: {}", move);

        //Perform move
        Set<Piece> resultingPieces = state.getPieces().stream()
                .filter(piece -> ! move.getTo().equals(piece.getAt())) // Exclude piece we are stepping over
                .map(piece ->
                        piece.getAt().equals(move.getFrom()) ?
                                piece.move(move.getTo()) :
                                piece
                )
                .collect(Collectors.toSet());

        return GameState.builder()
                .gameState(state)
                .pieces(resultingPieces)
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

    private boolean isPiecePresent(GameState state, Move move) {
        return state.getPieceAt(move.getFrom()).isPresent();
    }

    private boolean isPieceBelongingToPlayer(GameState state, Move move) {
        return state.getPieceAt(move.getFrom())
                .filter(p -> p.getPlayer().equals(state.getCurrentPlayer()))
                .isPresent();
    }

    private boolean isTargetNodeValid(GameState state, Move move) {
        return state.getPieceAt(move.getFrom())
                .filter(p -> GameStateUtils.isValidTargetNode(state, move.getTo(), p))
                .isPresent();
    }

    private boolean isPieceAbleToDoMove(GameState state, Move move) {
        return state.getPieceAt(move.getFrom()).filter(piece ->
                piece.getMoves(state).stream()
                        .anyMatch(validMove -> validMove.equals(move))
        ).isPresent();
    }
}
