package com.github.elementbound.nchess.marshalling;

import com.github.elementbound.nchess.game.*;
import com.github.elementbound.nchess.game.pieces.*;
import com.github.elementbound.nchess.util.PieceFactory;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.json.*;
import java.io.InputStream;
import java.util.*;
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

    public GameState parse(InputStream is) {
        JsonReader reader = Json.createReader(is);
        JsonObject root = reader.readObject();

        // Validate input
        validator.validate(root);

        // Extract data
        Set<Node> nodes = parseNodes(root);
        Set<Pair<Long, Long>> links = parseLinks(root);
        List<Player> players = parsePlayers(root);
        Set<Piece> pieces = parsePieces(root, nodes);

        // Apply links
        links.forEach(link -> {
            // TODO: Refactor this
            Node from = nodes.stream().filter(n -> n.getId() == link.getLeft()).findFirst().orElseThrow(IllegalArgumentException::new);
            Node to = nodes.stream().filter(n -> n.getId() == link.getRight()).findFirst().orElseThrow(IllegalArgumentException::new);

            from.link(to);
        });

        Table.Builder tableBuilder = Table.builder();
        nodes.forEach(tableBuilder::withNode);

        return GameState.builder()
                .table(tableBuilder.build())
                .players(players)
                .currentPlayer(players.get(0))
                .pieces(pieces)
                .build();
    }

    private Set<Node> parseNodes(JsonObject root) {
        JsonValue nodes = root.get("nodes");
        Set<Node> result = new HashSet<>();

        for (JsonValue jsval : ((JsonArray) nodes)) {
            JsonObject jsnode = (JsonObject) jsval;

            long id = jsnode.getInt("id");
            double x = jsnode.getJsonNumber("x").doubleValue();
            double y = jsnode.getJsonNumber("y").doubleValue();
            boolean visible = jsnode.getBoolean("visible");

            result.add(new Node(id, x, y, visible));
        }

        return result;
    }

    private Set<Pair<Long, Long>> parseLinks(JsonObject root) {
        JsonValue links = root.get("links");
        Set<Pair<Long, Long>> result = new HashSet<>();

        for (JsonValue jsval : ((JsonArray) links)) {
            JsonArray link = (JsonArray) jsval;
            long fromId = link.getInt(0);
            long toId = link.getInt(1);
            result.add(new ImmutablePair<>(fromId, toId));
        }

        return result;
    }

    private List<Player> parsePlayers(JsonObject root) {
        JsonValue players = root.get("players");
        List<Player> result = new ArrayList<>();

        for (JsonValue jsval : ((JsonArray) players)) {
            JsonNumber playerId = (JsonNumber) jsval;

            result.add(new Player(playerId.longValue()));
        }

        return result;
    }

    private Set<Piece> parsePieces(JsonObject root, Set<Node> nodes) {
        JsonValue pieces = root.get("pieces");
        Set<Piece> result = new HashSet<>();

        for (JsonValue jsval : ((JsonArray) pieces)) {
            JsonObject piece = (JsonObject) jsval;

            long atId = piece.getInt("at");
            long playerId = piece.getInt("player");
            String type = piece.getString("type");

            if (!pieceFactories.containsKey(type)) {
                throw new IllegalArgumentException(String.format("Unknown piece type: %s", type));
            }

            Node atNode = nodes.stream()
                    .filter(node -> node.getId() == atId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown node id: %d", atId)));

            Player player = new Player(playerId);

            PieceFactory pieceFactory = pieceFactories.get(type);
            Piece pieceInstance = pieceFactory.from(atNode, player);

            result.add(pieceInstance);
        }

        return result;
    }
}
