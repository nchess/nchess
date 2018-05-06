package com.github.elementbound.nchess.marshalling;

import com.github.elementbound.nchess.game.*;

import javax.json.*;

public class JsonTableSerializer {
    public JsonObject serialize(GameState gameState) {
        Table table = gameState.getTable();

        JsonObjectBuilder rootBuilder = Json.createObjectBuilder();
        JsonArrayBuilder nodesBuilder = Json.createArrayBuilder();
        JsonArrayBuilder linksBuilder = Json.createArrayBuilder();
        JsonArrayBuilder playersBuilder = Json.createArrayBuilder();
        JsonArrayBuilder piecesBuilder = Json.createArrayBuilder();

        //Serialize nodes
        table.getNodes().stream()
                .map(this::serializeNode)
                .forEach(nodesBuilder::add);

        //Serialize links
        table.getNodes().forEach(node ->
            node.getNeighbors().stream()
                    .map(neighbor -> serializeLink(node, neighbor))
                    .forEach(linksBuilder::add)
        );

        //Serialize players
        gameState.getPlayers().stream()
                .map(Player::getId)
                .forEach(playersBuilder::add);

        //Serialize pieces
        gameState.getPieces().stream()
                .map(this::serializePiece)
                .forEach(piecesBuilder::add);

        //Put them together
        rootBuilder.add("nodes", nodesBuilder.build())
                .add("links", linksBuilder.build())
                .add("players", playersBuilder.build())
                .add("pieces", piecesBuilder.build());

        return rootBuilder.build();
    }

    private JsonObject serializePiece(Piece piece) {
        return Json.createObjectBuilder()
                .add("type", piece.getName())
                .add("player", piece.getPlayer().getId())
                .add("at", piece.getAt().getId())
                .build();
    }

    private JsonArray serializeLink(Node from, Node to) {
        return Json.createArrayBuilder()
                .add(from.getId())
                .add(to.getId())
                .build();
    }

    private JsonObject serializeNode(Node node) {
        JsonObjectBuilder nodeBuilder = Json.createObjectBuilder();
        nodeBuilder.add("id", node.getId())
                .add("visible", node.isVisible())
                .add("x", node.getX())
                .add("y", node.getY());

        return nodeBuilder.build();
    }
}
