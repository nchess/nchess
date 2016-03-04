package com.github.elementbound.nchess.net.protocol;

public class JoinResponseMessage extends Message {
	private long playerId = -1; //TODO: Support spectators
	private boolean approved = false; 
	
	public JoinResponseMessage(long playerId, boolean approved) {
		this.playerId = playerId;
		this.approved = approved;
	}
	
	@Override
	public String toJSON() {
		StringBuilder strb = new StringBuilder();
		strb.append("{ type: \"join-response\", ")
			.append("as: ")
			.append(playerId)
			.append(", approved: ")
			.append(approved ? "true" : "false")
			.append("}");
		
		return strb.toString(); 
	}

}
