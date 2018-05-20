package com.github.elementbound.nchess.util.event.client;

import com.github.elementbound.nchess.net.Client;

import java.io.IOException;

public class ConnectFailEvent extends ClientEvent {
    private final IOException exception;
    private final String host;
    private final int port;

    public ConnectFailEvent(Client client, IOException exception, String host, int port) {
        super(client);
        this.exception = exception;
        this.host = host;
        this.port = port;
    }

    public IOException getException() {
        return exception;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
