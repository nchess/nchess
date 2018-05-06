package com.github.elementbound.nchess.view.event;

import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.view.GamePanel;

public class NodeSelectEvent {
    private final GamePanel source;
    private final Node nodeId;

    public NodeSelectEvent(GamePanel source, Node nodeId) {
        this.source = source;
        this.nodeId = nodeId;
    }

    public GamePanel getSource() {
        return source;
    }

    public Node getNodeId() {
        return nodeId;
    }
}
