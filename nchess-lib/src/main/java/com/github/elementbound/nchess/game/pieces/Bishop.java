package com.github.elementbound.nchess.game.pieces;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.elementbound.nchess.game.*;
import com.github.elementbound.nchess.util.GameStateUtils;

public class Bishop extends Piece {

	public Bishop(Node at, Player player) {
		super(at, player);
	}

	@Override
	public String getName() {
		return "bishop"; 
	}

	@Override
	public Set<Move> getMoves(GameState state) {
		Set<Node> targetNodes = new HashSet<>();
        Table table = state.getTable();

		at.getSecondaryNeighbors().stream()
                .forEach(towards -> {
                    double direction = table.linkDirection(at, towards);

                    while(towards != null) {
                        // Stop if node is not visible
                        if(!towards.isVisible())
                            break;

                        // Stop if node is occupied by ally
                        if(GameStateUtils.isAllyAtNode(state, towards, this))
                            break;

                        targetNodes.add(towards);
                        towards = table.nodeTowardsDirection(towards, direction);

                        // Stop if node is occupied by enemy
                        if(GameStateUtils.isEnemyAtNode(state, towards, this))
                            break;
                    }
                });

		return targetNodes.stream()
                .map(to -> new Move(at, to))
                .collect(Collectors.toSet());
	}

    @Override
    public Piece move(Node to) {
        return new Bishop(to, player);
    }

}
