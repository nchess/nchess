package com.github.elementbound.nchess.net.protocol;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ParsingMessageFactory {
	public static List<JsonMessageParser> knownTypes = new ArrayList<>();
	
	static {
		knownTypes.add(JoinRequestMessage::fromJSON);
		knownTypes.add(JoinResponseMessage::fromJSON);
		knownTypes.add(MoveMessage::fromJSON);
		knownTypes.add(PlayerTurnMessage::fromJSON);
		knownTypes.add(GameStateUpdateMessage::fromJSON);
	}
	
	public static Message from(String msg) {
		JsonReader reader = Json.createReader(new StringReader(msg));
		JsonObject root = reader.readObject();
		reader.close();
		
		Message ret = null;
		for(JsonMessageParser parser : knownTypes) {
			ret = parser.fromJSON(root);
			if(ret != null)
				return ret;
		}
		
		return ret; 
	}
}
