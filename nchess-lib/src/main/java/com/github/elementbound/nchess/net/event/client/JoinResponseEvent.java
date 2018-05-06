package com.github.elementbound.nchess.net.event.client;

import com.github.elementbound.nchess.net.Client;

public class JoinResponseEvent extends ClientEvent {
    private final boolean approved;
    private final String playerId;

    public JoinResponseEvent(Client client, boolean approved, String playerId) {
        super(client);
        this.approved = approved;
        this.playerId = playerId;
    }

    public boolean isApproved() {
        return approved;
    }

    public String getPlayerId() {
        return playerId;
    }
}
