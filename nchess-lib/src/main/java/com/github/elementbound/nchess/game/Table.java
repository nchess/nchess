package com.github.elementbound.nchess.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.elementbound.nchess.util.MathUtils;
import javafx.scene.control.Tab;

import java.util.Set;

public class Table {
	private final Set<Node> nodes;

    public Table(Set<Node> nodes) {
        this.nodes = nodes;
    }

    //=========================================================================================
	//Nodes 
	//region Nodes

	@Deprecated
	public double linkDirection(Node a, Node b) {
		return MathUtils.vectorDirection(a.getX(), a.getY(), b.getX(), b.getY());
	}

	// TODO: Where to move this even? Probably to Node?
    @Deprecated
	public boolean isLink(Node from, Node to) {
		return from.getNeighbors().contains(to);
	}

	// TODO: Move to Node?
	@Deprecated
	public boolean isSecondaryLink(Node from, Node to) {
        return from.getSecondaryNeighbors().contains(to);
	}

	// TODO: Refactor and unit test; move?
	public long nodeTowardsDirection(long from, double dir) {
		Node node = this.getNode(from);
		if(node == null)
			return -1;
		
		double bestSimilarity = -2.0;
		long bestNode = -1;
		//System.out.printf("[Towards]From %d, in %f\n", from, dir);
		
		for(int i = 0; i < node.neighborCount(); i++) {
			long ni = node.neighbor(i);
			
			double similarity = MathUtils.directionSimilarity(dir, this.linkDirection(from, ni));
			//System.out.printf("[Towards]%d => %d, dir is %f, similarity is %f vs %f\n", from, ni, this.linkDirection(from, ni), similarity, bestSimilarity);
			if(similarity > bestSimilarity) {
				bestSimilarity = similarity;
				bestNode = ni;
				//System.out.printf("[Towards]New best: %d => %d, dir is %f, similarity is %f\n", from, ni, this.linkDirection(from, ni), similarity);
			}
		}
		
		if(bestNode < 0)
			return -1;
		
		Node candidate = this.getNode(bestNode);
		if(!candidate.visible())
			return -1;
		else
			return bestNode;
	}

    // TODO: Refactor and unit test; and move?
	public long secondaryNodeTowardsDirection(long from, double dir) {
		Node node = this.getNode(from);
		if(node == null)
			return -1;
		
		double bestSimilarity = -2.0;
		long bestNode = -1;
		//System.out.printf("[Towards]From %d, in %f\n", from, dir);
		
		for(int i = 0; i < node.secondaryNeighborCount(); i++) {
			long ni = node.secondaryNeighbor(i);
			
			double similarity = MathUtils.directionSimilarity(dir, this.linkDirection(from, ni));
			//System.out.printf("[Towards]%d => %d, dir is %f, similarity is %f vs %f\n", from, ni, this.linkDirection(from, ni), similarity, bestSimilarity);
			if(similarity > bestSimilarity) {
				bestSimilarity = similarity;
				bestNode = ni;
				//System.out.printf("[Towards]New best: %d => %d, dir is %f, similarity is %f\n", from, ni, this.linkDirection(from, ni), similarity);
			}
		}
		
		return bestNode;
	}

	//=========================================================================================
	//Moves 
	//region Moves 

    // TODO: Move to GameState
    @Deprecated
	public boolean applyMove(Move move) {
		long fromPieceId = this.pieceAt(move.from());
		long toPieceId = this.pieceAt(move.to());
		
		System.out.printf("Applying move %s\n", move.toString());
		if(fromPieceId < 0)
			return false; //Trying to move a nonexistent piece
		
		if(toPieceId >= 0)
			this.removePiece(toPieceId); //To move over an existing piece is to eradicate it
		//TODO: Check if moving over an allied piece, and if so, deny move 
		
		//Perform move
		System.out.println("Done. ");
		Piece piece = this.getPiece(fromPieceId);
		piece.at(move.to());
		piece.onMoveApplied(this, move.from(), move.to());
		return true; 
	}

	// TODO: Move to controller?
    @Deprecated
	public List<Move> getMovesByPlayer(long playerId) {
		List<Move> result = new ArrayList<>();
		
		for(Entry<Long, Piece> p : this.allPieces()) {
			long pieceId = p.getKey();
			Piece piece = p.getValue();
			
			if(piece.player() != playerId)
				continue; 
			
			result.addAll(piece.getMoves(this));
		}
		
		return result; 
	}
	
	//endregion Moves

    public static Builder builder() {
	    return new Builder();
    }

    public static class Builder {
	    private Set<Node> nodes = new HashSet<>();

	    private Builder() {
        }

        public Builder withNode(Node node) {
	        nodes.add(node);
	        return this;
        }

        public Table build() {
            nodes.forEach(Node::gatherSecondaryNeighbors);
            return new Table(nodes);
        }
    }
}
