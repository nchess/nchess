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
	
	@Override 
	public String toString() {
		StringBuffer strb = new StringBuffer();
		
		return strb.append("[Move]")
			.append(this.fromId)
			.append(" => ")
			.append(this.toId) 
			.toString();
	}
	
	@Override 
	public boolean equals(Object rhs) {
		if(rhs == this)
			return true; 
		
		if(!(rhs instanceof Move))
			return false; 
		
		Move m = (Move)rhs;
		return ((this.fromId == m.fromId) && (this.toId == m.toId));
	}
}
