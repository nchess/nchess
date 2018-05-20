package com.github.elementbound.nchess.net.protocol;

import javax.json.JsonObject;

/**
 * Interface to convert JSON input to a {@link Message}.
 */
public interface JsonMessageParser {
    Message fromJSON(JsonObject json);
}
