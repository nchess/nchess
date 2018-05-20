package com.github.elementbound.nchess.net.protocol;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Node;

import javax.json.JsonObject;
import java.util.Set;

public class MoveMessage extends Message {
	private final long fromId;
	private final long toId;

    public MoveMessage(long fromId, long toId) {
        this.fromId = fromId;
        this.toId = toId;
    }

    public MoveMessage(Move move) {
        this.fromId = move.getFrom().getId();
        this.toId = move.getTo().getId();
    }

    public long getFromId() {
        return fromId;
    }

    public long getToId() {
        return toId;
    }

    public Move getMove(GameState gameState) {
        Set<Node> nodes = gameState.getTable().getNodes();
        Node from = nodes.stream().filter(n -> n.getId() == fromId).findFirst().get();
        Node to = nodes.stream().filter(n -> n.getId() == toId).findFirst().get();

        return new Move(from, to);
    }

    @Override
	public String toJSON() {
		return getBuilder()
				.add("type", "move")
				.add("from", fromId)
				.add("to", toId)
				.build()
				.toString();
	}

	public static Message fromJSON(JsonObject json) {
		if(!json.getString("type").equals("move"))
			return null;

		long fromId = json.getInt("from");
        long toId = json.getInt("to");

		return new MoveMessage(fromId, toId);
	}
}
