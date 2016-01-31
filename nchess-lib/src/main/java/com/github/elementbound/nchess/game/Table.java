package com.github.elementbound.nchess.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Table {
	private Map<Long, Node> nodes = new HashMap<>(); 
	private List<Piece> pieces = new ArrayList<>(); 
	private Map<Long, Player> players = new HashMap<>(); 
	
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
		if(!this.hasNode(id))
			return null;
		return nodes.get(id);
	}
	
	public Set<Entry<Long, Node>> allNodes() {
		return nodes.entrySet();
	}
}
