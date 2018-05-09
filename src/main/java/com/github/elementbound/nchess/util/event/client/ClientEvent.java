package com.github.elementbound.nchess.util.event.client;

import com.github.elementbound.nchess.net.Client;

public class ClientEvent {
    private final Client client;

    public ClientEvent(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}
