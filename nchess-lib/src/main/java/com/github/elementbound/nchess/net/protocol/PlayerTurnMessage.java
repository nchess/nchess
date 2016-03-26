package com.github.elementbound.nchess.net.protocol;

import javax.json.JsonObject;

public class PlayerTurnMessage extends Message {
	private long playerId;
	
	public PlayerTurnMessage() {
		this.playerId = 0;
	}
	
	public PlayerTurnMessage(long pid) {
		this.playerId = pid;
	}
	
	public long playerId() {
		return this.playerId;
	}
	
	@Override
	public String toJSON() {
		return getBuilder()
				.add("type", "player-turn")
				.add("player", playerId)
				.build()
				.toString();
	}

	@Override
	public Message fromJSON(JsonObject json) {
		if(!json.getString("type").equals("player-turn"))
			return null;
		
		return new PlayerTurnMessage(json.getInt("player"));
	}

}
