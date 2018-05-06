package com.github.elementbound.nchess.marshalling;

import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Table;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Map;

public class JsonTableSerializer {
    public JsonObject serialize(Table resultTable) {
        if (resultTable == null)
            return null;

        JsonObjectBuilder rootBuilder = Json.createObjectBuilder();
        JsonArrayBuilder nodesBuilder = Json.createArrayBuilder();
        JsonArrayBuilder linksBuilder = Json.createArrayBuilder();
        JsonArrayBuilder playersBuilder = Json.createArrayBuilder();
        JsonArrayBuilder piecesBuilder = Json.createArrayBuilder();

        //region BuildNodes
        for (Map.Entry<Long, Node> e : resultTable.allNodes()) {
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
        for (Map.Entry<Long, Node> e : resultTable.allNodes()) {
            Node node = e.getValue();

            for (int i = 0; i < node.neighborCount(); i++) {
                JsonArrayBuilder linkBuilder = Json.createArrayBuilder();
                linkBuilder.add(node.id())
                        .add(node.neighbor(i));
                linksBuilder.add(linkBuilder.build());
            }
        }
        //endregion BuildLinks

        //region BuildPlayers
        for (long playerId : resultTable.allPlayers()) {
            playersBuilder.add(playerId);
        }
        //endregion BuildPlayer

        //region BuildPieces
        for (Map.Entry<Long, Piece> e : resultTable.allPieces()) {
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
}
