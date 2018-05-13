package com.github.elementbound.nchess.game.operator;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.util.GameStateUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class MoveOperator implements Operator {
    private final Move move;

    public MoveOperator(Move move) {
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public boolean isApplicable(GameState state) {
        if(!isOwnKingPresent(state))
            return false;

        Optional<Piece> possiblePieceAt = state.getPieceAt(move.getFrom());
        if(!possiblePieceAt.isPresent())
            return false;

        Piece pieceAt = possiblePieceAt.get();

        if(!isPieceBelongingToPlayer(state, pieceAt))
            return false;

        if(!isTargetNodeValid(state, move, pieceAt))
            return false;

        return isPieceAbleToDoMove(state, move, pieceAt);
    }

    @Override
    public GameState apply(GameState state) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveOperator that = (MoveOperator) o;
        return Objects.equals(move, that.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(move);
    }

    private boolean isOwnKingPresent(GameState state) {
        return state.getPieces().stream()
                .anyMatch(piece -> piece.getPlayer().equals(state.getCurrentPlayer())  && piece.getName().equals("king"));
    }

    private boolean isPiecePresent(GameState state, Move move) {
        return state.getPieceAt(move.getFrom()).isPresent();
    }

    private boolean isPieceBelongingToPlayer(GameState state, Piece piece) {
        return state.getCurrentPlayer().equals(piece.getPlayer());
    }

    private boolean isTargetNodeValid(GameState state, Move move, Piece piece) {
        return GameStateUtils.isValidTargetNode(state, move.getTo(), piece);
    }

    private boolean isPieceAbleToDoMove(GameState state, Move move, Piece piece) {
        return piece.getMoves(state).stream()
                        .anyMatch(validMove -> validMove.equals(move));
    }
}
