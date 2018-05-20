package com.github.elementbound.nchess.net.protocol;

import javax.json.JsonObject;

public interface JsonMessageParser {
    Message fromJSON(JsonObject json);
}
