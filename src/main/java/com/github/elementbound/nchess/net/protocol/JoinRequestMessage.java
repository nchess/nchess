package com.github.elementbound.nchess.net.protocol;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class JoinRequestMessage extends Message {
	public enum JoinType {
		AS_PLAYER,
		AS_OBSERVER
	}
	
	private final JoinType joinType;

	public JoinRequestMessage(JoinType type) {
		this.joinType = type; 
	}

	public JoinType getJoinType() {
		return joinType;
	}

	@Override
	public String toJSON() {
		JsonObjectBuilder builder = getBuilder();
		
		builder.add("type", "join");
		
		switch(joinType) {
			case AS_PLAYER: builder.add("as", "player"); break;
			case AS_OBSERVER: builder.add("as", "observer"); break;
		}

		return builder.build().toString();
	}

	public static Message fromJSON(JsonObject json) {
		if(!json.getString("type").equals("join"))
			return null; 
		
		String joinAs = json.getString("as");
		if(joinAs.equals("player"))
			return new JoinRequestMessage(JoinType.AS_PLAYER);
		else if(joinAs.equals("observer"))
			return new JoinRequestMessage(JoinType.AS_OBSERVER);
		else 
			throw new IllegalArgumentException(String.format("Unknown join type: %s", joinAs));
	}
}
