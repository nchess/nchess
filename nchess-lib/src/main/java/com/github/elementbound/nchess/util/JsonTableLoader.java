package com.github.elementbound.nchess.util;

import com.github.elementbound.nchess.game.Node;
import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.github.elementbound.nchess.game.Table;

public class JsonTableLoader {
	private Table resultTable = null;
	private InputStream is = null;
	
	public JsonTableLoader(InputStream is) {
		this.is = is; 
	}

	public boolean parse() {
		if(this.resultTable == null)
			this.resultTable = new Table();
		
		//TODO: do something more sophisticated to indicate errors. 
		//Possibly maintain a list of errors and warnings? 
		
		JsonReader reader = Json.createReader(is);
		JsonObject root = reader.readObject();
		
		//=====================================================================================
		//Root must be an object
		if(root.getValueType() != JsonValue.ValueType.OBJECT)
			return false; 
		
		//Must have nodes
		if(!root.containsKey("nodes"))
			return false;
		
		//Must have links 
		if(!root.containsKey("links"))
			return false; 
		
		//Must have players
		if(!root.containsKey("players"))
			return false;
		
		//Must have pieces
		if(!root.containsKey("pieces"))
			return false; 
		
		//=====================================================================================
		//Parse nodes
		JsonObject nodes = root.getJsonObject("nodes");
		if(nodes.getValueType() != JsonValue.ValueType.ARRAY)
			return false; 
		
		for(JsonValue jsval : nodes.values()) {
			if(jsval.getValueType() != JsonValue.ValueType.OBJECT)
				return false; 
			
			JsonObject jsnode = (JsonObject)jsval; 
			if(!jsnode.containsKey("x"))
				return false;
			
			if(!jsnode.containsKey("y"))
				return false;
			
			if(!jsnode.containsKey("id"))
				return false; 
			
			long id = jsnode.getInt("id");
			double x = jsnode.getJsonNumber("x").doubleValue();
			double y = jsnode.getJsonNumber("y").doubleValue();
			resultTable.addNode(id, new Node(id,x,y));
		}
		
		//=====================================================================================
		//Parse links 
		JsonObject links = root.getJsonObject("links");
		if(links.getValueType() != JsonValue.ValueType.ARRAY)
			return false; 
		
		for(JsonValue jsval : links.values()) {
			if(jsval.getValueType() != JsonValue.ValueType.ARRAY)
				return false; 
			
			JsonArray link = (JsonArray)jsval; 
			long fromId = link.getInt(0);
			long toId = link.getInt(1);
			if(!resultTable.linkNode(fromId, toId))
				return false; 
		}
		
		//=====================================================================================
		//Parse players
		//TODO
		
		//=====================================================================================
		//Parse pieces
		//TODO
		
		return true; 
	}

	public Table getResult() {
		return this.resultTable;
	}
	
	public void assignTable(Table table) {
		this.resultTable = table; 
	}
}
