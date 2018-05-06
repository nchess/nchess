package com.github.elementbound.nchess.view;

import com.github.elementbound.nchess.game.Node;

public interface TablePanelListener {
	void nodeSelect(GamePanel source, Node nodeId);
}
