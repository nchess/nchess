package com.github.elementbound.nchess.net.protocol;

import javax.json.JsonObject;

public class JoinResponseMessage extends Message {
	private long playerId = -1; //TODO: Support spectators
	private boolean approved = false; 
	
	public JoinResponseMessage() {
	}
	
	public JoinResponseMessage(long playerId, boolean approved) {
		this.playerId = playerId;
		this.approved = approved;
	}
	
	public long playerId() {
		return this.playerId; 
	}
	
	public boolean approved() {
		return this.approved; 
	}
	
	@Override
	public String toJSON() {
		return getBuilder()
				.add("type", "join-response")
				.add("as", playerId)
				.add("approved", approved)
				.build()
				.toString();
	}

	@Override
	public Message fromJSON(JsonObject json) {
		if(!json.getString("type").equals("join-response"))
			return null;
		
		return new JoinResponseMessage(json.getInt("as"), json.getBoolean("approved"));
	}

}
