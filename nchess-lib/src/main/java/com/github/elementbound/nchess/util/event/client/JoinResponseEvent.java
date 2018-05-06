package com.github.elementbound.nchess.util.event.client;

import com.github.elementbound.nchess.game.Player;
import com.github.elementbound.nchess.net.Client;

public class JoinResponseEvent extends ClientEvent {
    private final boolean approved;
    private final Player player;

    public JoinResponseEvent(Client client, boolean approved, Player player) {
        super(client);
        this.approved = approved;
        this.player = player;
    }

    public boolean isApproved() {
        return approved;
    }

    public Player getPlayer() {
        return player;
    }
}
