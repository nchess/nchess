package com.github.elementbound.nchess.net.protocol;


import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.util.JsonTableLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TableUpdateMessage extends Message {
	private final Table table;
	
	public TableUpdateMessage(Table table) {
		this.table = table; 
	}

	public Table getTable() {
		return table;
	}

	@Override
	public String toJSON() {
		JsonTableLoader tableHandler = new JsonTableLoader(null); // TODO: Eek... refactor
		tableHandler.assignTable(this.table);
		JsonObject jTable = tableHandler.serialize();
		
		return getBuilder()
				.add("type", "table-update")
				.add("table", jTable)
				.build().toString();
	}

	public static Message fromJSON(JsonObject json) {
		if(!json.getString("type").equals("table-update"))
			return null;

		InputStream is = new ByteArrayInputStream(json.getJsonObject("table").toString().getBytes());
		JsonTableLoader tableHandler = new JsonTableLoader(is);
		if(!tableHandler.parse()) {
			throw new IllegalArgumentException("Ill-formed JSON!");
		}
		
		return new TableUpdateMessage(tableHandler.getResult());
	}
}
