package com.github.elementbound.nchess.net.event.client;

import com.github.elementbound.nchess.game.Player;
import com.github.elementbound.nchess.net.Client;

public class TurnEvent extends ClientEvent {
    private final Player player;
    private final boolean myTurn;

    public TurnEvent(Client client, Player player, boolean myTurn) {
        super(client);
        this.player = player;
        this.myTurn = myTurn;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isMyTurn() {
        return myTurn;
    }
}
