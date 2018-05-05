package com.github.elementbound.nchess.net.protocol;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Node;

import javax.json.JsonObject;

public class MoveMessage extends Message {
	private final Move move;

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
				.add("from", move.getFrom().getId())
				.add("to", move.getTo().getId())
				.build()
				.toString();
	}

	public static Message fromJSON(JsonObject json) {
		if(!json.getString("type").equals("move"))
			return null;

		long fromId = json.getInt("from");
        long toId = json.getInt("to");

        Node from = Node.builder().id(fromId).build();
        Node to = Node.builder().id(toId).build();

		return new MoveMessage(new Move(from, to));
	}
}
