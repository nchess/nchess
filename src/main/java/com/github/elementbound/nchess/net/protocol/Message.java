package com.github.elementbound.nchess.net.protocol;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public abstract class Message implements JsonMessageSerializer {
	public abstract String toJSON();
	
	protected static JsonObjectBuilder getBuilder() {
		return Json.createBuilderFactory(null).createObjectBuilder();
	}
}
