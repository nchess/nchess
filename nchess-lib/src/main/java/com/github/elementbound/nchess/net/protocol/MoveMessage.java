package com.github.elementbound.nchess.net.protocol;

import javax.json.JsonObject;

import com.github.elementbound.nchess.game.Move;

public class MoveMessage extends Message {
	private Move move; 
	
	public MoveMessage() {
		this.move = new Move(0,0);
	}
	
	public MoveMessage(Move move) {
		this.move = move;
	}
	
	public Move move() {
		return this.move; 
	}
	
	@Override
	public String toJSON() {
		return getBuilder()
				.add("type", "move")
				.add("from", move.from())
				.add("to", move.to())
				.build()
				.toString();
	}

	@Override
	public Message fromJSON(JsonObject json) {
		if(!json.getString("type").equals("move"))
			return null;
		
		return new MoveMessage(new Move(json.getInt("from"), json.getInt("to")));
	}

}
