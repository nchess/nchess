package com.github.elementbound.nchess.net.protocol;

import javax.json.JsonObject;

public class PlayerTurnMessage extends Message {
	private final String playerId;

	public PlayerTurnMessage(String playerId) {
		this.playerId = playerId;
	}

	public String getPlayerId() {
		return playerId;
	}

	@Override
	public String toJSON() {
		return getBuilder()
				.add("type", "player-turn")
				.add("player", playerId)
				.build()
				.toString();
	}

	public static Message fromJSON(JsonObject json) {
		if(!json.getString("type").equals("player-turn"))
			return null;
		
		return new PlayerTurnMessage(json.getString("player"));
	}

}
