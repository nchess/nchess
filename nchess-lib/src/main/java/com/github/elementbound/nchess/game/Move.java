package com.github.elementbound.nchess.game;

public class Move {
	private long fromId; 
	private long toId; 
	
	public long from() {
		return this.fromId;
	}
	
	public long to() {
		return this.toId; 
	}
}
