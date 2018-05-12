package com.github.elementbound.nchess.util.event.client;

import com.github.elementbound.nchess.game.Player;
import com.github.elementbound.nchess.net.Client;

public class GameEndEvent extends ClientEvent {
    private final Player winner;

    public GameEndEvent(Client client, Player winner) {
        super(client);
        this.winner = winner;
    }

    public Player getWinner() {
        return winner;
    }
}
