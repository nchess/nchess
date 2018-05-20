package com.github.elementbound.nchess.net.event.client;

import com.github.elementbound.nchess.game.Player;
import com.github.elementbound.nchess.net.Client;

/**
 * <p>Join response event.
 * <p>Contains the server's decision about the join request - whether it's
 * approved and which player slot is assigned to the client.
 */
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
