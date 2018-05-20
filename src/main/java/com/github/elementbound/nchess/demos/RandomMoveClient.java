package com.github.elementbound.nchess.demos;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.net.Client;
import com.github.elementbound.nchess.net.event.client.TurnEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Set;

/**
 * A command-line client that responds with random moves.
 */
public class RandomMoveClient {

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 60001;

    public static void main(String[] args) {
        new RandomMoveClient().run(args);
    }

    private void run(String[] args) {
        Scanner sc = new Scanner(System.in);
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;

        System.out.println("Host: ");
        //host = sc.nextLine();

        System.out.println("Port: ");
        //port = sc.nextInt();
        sc.close();

        Client client = new Client(host, port);
        client.getTurnEventSource().subscribe(this::onTurn);

        client.run();
    }

    /**
     * Responds with a random move whenever it's the client's turn.
     *
     * @param event turn event
     */
    private void onTurn(TurnEvent event) {
        Client client = event.getClient();

        if (event.isMyTurn()) {
            GameState gameState = event.getClient().getGameState();
            Set<Move> possibleMoves = gameState.getMovesByPlayer(event.getPlayer());

            ArrayList<Move> movesList = new ArrayList<>(possibleMoves);
            Collections.shuffle(movesList);
            client.move(movesList.get(0));
        }
    }
}
