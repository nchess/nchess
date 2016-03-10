package com.github.elementbound.nchess.util;

import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;

import java.io.InputStream;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
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
		//region ValidateRoot
		if(root.getValueType() != JsonValue.ValueType.OBJECT) {
			System.out.println("Root object not an actual object!");
			return false; 
		}
		
		//Must have nodes
		if(!root.containsKey("nodes")) {
			System.out.println("Where dem nodes at?!");
			return false; 
		}
		
		//Must have links 
		if(!root.containsKey("links")) {
			System.out.println("Where dem links at?!");
			return false; 
		}
		
		//Must have players
		if(!root.containsKey("players")) {
			System.out.println("Where dem players at?!");
			return false; 
		}
		
		//Must have pieces
		if(!root.containsKey("pieces")) {
			System.out.println("Where dem pieces at?!");
			return false; 
		}
		//endregion ValidateRoot
		
		//=====================================================================================
		//Parse nodes
		//JsonObject nodes = root.getJsonObject("nodes");
		JsonValue nodes = root.get("nodes");
		if(nodes.getValueType() != JsonValue.ValueType.ARRAY) {
			System.out.println("Nodes array not actually an array!");
			return false; 
		}

		for(JsonValue jsval : ((JsonArray)nodes)) {
			if(jsval.getValueType() != JsonValue.ValueType.OBJECT) {
				System.out.println("Node object is not an actual object!");
				return false; 
			}
			
			JsonObject jsnode = (JsonObject)jsval; 
			if(!jsnode.containsKey("x")) {
				System.out.println("Node got no x coordinate!");
				return false; 
			}
			
			if(!jsnode.containsKey("y")) {
				System.out.println("Node got no y coordinate!");
				return false; 
			}
			
			if(!jsnode.containsKey("id")) {
				System.out.println("Node got no id!");
				return false; 
			}
			
			if(!jsnode.containsKey("visible")) {
				System.out.println("Node got no visibility!");
				return false; 
			}
			
			long id = jsnode.getInt("id");
			double x = jsnode.getJsonNumber("x").doubleValue();
			double y = jsnode.getJsonNumber("y").doubleValue();
			boolean visible = jsnode.getBoolean("visible");
			
			//Negative IDs are prohibited
			if(id < 0) {
				System.out.printf("Invalid node ID: %d\n", id);
				return false;
			}
			
			resultTable.addNode(id, new Node(resultTable, id,x,y, visible));
		}
		
		//=====================================================================================
		//Parse links 
		JsonValue links = root.get("links");
		if(links.getValueType() != JsonValue.ValueType.ARRAY) {
			System.out.println("Links array not an actual array!");
			return false; 
		}
		
		for(JsonValue jsval : ((JsonArray)links)) {
			if(jsval.getValueType() != JsonValue.ValueType.ARRAY) {
				System.out.println("Link not an actual array!");
				return false; 
			}
			
			JsonArray link = (JsonArray)jsval; 
			long fromId = link.getInt(0);
			long toId = link.getInt(1);
			if(!resultTable.linkNode(fromId, toId)) {
				System.out.printf("Couldn't establish link: %d -> %d\n", fromId, toId);
				return false; 
			}
		}
		
		//=====================================================================================
		//Parse players
		JsonValue players = root.get("players");
		if(players.getValueType() != JsonValue.ValueType.ARRAY) {
			System.out.println("Players array actual array!");
			return false; 
		}
		
		for(JsonValue jsval : ((JsonArray)players)) {
			if(jsval.getValueType() != JsonValue.ValueType.NUMBER) {
				System.out.println("Player id not a number!");
				return false; 
			}
			
			JsonNumber player = (JsonNumber)jsval; 
			resultTable.addPlayer(player.longValue());
		}
		
		//=====================================================================================
		//Parse pieces
		
		JsonValue pieces = root.get("pieces"); 
		if(pieces.getValueType() != JsonValue.ValueType.ARRAY) {
			System.out.println("Pieces array not an actual array!");
			return false; 
		}
		
		for(JsonValue jsval: ((JsonArray)pieces)) {
			if(jsval.getValueType() != JsonValue.ValueType.OBJECT) {
				System.out.println("Piece not an actual object!");
				return false; 
			}
			
			JsonObject piece = (JsonObject)jsval; 
			
			if(!piece.containsKey("id")) {
				System.out.println("Piece got no id!");
				return false; 
			}
			
			if(!piece.containsKey("at")) {
				System.out.println("Where the piece at?!");
				return false; 
			}
			
			if(!piece.containsKey("player")) {
				System.out.println("Piece got no player!");
				return false; 
			}
			
			if(!piece.containsKey("type")) {
				System.out.println("Piece got no type!");
				return false; 
			}
			
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

	public JsonObject serialize() {
		if(resultTable == null)
			return null; 
		
		JsonObjectBuilder rootBuilder = Json.createObjectBuilder();
		JsonArrayBuilder nodesBuilder = Json.createArrayBuilder();
		JsonArrayBuilder linksBuilder = Json.createArrayBuilder();
		JsonArrayBuilder playersBuilder = Json.createArrayBuilder();
		JsonArrayBuilder piecesBuilder = Json.createArrayBuilder();
		
		//region BuildNodes
		for(Entry<Long, Node> e : resultTable.allNodes()) {
			Node node = e.getValue();
			
			JsonObjectBuilder nodeBuilder = Json.createObjectBuilder();
			nodeBuilder.add("id", node.id())
						.add("visible", node.visible())
						.add("x", node.x())
						.add("y", node.y());
			
			nodesBuilder.add(nodeBuilder.build());
		}
		//endregion BuildNodes

		//region BuildLinks 
		for(Entry<Long, Node> e : resultTable.allNodes()) {
			Node node = e.getValue();
			
			for(int i = 0; i < node.neighborCount(); i++) {
				JsonArrayBuilder linkBuilder = Json.createArrayBuilder();
				linkBuilder.add(node.id())
							.add(node.neighbor(i));
				linksBuilder.add(linkBuilder.build());
			}
		}
		//endregion BuildLinks
		
		//region BuildPlayers
		for(long playerId : resultTable.allPlayers()) {
			playersBuilder.add(playerId);
		}
		//endregion BuildPlayer
		
		//region BuildPieces 
		for(Entry<Long, Piece> e : resultTable.allPieces()) {
			Piece piece = e.getValue();
			long pieceId = e.getKey();
			
			JsonObjectBuilder pieceBuilder = Json.createObjectBuilder();
			pieceBuilder.add("type", piece.getName())
						.add("player", piece.player())
						.add("at", piece.at())
						.add("id", pieceId);
			
			piecesBuilder.add(pieceBuilder.build());
		}
		//endregion BuildPieces
		
		rootBuilder.add("nodes", nodesBuilder.build())
					.add("links", linksBuilder.build())
					.add("players", playersBuilder.build())
					.add("pieces", piecesBuilder.build());
		
		return rootBuilder.build();
	}
	
	public Table getResult() {
		return this.resultTable;
	}
	
	public void assignTable(Table table) {
		this.resultTable = table; 
	}
}
