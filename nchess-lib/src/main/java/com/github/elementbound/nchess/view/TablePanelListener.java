package com.github.elementbound.nchess.view;

import com.github.elementbound.nchess.game.Table;

public interface TablePanelListener {
	public void nodeSelect(TablePanel source, long nodeId);
}
