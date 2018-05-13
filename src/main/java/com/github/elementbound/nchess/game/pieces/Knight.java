package com.github.elementbound.nchess.game.pieces;

import com.github.elementbound.nchess.game.*;
import com.github.elementbound.nchess.util.GameStateUtils;
import com.github.elementbound.nchess.util.TableUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>Class to represent the Knight piece.
 * <p>
 *     The knight can take two steps in an arbitrary direction, then take another step in a different direction.
 *     The last step can't be taken backwards, meaning the knight can't step on the first node it moved onto
 *     in the process. Also, the last step must be taken in a way that the knight's distance to its previous position
 *     should be larger than two.
 * <p>
 *     The move can't be made if the knight would land on a blocked node.
 * <p>
 *     The knight is the only piece that can jump over other pieces.
 */
public class Knight extends Piece {

	public Knight(Node at, Player player) {
		super(at, player);
	}

	@Override
	public String getName() {
		return "knight";
	}

	@Override
	public Set<Move> getMoves(GameState state) {
		Set<Node> targetNodes = new HashSet<>();
		Table table = state.getTable();

		at.getNeighbors().forEach(towards -> {
            double direction = TableUtils.linkDirection(at, towards);
            Node target = at;

            // Take two steps in direction
            for(int i = 0; i < 2; i++) {
                target = table.nodeTowardsDirection(target, direction);
            }

            // Got lost in the process
            if(target == null)
                return;

            // Exclude third step node
            Node excluded = table.nodeTowardsDirection(target, direction);

            target.getNeighbors().stream()
                    .filter(to -> to != excluded)
                    .filter(to -> !table.isLink(at, to))
                    .filter(to -> !table.isSecondaryLink(at, to))
                    .filter(to -> GameStateUtils.isValidTargetNode(state, to, this))
                    .forEach(targetNodes::add);
        });

		return targetNodes.stream()
                .map(to -> new Move(at, to))
                .collect(Collectors.toSet());
	}

    @Override
    public Piece move(Node to) {
        return new Knight(to, player);
    }

}
