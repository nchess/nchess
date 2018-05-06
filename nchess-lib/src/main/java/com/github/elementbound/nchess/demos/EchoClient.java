package com.github.elementbound.nchess.demos;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.net.Client;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class EchoClient {
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
		client.setListener(new ClientEventListener() {
			
			@Override
			public void onTableUpdate(Client client, Table table) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMyTurn(Client client) {
				Table table = client.table();
				List<Move> moves = table.getMovesByPlayer(client.playerId());
				Random rng = new Random();
				
				//Choose a random move 
				client.move(moves.get(rng.nextInt(moves.size())));
			}
			
			@Override
			public void onJoinResponse(Client client, boolean approved, long playerId) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailedConnect(Client client, IOException e) {
				System.out.println("Connection failed: ");
				e.printStackTrace();
			}

			@Override
			public void onSuccessfulConnect(Client client) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMove(Client client, Table table, Move move) {
				// TODO Auto-generated method stub
				
			}
		});
		
		client.run();
	}
}
