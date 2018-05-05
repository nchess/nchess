package com.github.elementbound.nchess.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable class containing a discrete state of the game.
 */
public class GameState {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameState.class);

    private final Table table;
    private final Set<Piece> pieces;
    private final List<Player> players;
    private final Player currentPlayer;

    public GameState(Builder builder) {
        this.table = builder.table;
        this.pieces = builder.pieces;
        this.players = builder.players;
        this.currentPlayer = builder.currentPlayer;
    }

    // TODO: Maybe move to its own component?
    // TODO: Possibly throw instead of returning a boolean?
    public boolean validateMove(Move move) {
        // TODO: check if the right player is moving the piece
        // TODO: check if the piece can do that move
        // TODO: check if moving over an allied piece
        return true;
    }

    // TODO: Maybe move to its own component?
    private Player nextPlayer(Player player) {
        int nextIndex = (players.indexOf(player) + 1) % players.size();
        return players.get(nextIndex);
    }

    // TODO: Maybe move to its own component?
    public GameState applyMove(Move move) {
        LOGGER.info("Applying move: {}", move);

        if(!validateMove(move)) {
            LOGGER.error("Invalid move: {}", move);
            // TODO: proper throw
            throw new RuntimeException("Invalid move!");
        }

        //Perform move
        Set<Piece> resultingPieces = pieces.stream()
                .filter(piece -> ! move.getTo().equals(piece.getAt())) // Exclude piece we are stepping over
                .map(piece ->
                    piece.getAt().equals(move.getFrom()) ?
                    piece.move(move.getTo()) :
                    piece
                )
                .collect(Collectors.toSet());

        return builder()
                .table(table)
                .players(players)
                .pieces(resultingPieces)
                .currentPlayer(nextPlayer(currentPlayer))
                .build();
    }

    @Deprecated
    public Set<Move> getMovesByPlayer(Player player) {
        return pieces.stream()
                .filter(piece -> piece.getPlayer().equals(player))
                .flatMap(piece -> piece.getMoves(this).stream())
                .collect(Collectors.toSet());
    }

    public Table getTable() {
        return table;
    }

    public Set<Piece> getPieces() {
        return Collections.unmodifiableSet(pieces);
    }

    /**
     * Get piece at node.
     * @param at to search
     * @return optional piece
     */
    public Optional<Piece> getPieceAt(Node at) {
        return pieces.stream()
                .filter(piece -> at.equals(piece.getAt()))
                .findFirst();
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public static Builder builder() {
        return new Builder();
    }

    private static class Builder {
        private Table table;
        private Set<Piece> pieces;
        private List<Player> players;
        private Player currentPlayer;

        private Builder() {

        }

        public Builder gameState(GameState gameState) {
            this.table = gameState.table;
            this.pieces = gameState.pieces;
            this.players = gameState.players;
            this.currentPlayer = gameState.currentPlayer;
            return this;
        }

        public Builder table(Table table) {
            this.table = table;
            return this;
        }

        public Builder pieces(Set<Piece> pieces) {
            this.pieces = pieces;
            return this;
        }

        public Builder players(List<Player> players) {
            this.players = players;
            return this;
        }

        public Builder currentPlayer(Player currentPlayer) {
            this.currentPlayer = currentPlayer;
            return this;
        }

        public GameState build() {
            return new GameState(this);
        }
    }
}
