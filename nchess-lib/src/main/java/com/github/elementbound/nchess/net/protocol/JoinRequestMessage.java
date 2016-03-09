package com.github.elementbound.nchess.net.protocol;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class JoinRequestMessage extends Message {
	public enum JoinType {
		AS_PLAYER,
		AS_OBSERVER
	}
	
	private JoinType joinType; 
	
	public JoinRequestMessage() {
	}
	
	public JoinRequestMessage(JoinType type) {
		this.joinType = type; 
	}
	
	public JoinType joinType() {
		return this.joinType; 
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

	@Override
	public Message fromJSON(JsonObject json) {
		if(!json.getString("type").equals("join"))
			return null; 
		
		String joinAs = json.getString("as");
		if(joinAs.equals("player"))
			return new JoinRequestMessage(JoinType.AS_PLAYER);
		else if(joinAs.equals("observer"))
			return new JoinRequestMessage(JoinType.AS_OBSERVER);
		else 
			return null;
	}
}
