package com.github.elementbound.nchess.net.protocol;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

/**
 * <p>Abstract base class for messages.
 * <p>Messages must implement functionality to be converted from and to JSON.
 */
public abstract class Message implements JsonMessageSerializer {
    protected static JsonObjectBuilder getBuilder() {
        return Json.createBuilderFactory(null).createObjectBuilder();
    }

    public abstract String toJSON();
}
