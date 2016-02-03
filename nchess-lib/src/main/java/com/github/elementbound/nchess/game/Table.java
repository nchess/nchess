package com.github.elementbound.nchess.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Table {
	private Map<Long, Node> nodes = new HashMap<>(); 
	private Map<Long, Piece> pieces = new HashMap<>(); 
	private Set<Long> players = new HashSet<>(); 
	
	//=========================================================================================
	//Nodes 
	
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
	
	//=========================================================================================
	//Players
	
	public boolean addPlayer(long id) {
		return players.add(id);
	}
	
	public boolean hasPlayer(long id) {
		return players.contains(id);
	}
	
	public Set<Long> allPlayers() {
		return players;
	}

	//=========================================================================================
	//Pieces 
	
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

	public long pieceAt(long node) {
		for(Entry<Long, Piece> e: pieces.entrySet())
			if(e.getValue().at() == node)
				return e.getKey();
		
		return -1;
	}

	public boolean applyMove(Move move) {
		long fromPieceId = this.pieceAt(move.from());
		long toPieceId = this.pieceAt(move.to());
		
		if(fromPieceId < 0)
			return false; //Trying to move an unexisting piece
		
		if(toPieceId >= 0)
			this.removePiece(toPieceId); //To move over an existing piece is to eradicate it
		//TODO: Check if moving over an allied piece, and if so, deny move 
		
		//Perform move
		this.getPiece(fromPieceId).at(move.to());
		return true; 
	}
}
