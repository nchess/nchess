package com.github.elementbound.nchess.marshalling;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Player;
import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.game.pieces.Bishop;
import com.github.elementbound.nchess.game.pieces.King;
import com.github.elementbound.nchess.game.pieces.Knight;
import com.github.elementbound.nchess.game.pieces.Pawn;
import com.github.elementbound.nchess.game.pieces.Queen;
import com.github.elementbound.nchess.game.pieces.Rook;
import com.github.elementbound.nchess.util.PieceFactory;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to extract {@link GameState} data from JSON input.
 */
public class JsonGameStateParser {
    private static final Map<String, PieceFactory> PIECE_FACTORIES = ImmutableMap.<String, PieceFactory>builder()
            .put("pawn", Pawn::new)
            .put("rook", Rook::new)
            .put("knight", Knight::new)
            .put("bishop", Bishop::new)
            .put("queen", Queen::new)
            .put("king", King::new)
            .build();

    private final JsonGameStateValidator validator = new JsonGameStateValidator();

    public GameState parse(InputStream is) {
        try (JsonReader reader = Json.createReader(is)) {
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
                Node from = getLinkStartNode(nodes, link);
                Node to = getLinkDestinationNode(nodes, link);

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
    }

    private Node getLinkDestinationNode(Set<Node> nodes, Pair<Long, Long> link) {
        return nodes.stream()
                .filter(n -> n.getId() == link.getRight())
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Node getLinkStartNode(Set<Node> nodes, Pair<Long, Long> link) {
        return nodes.stream()
                .filter(n -> n.getId() == link.getLeft())
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
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

            if (!PIECE_FACTORIES.containsKey(type)) {
                throw new IllegalArgumentException(String.format("Unknown piece type: %s", type));
            }

            Node atNode = nodes.stream()
                    .filter(node -> node.getId() == atId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown node id: %d", atId)));

            Player player = new Player(playerId);

            PieceFactory pieceFactory = PIECE_FACTORIES.get(type);
            Piece pieceInstance = pieceFactory.from(atNode, player);

            result.add(pieceInstance);
        }

        return result;
    }
}
