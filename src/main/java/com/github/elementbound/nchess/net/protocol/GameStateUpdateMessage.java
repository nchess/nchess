package com.github.elementbound.nchess.net.protocol;


import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.marshalling.JsonGameStateParser;
import com.github.elementbound.nchess.marshalling.JsonGameStateSerializer;

import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Message containing a full game state update.
 *
 * @see GameState
 */
public class GameStateUpdateMessage extends Message {
    private final GameState gameState;

    public GameStateUpdateMessage(GameState gameState) {
        this.gameState = gameState;
    }

    public static Message fromJSON(JsonObject json) {
        if (!json.getString("type").equals("game-update")) {
            return null;
        }

        InputStream is = new ByteArrayInputStream(json.getJsonObject("state").toString().getBytes());
        JsonGameStateParser tableHandler = new JsonGameStateParser();

        return new GameStateUpdateMessage(tableHandler.parse(is));
    }

    public GameState getGameState() {
        return gameState;
    }

    @Override
    public String toJSON() {
        JsonGameStateSerializer serializer = new JsonGameStateSerializer();

        return getBuilder()
                .add("type", "game-update")
                .add("state", serializer.serialize(gameState))
                .build().toString();
    }
}
