package com.github.elementbound.nchess.game;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Immutable class containing a discrete state of the game.
 */
public class GameState {
    private final Table table;
    private final Set<Piece> pieces;
    private final List<Player> players;
    private final Player currentPlayer;

    public GameState(Table table, Set<Piece> pieces, List<Player> players, Player currentPlayer) {
        this.table = table;
        this.pieces = pieces;
        this.players = players;
        this.currentPlayer = currentPlayer;
    }

    public Table getTable() {
        return table;
    }

    public Set<Piece> getPieces() {
        return Collections.unmodifiableSet(pieces);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}
