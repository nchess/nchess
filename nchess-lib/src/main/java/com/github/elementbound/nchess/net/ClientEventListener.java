package com.github.elementbound.nchess.net;

import com.github.elementbound.nchess.game.Table;

public interface ClientEventListener {
	public void onTableUpdate(Client client, Table table);
	public void onJoinResponse(Client client, boolean approved, long playerId);
	public void onMyTurn(Client client);
}
