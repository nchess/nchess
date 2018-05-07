package com.github.elementbound.nchess.game.pieces;

import com.github.elementbound.nchess.game.*;
import com.github.elementbound.nchess.util.GameStateUtils;
import com.github.elementbound.nchess.util.TableUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Rook extends Piece {

	public Rook(Node at, Player player) {
		super(at, player);
	}

	@Override
	public String getName() {
		return "rook";
	}

	@Override
	public Set<Move> getMoves(GameState state) {
		Table table = state.getTable();
        Set<Node> targetNodes = new HashSet<>();

		at.getNeighbors().forEach(towards -> {
		    double direction = TableUtils.linkDirection(at, towards);

		    // Start stepping in direction until we hit something or arrive on an invalid node
		    for(Node target = table.nodeTowardsDirection(at, direction);
                    GameStateUtils.isValidTargetNode(state, target, this);
                    target = table.nodeTowardsDirection(target, direction)) {

		        // Gather our steps
                targetNodes.add(target);

                // If we hit an enemy, stop there
                if(GameStateUtils.isEnemyAtNode(state, target, this))
                    break;
            }
        });

		return targetNodes.stream()
                .map(to -> new Move(at, to))
                .collect(Collectors.toSet());
	}

    @Override
    public Piece move(Node to) {
        return new Rook(to, player);
    }

}
