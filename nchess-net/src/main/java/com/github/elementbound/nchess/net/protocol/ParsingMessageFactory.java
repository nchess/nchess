package com.github.elementbound.nchess.net.protocol;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Class to parse a JSON input to a {@link Message}.
 * <p>The class has a set of known message type it recognizes, and delegates
 * the parsing to the appropriate class.
 */
public final class ParsingMessageFactory {
    public static final List<JsonMessageParser> KNOWN_TYPES = new ArrayList<>();

    static {
        KNOWN_TYPES.add(JoinRequestMessage::fromJSON);
        KNOWN_TYPES.add(JoinResponseMessage::fromJSON);
        KNOWN_TYPES.add(MoveMessage::fromJSON);
        KNOWN_TYPES.add(PlayerTurnMessage::fromJSON);
        KNOWN_TYPES.add(GameStateUpdateMessage::fromJSON);
    }

    public static Message from(String msg) {
        JsonReader reader = Json.createReader(new StringReader(msg));
        JsonObject root = reader.readObject();
        reader.close();

        Message ret = null;
        for (JsonMessageParser parser : KNOWN_TYPES) {
            ret = parser.fromJSON(root);
            if (ret != null) {
                return ret;
            }
        }

        return ret;
    }

    private ParsingMessageFactory() {
        // Hide constructor
    }
}
