package com.github.elementbound.nchess.demos;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.marshalling.JsonGameStateParser;
import com.github.elementbound.nchess.net.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * A command-line interface server.
 */
public class CLIServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CLIServer.class);
    private static final int PORT = 60001;

    public static void main(String[] args) {
        CLIServer app = new CLIServer();
        app.run(args);
    }

    private void run(String[] args) {
        String fname = "2hexa.json";
        LOGGER.info("Loading map {}", fname);
        InputStream fin = this.getClass().getClassLoader().getResourceAsStream(fname);

        if (fin == null) {
            LOGGER.error("Couldn't load map {}", fname);
            return;
        }

        JsonGameStateParser jsonLoader = new JsonGameStateParser();
        GameState gameState = jsonLoader.parse(fin);

        LOGGER.info("Map successfully loaded!");

        //=====================================================================================

        LOGGER.info("Starting server");
        Server server = new Server(gameState);
        try {
            server.run(PORT);
        } catch (IOException e) {
            LOGGER.error("Communication exception", e);
        }
    }
}
