package com.github.elementbound.nchess.game;

public class Move {
	private long fromId; 
	private long toId; 
	
	public Move(long fromId, long toId) {
		this.fromId = fromId;
		this.toId = toId;
	}
	
	public long from() {
		return this.fromId;
	}
	
	public long to() {
		return this.toId; 
	}
}
