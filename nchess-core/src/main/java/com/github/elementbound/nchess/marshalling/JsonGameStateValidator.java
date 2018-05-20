package com.github.elementbound.nchess.marshalling;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * Class to validate JSON input as game state data.
 */
public class JsonGameStateValidator {
    public void validate(JsonObject root) {
        validateRoot(root);
        validateRootTypes(root);
        validateNodes(root.get("nodes"));
        validateLinks(root.get("links"));
        validatePlayers(root.get("players"));
        validatePieces(root.get("pieces"));
    }

    private void validateRoot(JsonObject root) {
        if (root.getValueType() != JsonValue.ValueType.OBJECT) {
            throw new IllegalArgumentException("Root object not an actual object");
        }

        //Must have nodes
        if (!root.containsKey("nodes")) {
            throw new IllegalArgumentException("Nodes list missing");
        }

        //Must have links
        if (!root.containsKey("links")) {
            throw new IllegalArgumentException("Links list missing");
        }

        //Must have players
        if (!root.containsKey("players")) {
            throw new IllegalArgumentException("Players list missing");
        }

        //Must have pieces
        if (!root.containsKey("pieces")) {
            throw new IllegalArgumentException("Pieces list missing");
        }
    }

    private void validateRootTypes(JsonObject root) {
        JsonValue nodes = root.get("nodes");
        if (nodes.getValueType() != JsonValue.ValueType.ARRAY) {
            throw new IllegalArgumentException("Nodes array not actually an array!");
        }

        JsonValue links = root.get("links");
        if (links.getValueType() != JsonValue.ValueType.ARRAY) {
            throw new IllegalArgumentException("Links array not an actual array!");
        }

        JsonValue players = root.get("players");
        if (players.getValueType() != JsonValue.ValueType.ARRAY) {
            throw new IllegalArgumentException("Players array actual array!");
        }

        JsonValue pieces = root.get("pieces");
        if (pieces.getValueType() != JsonValue.ValueType.ARRAY) {
            throw new IllegalArgumentException("Pieces array not an actual array!");
        }
    }

    private void validateNode(JsonValue jsval) {
        if (jsval.getValueType() != JsonValue.ValueType.OBJECT) {
            throw new IllegalArgumentException("Node object is not an actual object!");
        }

        JsonObject jsnode = (JsonObject) jsval;
        if (!jsnode.containsKey("x")) {
            throw new IllegalArgumentException("Node got no x coordinate!");
        }

        if (!jsnode.containsKey("y")) {
            throw new IllegalArgumentException("Node got no y coordinate!");
        }

        if (!jsnode.containsKey("id")) {
            throw new IllegalArgumentException("Node got no id!");
        }

        if (!jsnode.containsKey("visible")) {
            throw new IllegalArgumentException("Node got no visibility!");
        }

        long id = jsnode.getInt("id");

        //Negative IDs are prohibited
        if (id < 0) {
            throw new IllegalArgumentException(String.format("Invalid node ID: %d\n", id));
        }
    }

    private void validateNodes(JsonValue nodes) {
        for (JsonValue jsval : ((JsonArray) nodes)) {
            validateNode(jsval);
        }
    }

    private void validateLink(JsonValue jsval) {
        if (jsval.getValueType() != JsonValue.ValueType.ARRAY) {
            throw new IllegalArgumentException("Link not an actual array!");
        }
    }

    private void validateLinks(JsonValue links) {
        for (JsonValue jsval : ((JsonArray) links)) {
            validateLink(jsval);
        }
    }

    private void validatePiece(JsonValue jsval) {
        if (jsval.getValueType() != JsonValue.ValueType.OBJECT) {
            throw new IllegalArgumentException("Piece not an actual object!");
        }

        JsonObject piece = (JsonObject) jsval;

        if (!piece.containsKey("at")) {
            throw new IllegalArgumentException("Where the piece at?!");
        }

        if (!piece.containsKey("player")) {
            throw new IllegalArgumentException("Piece got no player!");
        }

        if (!piece.containsKey("type")) {
            throw new IllegalArgumentException("Piece got no type!");
        }
    }

    private void validatePieces(JsonValue pieces) {
        for (JsonValue jsval : ((JsonArray) pieces)) {
            validatePiece(jsval);
        }
    }

    private void validatePlayer(JsonValue jsval) {
        if (jsval.getValueType() != JsonValue.ValueType.NUMBER) {
            throw new IllegalArgumentException("Player id not a number!");
        }
    }

    private void validatePlayers(JsonValue players) {
        for (JsonValue jsval : ((JsonArray) players)) {
            validatePlayer(jsval);
        }
    }
}
