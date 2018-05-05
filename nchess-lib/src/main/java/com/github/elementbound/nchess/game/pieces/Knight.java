package com.github.elementbound.nchess.game.pieces;

import com.github.elementbound.nchess.game.*;
import com.github.elementbound.nchess.util.GameStateUtils;
import com.github.elementbound.nchess.util.TableUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
                    .filter(to -> !GameStateUtils.isAllyAtNode(state, to, this))
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
