package com.github.elementbound.nchess.net;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.MoveValidator;
import com.github.elementbound.nchess.game.Player;
import com.github.elementbound.nchess.game.exception.InvalidMoveException;
import com.github.elementbound.nchess.net.protocol.GameStateUpdateMessage;
import com.github.elementbound.nchess.net.protocol.JoinResponseMessage;
import com.github.elementbound.nchess.net.protocol.Message;
import com.github.elementbound.nchess.net.protocol.MoveMessage;
import com.github.elementbound.nchess.net.protocol.ParsingMessageFactory;
import com.github.elementbound.nchess.net.protocol.PlayerTurnMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.stream.JsonParsingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle server-side communications of the game.
 * Can be used as a component.
 */
public class Server {
    public static final int CLIENT_TIMEOUT = 150000;
    public static final int INVALID_MOVE_RETRY_COUNT = 8;
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientData.class);
    public static final int DEFAULT_STEP_LIMIT = 64;
    public static final int SLEEP_INTERVAL = 1000;
    private final MoveValidator moveValidator = new MoveValidator();

    private GameState gameState;
    private List<ClientData> clients = new ArrayList<>();

    public Server(GameState gameState) {
        this.gameState = gameState;
    }

    private void broadcast(Message message) {
        clients.forEach(client -> client.send(message));
    }

    private void waitForPlayers(ServerSocket listen) {
        gameState.getPlayers().forEach(player -> {
            while (true) {
                try {
                    LOGGER.info("Waiting for player {}", player);
                    Socket s = listen.accept();

                    //Don't care for join requests atm, just approve them as player
                    ClientData cd = new ClientData(player, s);
                    cd.send(new JoinResponseMessage(player, true));
                    cd.send(new GameStateUpdateMessage(gameState));
                    clients.add(cd);

                    LOGGER.info("Accepted player {}", player);
                    break;
                } catch (IOException e) {
                    LOGGER.error("Could't accept player {}, retrying...", player, e);
                }
            }
        });
    }

    public void run(int port) throws IOException {
        ServerSocket listen = new ServerSocket(port);
        waitForPlayers(listen);
        listen.close();

        //Play for some steps then quit
        for (int i = 0; i < DEFAULT_STEP_LIMIT; i++) {
            for (ClientData client : clients) {
                //Send a player turn notif
                broadcast(new PlayerTurnMessage(client.getPlayer()));

                //Wait for response
                Message msg = waitForMessage(client, CLIENT_TIMEOUT);
                if (msg == null) {
                    continue;
                }

                if (msg instanceof MoveMessage) {
                    MoveMessage moveMessage = (MoveMessage) msg;
                    Move move = moveMessage.getMove(gameState);

                    for (int j = 0; j < INVALID_MOVE_RETRY_COUNT; ++j) {
                        try {
                            gameState = gameState.applyMove(move);
                            LOGGER.info("Valid move: {}; broadcasting", moveMessage);
                            broadcast(moveMessage);
                            break;
                        } catch (InvalidMoveException e) {
                            LOGGER.error("Invalid move!", e);
                            LOGGER.info("Player {} sent invalid turn, retrying {}/{}",
                                    new Object[] {client.getPlayer(), j, INVALID_MOVE_RETRY_COUNT});
                            broadcast(new PlayerTurnMessage(client.getPlayer()));
                        }
                    }
                } else {
                    LOGGER.error("Unrecognized message: {}", msg);
                }
            }
        }
    }

    private Message waitForMessage(ClientData cd, long timeout) throws IOException {
        Message msg = null;

        try {
            for (long till = System.currentTimeMillis() + timeout;
                 System.currentTimeMillis() < till;
                 Thread.sleep(SLEEP_INTERVAL)) {
                msg = cd.receive();
                if (msg != null) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            LOGGER.warn("Message receive interrupted", e);
        }

        if (msg == null) {
            LOGGER.error("No response from {} in {} ms, skipping turn", cd.getPlayer(), timeout);
            return null;
        }

        return msg;
    }

    /**
     * Class representing data about connected clients.
     */
    public class ClientData {
        public static final int BUFFER_SIZE = 4096;
        private final Logger logger = LoggerFactory.getLogger(ClientData.class);

        private final Player player;
        private final PrintStream out;
        private final InputStream in;

        ClientData(Player player, Socket socket) throws IOException {
            this.player = player;
            this.out = new PrintStream(socket.getOutputStream());
            this.in = socket.getInputStream();
        }

        void send(Message msg) {
            this.out.println(msg.toJSON());
            this.out.flush();

            logger.info("[{}] Sending message to {}: {}", player, msg.toJSON());
        }

        Message receive() throws IOException {
            StringBuilder input = new StringBuilder();
            byte[] buffer = new byte[BUFFER_SIZE];
            while (in.available() != 0) {
                int read = in.read(buffer, 0, BUFFER_SIZE);
                input.append(new String(buffer, 0, read));
            }

            String messageString = input.toString();
            if (messageString.isEmpty()) {
                return null;
            }

            logger.info("[{}] Received message from {}: {}", player, messageString);
            try {
                return ParsingMessageFactory.from(input.toString());
            } catch (JsonParsingException e) {
                logger.error("Malformed JSON", e);
                return null;
            }
        }

        public Player getPlayer() {
            return player;
        }
    }
}
