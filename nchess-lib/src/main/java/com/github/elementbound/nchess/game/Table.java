package com.github.elementbound.nchess.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.elementbound.nchess.util.MathUtils;

import java.util.Set;

public class Table {
	private Map<Long, Node> nodes = new HashMap<>(); 
	private Map<Long, Piece> pieces = new HashMap<>(); 
	private Set<Player> players = new HashSet<>();
	
	//=========================================================================================
	//Preprocess
	
	public void preprocess() {
		System.out.println("Gathering secondary neighbors... ");
		int i = 0;
		for(Node n : nodes.values()) {
			n.gatherSecondaryNeighbors();
			i++;
			System.out.printf("%d/%d\r", i, nodes.size());
		}
		System.out.println();
	}
	
	//=========================================================================================
	//Nodes 
	//region Nodes
	
	public boolean addNode(long id, Node node) {
		if(!this.hasNode(id)) {
			nodes.put(id, node);
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean hasNode(long id) {
		return nodes.containsKey(id);
	}
	
	public boolean linkNode(long fromId, long toId) {
		if(!this.hasNode(fromId) || !this.hasNode(toId))
			return false;
		
		nodes.get(fromId).link(toId);
		return true; 
	}
	
	public Node getNode(long id) {
		return nodes.get(id);
	}
	
	public Set<Entry<Long, Node>> allNodes() {
		return nodes.entrySet();
	}
	
	public double linkDirection(long from, long to) {
		if(!this.hasNode(from))
			return Double.NaN;
		
		if(!this.hasNode(to))
			return Double.NaN;
		
		Node a = this.getNode(from);
		Node b = this.getNode(to);
		
		return MathUtils.vectorDirection(a.x(), a.y(), b.x(), b.y());
	}
	
	public boolean isLink(long from, long to) {
		Node a = this.getNode(from);
		if(a == null)
			return false; 
		
		for(int i = 0; i < a.neighborCount(); i++)
			if(to == a.neighbor(i))
				return true;
		
		return false; 
	}
	
	public boolean isSecondaryLink(long from, long to) {
		Node a = this.getNode(from);
		if(a == null)
			return false; 
		
		for(int i = 0; i < a.secondaryNeighborCount(); i++)
			if(to == a.secondaryNeighbor(i))
				return true;
		
		return false; 
	}
	
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

	public boolean isNodeOccupied(long node) {
		long pieceId = this.pieceAt(node);
		return (pieceId >= 0);
	}
	
	public boolean isNodeOccupiedByAlly(long node, long playerId) {
		long pieceId = this.pieceAt(node);
		System.out.printf("Piece at %d is %d\n", node, pieceId);
		if(pieceId < 0)
			return false; 
		
		Piece piece = this.getPiece(pieceId);
		if(piece.player() == playerId)
			return true; 
		
		return false;
	}

	public boolean isNodeOccupiedByEnemy(long node, long playerId) {
		long pieceId = this.pieceAt(node);
		if(pieceId < 0)
			return false; 
		
		Piece piece = this.getPiece(pieceId);
		if(piece.player() != playerId)
			return true; 
		
		return false;
	}
	
	//endregion Nodes
	
	//=========================================================================================
	//Players
	//region Players
	
	public boolean addPlayer(long id) {
		return players.add(id);
	}
	
	public boolean hasPlayer(long id) {
		return players.contains(id);
	}
	
	public Set<Long> allPlayers() {
		return players;
	}
	
	//endregion

	//=========================================================================================
	//Pieces 
	//region Pieces
	public boolean addPiece(long id, Piece piece) {
		if(!this.hasPiece(id)) {
			pieces.put(id, piece);
			return true;
		}
		else {
			return false; 
		}
	}

	public boolean hasPiece(long id) {
		return pieces.containsKey(id);
	}

	public boolean removePiece(long id) {
		return pieces.remove(id) != null;
	}

	public Piece getPiece(long id) {
		return pieces.get(id);
	}

	public Set<Entry<Long, Piece>> allPieces() {
		return pieces.entrySet();
	}
	
	public long pieceAt(long node) {
		for(Entry<Long, Piece> e: pieces.entrySet())
			if(e.getValue().at() == node)
				return e.getKey();
		
		return -1;
	}

	//endregion Pieces

	//=========================================================================================
	//Moves 
	//region Moves 
	
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
	
}
