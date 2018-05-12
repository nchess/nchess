package com.github.elementbound.nchess.game;

import com.github.elementbound.nchess.game.exception.InvalidMoveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    private final MoveValidator moveValidator = new MoveValidator();

    public GameState(Builder builder) {
        this.table = builder.table;
        this.pieces = builder.pieces;
        this.players = builder.players;
        this.currentPlayer = builder.currentPlayer;
    }

    // TODO: Maybe move to its own component?
    public Player getNextPlayer(Player player) {
        int nextIndex = (players.indexOf(player) + 1) % players.size();
        return players.get(nextIndex);
    }

    public Player getNextPlayer() {
        return getNextPlayer(currentPlayer);
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

    public Optional<Player> getWinner() {
        List<Player> playersWithKing = pieces.stream()
                .filter(piece -> piece.getName().equals("king"))
                .map(Piece::getPlayer)
                .distinct()
                .collect(Collectors.toList());

        return playersWithKing.size() == 1 ?
                Optional.of(playersWithKing.get(0)) :
                Optional.empty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
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
