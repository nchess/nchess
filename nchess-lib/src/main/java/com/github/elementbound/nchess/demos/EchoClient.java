package com.github.elementbound.nchess.demos;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Player;
import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.net.Client;
import com.github.elementbound.nchess.util.event.client.TurnEvent;

import java.io.IOException;
import java.util.*;

public class EchoClient {
    private Player myPlayer;

	public static void main(String[] args) {
		new EchoClient().run(args);
	}
	
	public void run(String[] args) {
		Scanner sc = new Scanner(System.in);
		String host = "localhost";
		int port = 60001;
		
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
     * @param event turn event
     */
	private void onTurn(TurnEvent event) {
        Player player = event.getPlayer();
        Client client = event.getClient();

        if(player.equals(myPlayer)) {
            GameState gameState = event.getClient().getGameState();
            Set<Move> possibleMoves = gameState.getMovesByPlayer(myPlayer);

            ArrayList<Move> movesList = new ArrayList<>(possibleMoves);
            Collections.shuffle(movesList);
            client.move(movesList.get(0));
        }
    }
}
