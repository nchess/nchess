package com.github.elementbound.nchess.net.event.client;

import com.github.elementbound.nchess.net.Client;

/**
 * Base class to represent events from the client.
 */
public abstract class ClientEvent {
    private final Client client;

    public ClientEvent(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}
