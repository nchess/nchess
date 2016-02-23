package com.github.elementbound.nchess.util;

import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.game.pieces.Bishop;
import com.github.elementbound.nchess.game.pieces.King;
import com.github.elementbound.nchess.game.pieces.Knight;
import com.github.elementbound.nchess.game.pieces.Pawn;
import com.github.elementbound.nchess.game.pieces.Queen;
import com.github.elementbound.nchess.game.pieces.Rook;

public class JsonTableLoader {
	private Table resultTable = null;
	private InputStream is = null;
	
	public Map<String, Class<?>> nameToPiece = new HashMap<>();
	
	public JsonTableLoader(InputStream is) {
		this.is = is; 
		
		nameToPiece.put("pawn", Pawn.class);
		nameToPiece.put("rook", Rook.class);
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
		//JsonObject nodes = root.getJsonObject("nodes");
		JsonValue nodes = root.get("nodes");
		if(nodes.getValueType() != JsonValue.ValueType.ARRAY)
			return false; 

		for(JsonValue jsval : ((JsonArray)nodes)) {
			if(jsval.getValueType() != JsonValue.ValueType.OBJECT)
				return false; 
			
			JsonObject jsnode = (JsonObject)jsval; 
			if(!jsnode.containsKey("x"))
				return false;
			
			if(!jsnode.containsKey("y"))
				return false;
			
			if(!jsnode.containsKey("id"))
				return false; 
			
			if(!jsnode.containsKey("visible"))
				return false; 
			
			long id = jsnode.getInt("id");
			double x = jsnode.getJsonNumber("x").doubleValue();
			double y = jsnode.getJsonNumber("y").doubleValue();
			boolean visible = jsnode.getBoolean("visible");
			
			//Negative IDs are prohibited
			if(id < 0)
				return false;
			
			resultTable.addNode(id, new Node(resultTable, id,x,y, visible));
		}
		
		//=====================================================================================
		//Parse links 
		JsonValue links = root.get("links");
		if(links.getValueType() != JsonValue.ValueType.ARRAY)
			return false; 
		
		for(JsonValue jsval : ((JsonArray)links)) {
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
		JsonValue players = root.get("players");
		if(players.getValueType() != JsonValue.ValueType.ARRAY)
			return false; 
		
		for(JsonValue jsval : ((JsonArray)players)) {
			if(jsval.getValueType() != JsonValue.ValueType.NUMBER)
				return false; 
			
			JsonNumber player = (JsonNumber)jsval; 
			resultTable.addPlayer(player.longValue());
		}
		
		//=====================================================================================
		//Parse pieces
		
		JsonValue pieces = root.get("pieces"); 
		if(pieces.getValueType() != JsonValue.ValueType.ARRAY)
			return false; 
		
		for(JsonValue jsval: ((JsonArray)pieces)) {
			if(jsval.getValueType() != JsonValue.ValueType.OBJECT)
				return false; 
			
			JsonObject piece = (JsonObject)jsval; 
			
			if(!piece.containsKey("id"))
				return false; 
			
			if(!piece.containsKey("at"))
				return false;
			
			if(!piece.containsKey("player"))
				return false; 
			
			if(!piece.containsKey("type"))
				return false; 
			
			long id = piece.getInt("id");
			long at = piece.getInt("at");
			long player = piece.getInt("player");
			String type = piece.getString("type");

			Piece pieceInstance = null;
			
			System.out.printf("This a %s\n", type);
			if(type.equals("pawn"))
				pieceInstance = new Pawn(player, at);
			else if(type.equals("rook"))
				pieceInstance = new Rook(player, at);
			else if(type.equals("knight"))
				pieceInstance = new Knight(player, at);
			else if(type.equals("bishop"))	
				pieceInstance = new Bishop(player, at);
			else if(type.equals("queen"))
				pieceInstance = new Queen(player, at);
			else if(type.equals("king"))
				pieceInstance = new King(player, at);
				
			if(pieceInstance == null){
				System.out.printf("Unknown type %s\n", type);
				//TODO: something more sophisticated with reflection? 
			}
			else {
				resultTable.addPiece(id, pieceInstance);
			}
		}
		
		resultTable.preprocess();
		return true; 
	}

	public Table getResult() {
		return this.resultTable;
	}
	
	public void assignTable(Table table) {
		this.resultTable = table; 
	}
}
