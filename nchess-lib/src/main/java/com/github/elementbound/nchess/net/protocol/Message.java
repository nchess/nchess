package com.github.elementbound.nchess.net.protocol;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public abstract class Message implements JSONable {
	public abstract String toJSON(); 
	
	//Can return null if message is not recognized by this particular class
	public abstract Message fromJSON(JsonObject json);
	
	protected static JsonObjectBuilder getBuilder() {
		return Json.createBuilderFactory(null).createObjectBuilder();
	}
}
