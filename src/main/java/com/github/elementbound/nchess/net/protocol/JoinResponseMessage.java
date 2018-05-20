package com.github.elementbound.nchess.net.protocol;

import com.github.elementbound.nchess.game.Player;

import javax.json.JsonObject;

/**
 * Message containing a join response.
 */
public class JoinResponseMessage extends Message {
    private final Player player; //TODO: Support spectators
    private final boolean approved;

    public JoinResponseMessage(Player player, boolean approved) {
        this.player = player;
        this.approved = approved;
    }

    public static Message fromJSON(JsonObject json) {
        if (!json.getString("type").equals("join-response")) {
            return null;
        }

        return new JoinResponseMessage(
                new Player(json.getInt("as")),
                json.getBoolean("approved"));
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isApproved() {
        return approved;
    }

    @Override
    public String toJSON() {
        return getBuilder()
                .add("type", "join-response")
                .add("as", player.getId())
                .add("approved", approved)
                .build()
                .toString();
    }

}
