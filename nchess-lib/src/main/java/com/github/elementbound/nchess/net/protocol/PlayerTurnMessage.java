package com.github.elementbound.nchess.net.protocol;

import com.github.elementbound.nchess.game.Player;

import javax.json.JsonObject;

public class PlayerTurnMessage extends Message {
	private final Player player;

	public PlayerTurnMessage(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public String toJSON() {
		return getBuilder()
				.add("type", "player-turn")
				.add("player", player.getId())
				.build()
				.toString();
	}

	public static Message fromJSON(JsonObject json) {
		if(!json.getString("type").equals("player-turn"))
			return null;
		
		return new PlayerTurnMessage(
		        new Player(json.getString("player"))
        );
	}

}
