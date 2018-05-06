package com.github.elementbound.nchess.marshalling;

import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Player;
import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.game.pieces.*;
import com.github.elementbound.nchess.util.PieceFactory;
import com.google.common.collect.ImmutableMap;

import javax.json.*;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

public class JsonTableParser {
    private static final Map<String, PieceFactory> pieceFactories = ImmutableMap.<String, PieceFactory>builder()
            .put("pawn", Pawn::new)
            .put("rook", Rook::new)
            .put("knight", Knight::new)
            .put("bishop", Bishop::new)
            .put("queen", Queen::new)
            .put("king", King::new)
            .build();

    private final JsonTableValidator validator = new JsonTableValidator();

    private Table resultTable = null;
    private InputStream is;

    public JsonTableParser(InputStream is) {
        this.is = is;
    }

    public boolean parse() {
        if (this.resultTable == null)
            this.resultTable = new Table();

        JsonReader reader = Json.createReader(is);
        JsonObject root = reader.readObject();

        validator.validate(root);

        parseNodes(root);
        parseLinks(root);
        parsePlayers(root);
        parsePieces(root);

        resultTable.preprocess();
        return true;
    }

    private void parsePieces(JsonObject root) {
        JsonValue pieces = root.get("pieces");

        for (JsonValue jsval : ((JsonArray) pieces)) {
            JsonObject piece = (JsonObject) jsval;

            long id = piece.getInt("id");
            long at = piece.getInt("at");
            long player = piece.getInt("player");
            String type = piece.getString("type");

            if (!pieceFactories.containsKey(type)) {
                throw new IllegalArgumentException(String.format("Unknown piece type: %s", type));
            }

            PieceFactory pieceFactory = pieceFactories.get(type);
            Piece pieceInstance = pieceFactory.from(at, player);

            resultTable.addPiece(id, pieceInstance);
        }
    }

    private void parsePlayers(JsonObject root) {
        JsonValue players = root.get("players");

        for (JsonValue jsval : ((JsonArray) players)) {
            JsonNumber playerId = (JsonNumber) jsval;
            Player player = new Player(playerId.toString());
            resultTable.addPlayer(player.longValue());
        }
    }

    private void parseLinks(JsonObject root) {
        JsonValue links = root.get("links");

        for (JsonValue jsval : ((JsonArray) links)) {
            JsonArray link = (JsonArray) jsval;
            long fromId = link.getInt(0);
            long toId = link.getInt(1);
            if (!resultTable.linkNode(fromId, toId)) {
                throw new IllegalArgumentException(String.format("Couldn't establish link: %d -> %d\n", fromId, toId));
            }
        }
    }

    private void parseNodes(JsonObject root) {
        JsonValue nodes = root.get("nodes");

        for (JsonValue jsval : ((JsonArray) nodes)) {
            JsonObject jsnode = (JsonObject) jsval;

            long id = jsnode.getInt("id");
            double x = jsnode.getJsonNumber("x").doubleValue();
            double y = jsnode.getJsonNumber("y").doubleValue();
            boolean visible = jsnode.getBoolean("visible");

            Node node = new Node(id, x, y, visible);

            resultTable.addNode(node.getId(), node);
        }
    }

    public Table getResult() {
        return this.resultTable;
    }

    public void assignTable(Table table) {
        this.resultTable = table;
    }
}
