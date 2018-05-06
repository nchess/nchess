package com.github.elementbound.nchess.net.event.client;

import com.github.elementbound.nchess.net.Client;

public class TurnEvent extends ClientEvent {
    private final String playerId;
    private final boolean myTurn;

    public TurnEvent(Client client, String playerId, boolean myTurn) {
        super(client);
        this.playerId = playerId;
        this.myTurn = myTurn;
    }

    public String getPlayerId() {
        return playerId;
    }

    public boolean isMyTurn() {
        return myTurn;
    }
}
