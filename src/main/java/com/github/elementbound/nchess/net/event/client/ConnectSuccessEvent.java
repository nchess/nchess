package com.github.elementbound.nchess.net.event.client;

import com.github.elementbound.nchess.net.Client;

/**
 * <p>Connection successful event.
 * <p>Emitted when the client succeeds to connect to a server.
 */
public class ConnectSuccessEvent extends ClientEvent {
    private final String host;
    private final int port;

    public ConnectSuccessEvent(Client client, String host, int port) {
        super(client);
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
