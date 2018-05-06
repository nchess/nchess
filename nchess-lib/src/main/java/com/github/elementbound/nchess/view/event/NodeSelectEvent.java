package com.github.elementbound.nchess.view.event;

import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.view.GamePanel;

public class NodeSelectEvent {
    private final GamePanel source;
    private final Node node;

    public NodeSelectEvent(GamePanel source, Node node) {
        this.source = source;
        this.node = node;
    }

    public GamePanel getSource() {
        return source;
    }

    public Node getNode() {
        return node;
    }
}
