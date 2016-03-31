package com.github.elementbound.nchess.net;

import java.io.IOException;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Table;

public interface ClientEventListener {
	public void onTableUpdate(Client client, Table table);
	public void onJoinResponse(Client client, boolean approved, long playerId);
	public void onMyTurn(Client client);
	public void onMove(Client client, Table table, Move move);
	
	public void onFailedConnect(Client client, IOException e);
	public void onSuccessfulConnect(Client client);
}
