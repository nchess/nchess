package com.github.elementbound.nchess.net;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Table;

import java.io.IOException;

public interface ClientEventListener {
	public void onTableUpdate(Client client, Table table);
	public void onJoinResponse(Client client, boolean approved, long playerId);
	public void onMyTurn(Client client);
	public void onMove(Client client, Table table, Move move);
	
	public void onFailedConnect(Client client, IOException e);
	public void onSuccessfulConnect(Client client);
}
