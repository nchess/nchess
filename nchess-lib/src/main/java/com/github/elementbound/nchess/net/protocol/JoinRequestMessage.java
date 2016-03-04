package com.github.elementbound.nchess.net.protocol;

public class JoinRequestMessage extends Message {
	public enum JoinType {
		AS_PLAYER,
		AS_OBSERVER
	}
	
	private JoinType joinType; 
	
	public JoinRequestMessage(JoinType type) {
		this.joinType = type; 
	}
	
	public JoinType joinType() {
		return this.joinType; 
	}
	
	@Override
	public String toJSON() {
		StringBuilder strb = new StringBuilder();
		strb.append("{ type: \"join\", ")
			.append("as: ");
		
		switch(joinType) {
			case AS_PLAYER: strb.append("\"player\""); break;
			case AS_OBSERVER: strb.append("\"observer\""); break;
		}
		
		strb.append("}");
		
		return strb.toString(); 
	}
}
