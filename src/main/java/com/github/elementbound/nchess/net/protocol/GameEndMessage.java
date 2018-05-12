package com.github.elementbound.nchess.net.protocol;

import com.github.elementbound.nchess.game.Player;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class GameEndMessage extends Message {
    private static final String TYPE_STRING = "game-end";
    private final Player winner;

    public GameEndMessage(Player winner) {
        this.winner = winner;
    }

    public Player getWinner() {
        return winner;
    }

    @Override
    public String toJSON() {
        return getBuilder()
                .add("type", TYPE_STRING)
                .add("winner", winner.getId())
                .build()
            .toString();
    }

    public static Message fromJSON(JsonObject json) {
        if(!json.getString("type").equals("game-end")) {
            return null;
        }

        Player winner = new Player(json.getInt("winner"));
        return new GameEndMessage(winner);
    }
}
