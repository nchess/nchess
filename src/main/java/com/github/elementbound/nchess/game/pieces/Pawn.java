package com.github.elementbound.nchess.game.pieces;

import com.github.elementbound.nchess.game.*;
import com.github.elementbound.nchess.util.GameStateUtils;
import com.github.elementbound.nchess.util.TableUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>Class to represent the Pawn piece.
 * <p>
 *     On their first step, pawns can choose any ( non-diagonal ) direction, to take a single step.
 *     After this step has been made, pawns will have to stick to that direction.
 * <p>
 *     Pawns can take down other pieces in any direction, except their chosen direction, and their previous position.
 */
public class Pawn extends Piece {
	private Optional<Double> direction;

    public Pawn(Node at, Player player) {
        super(at, player);
        this.direction = Optional.empty();
    }

    public Pawn(Node at, Player player, double direction) {
        super(at, player);
        this.direction = Optional.of(direction);
    }

	private boolean hasMoved() {
	    return direction.isPresent();
    }

	@Override
	public String getName() {
		return "pawn";
	}

	// TODO: Pawn hits and blockages need to be implemented
	@Override
	public Set<Move> getMoves(GameState state) {
        Table table = state.getTable();

        if(!hasMoved()) {
            return at.getNeighbors().stream()
                    .filter(to -> GameStateUtils.isValidTargetNode(state, to, this))
                    .map(to -> new Move(at, to))
                    .collect(Collectors.toSet());
        } else {
            Set<Move> result = new HashSet<>();

            Optional.ofNullable(table.nodeTowardsDirection(at, direction.get()))
                    .filter(to -> GameStateUtils.isValidTargetNode(state, to, this))
                    .map(to -> new Move(at, to))
                    .ifPresent(result::add);

            return result;
        }
	}

    @Override
    public Piece move(Node to) {
        return new Pawn(to, player, TableUtils.linkDirection(at, to));
    }
}
