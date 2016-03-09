package com.github.elementbound.nchess.net.protocol;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;

import javax.json.JsonObject;

import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.util.JsonTableLoader;

public class TableUpdateMessage extends Message {
	private Table table;
	
	public TableUpdateMessage() {
		this.table = null; 
	}
	
	public TableUpdateMessage(Table table) {
		this.table = table; 
	}
	
	public Table table() {
		return this.table(); 
	}
	
	@Override
	public String toJSON() {
		JsonTableLoader tableHandler = new JsonTableLoader(null); //Eek... refactor
		tableHandler.assignTable(this.table);
		JsonObject jTable = tableHandler.serialize();
		
		return getBuilder()
				.add("type", "table-update")
				.add("table", jTable)
				.build().toString();
	}

	@Override
	public Message fromJSON(JsonObject json) {
		if(!json.getString("type").equals("player-turn"))
			return null;

		InputStream is = new ByteArrayInputStream(json.toString().getBytes());
		JsonTableLoader tableHandler = new JsonTableLoader(is);
		if(!tableHandler.parse())
			return null; 
		
		return new TableUpdateMessage(tableHandler.getResult());
	}
}
