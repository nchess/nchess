package com.github.elementbound.nchess.game.pieces;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Player;
import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.util.GameStateUtils;
import com.github.elementbound.nchess.util.TableUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>Class to represent the Bishop piece.
 * <p>
 * The bishop moves <i>diagonally</i>. Take a secondary neighbor of the node the bishop is standing on. Now, continue
 * stepping along on the secondary nodes in the given direction, until either:
 * <ul>
 * <li>there's no node to step on</li>
 * <li>there's no visible node to step on</li>
 * <li>or there's a friendly piece standing on it</li>
 * </ul>
 * When encountering an enemy piece, that also counts as a valid step, but no more further moves are allowed.
 */
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
                    double direction = TableUtils.linkDirection(at, towards);

                    while (GameStateUtils.isValidTargetNode(state, towards, this)) {
                        targetNodes.add(towards);

                        // Stop if node is occupied by enemy
                        if (GameStateUtils.isEnemyAtNode(state, towards, this)) {
                            break;
                        }

                        towards = table.secondaryNodeTowardsDirection(towards, direction);
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
