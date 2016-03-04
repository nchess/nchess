package com.github.elementbound.nchess.net.protocol;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class MessageParser {
	public static List<Message> knownTypes = new ArrayList<>();
	
	static {
		knownTypes.add(new JoinRequestMessage());
		knownTypes.add(new JoinResponseMessage());
		knownTypes.add(new MoveMessage());
		knownTypes.add(new PlayerTurnMessage());
	}
	
	public static Message parse(String msg) {
		JsonReader reader = Json.createReader(new StringReader(msg));
		JsonObject root = reader.readObject();
		reader.close();
		
		Message ret = null;
		for(Message message : knownTypes) {
			ret = message.fromJSON(root);
			if(ret != null)
				return ret;
		}
		
		return ret; 
	}
}
