package com.github.elementbound.nchess.net.protocol;


import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.marshalling.JsonTableParser;
import com.github.elementbound.nchess.marshalling.JsonTableSerializer;

import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class GameStateUpdateMessage extends Message {
	private final GameState gameState;

    public GameStateUpdateMessage(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    @Override
	public String toJSON() {
        JsonTableSerializer serializer = new JsonTableSerializer();

        return getBuilder()
                .add("type", "game-update")
                .add("state", serializer.serialize(gameState))
                .build().toString();
	}

	public static Message fromJSON(JsonObject json) {
		if(!json.getString("type").equals("game-update"))
			return null;

		InputStream is = new ByteArrayInputStream(json.getJsonObject("state").toString().getBytes());
		JsonTableParser tableHandler = new JsonTableParser();
		
		return new GameStateUpdateMessage(tableHandler.parse(is));
	}
}
