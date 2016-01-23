import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

public class VagueReaderValidator {
	public boolean validate(InputStream is) {
		JsonReader reader = Json.createReader(is);
		JsonObject root = reader.readObject();
		
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
		
		return true; 
	}
}
