package com.github.elementbound.nchess.net.protocol;

import javax.json.JsonObject;

public class JoinResponseMessage extends Message {
	private final String playerId; //TODO: Support spectators
	private final boolean approved;

    public JoinResponseMessage(String playerId, boolean approved) {
        this.playerId = playerId;
        this.approved = approved;
    }

    public String getPlayerId() {
        return playerId;
    }

    public boolean isApproved() {
        return approved;
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

	public static Message fromJSON(JsonObject json) {
		if(!json.getString("type").equals("join-response"))
			return null;
		
		return new JoinResponseMessage(json.getString("as"), json.getBoolean("approved"));
	}

}
