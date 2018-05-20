package com.github.elementbound.nchess.net.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Message containing a join game request.
 */
public class JoinRequestMessage extends Message {
    private static final Logger LOGGER = LoggerFactory.getLogger(JoinRequestMessage.class);

    private final JoinType joinType;

    public JoinRequestMessage(JoinType type) {
        this.joinType = type;
    }

    public static Message fromJSON(JsonObject json) {
        if (!json.getString("type").equals("join")) {
            return null;
        }

        String joinAs = json.getString("as");
        if (joinAs.equals("player")) {
            return new JoinRequestMessage(JoinType.AS_PLAYER);
        } else if (joinAs.equals("observer")) {
            return new JoinRequestMessage(JoinType.AS_OBSERVER);
        } else {
            throw new IllegalArgumentException(String.format("Unknown join type: %s", joinAs));
        }
    }

    public JoinType getJoinType() {
        return joinType;
    }

    @Override
    public String toJSON() {
        JsonObjectBuilder builder = getBuilder();

        builder.add("type", "join");

        switch (joinType) {
            case AS_PLAYER:
                builder.add("as", "player");
                break;
            case AS_OBSERVER:
                builder.add("as", "observer");
                break;
            default:
                LOGGER.error("Unknown join request type: {}", joinType);
        }

        return builder.build().toString();
    }

    /**
     * Enumeration denoting the join request type.
     */
    public enum JoinType {
        AS_PLAYER,
        AS_OBSERVER
    }
}
