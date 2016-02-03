package com.github.elementbound.nchess.game;

import java.util.List;

public abstract class Piece {
	private long at = -1; 
	private long player = -1;
	
	public Piece(long player, long at) {
		this.player = player;
		this.at = at; 
	}
	
	public abstract String getName(); 
	public abstract List<Move> getMoves(); 
	
	public long at() {
		return this.at();
	}
	
	//TODO: Make this a bit harder to modify, so only the containing Table can move things around
	//Interface segregation sounds like a fun thing to do here
	public void at(long nowAt) {
		this.at = nowAt; 
	}
}
