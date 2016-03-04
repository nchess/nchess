package com.github.elementbound.nchess.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.net.protocol.JoinResponseMessage;
import com.github.elementbound.nchess.net.protocol.Message;

public class Server {
	public class ClientData {
		public Socket socket; 
		public PrintStream out; 
		public long id; 
		
		public ClientData(long id, Socket socket) throws IOException {
			this.id = id; 
			this.socket = socket; 
			this.out = new PrintStream(socket.getOutputStream());
		}
		
		public void send(Message msg) {
			this.out.println(msg.toJSON());
			this.out.flush();
			
			System.out.printf("--- --- ---\n > %d\n%s\n\n", this.id, msg.toJSON());
		}
	}
	
	private Table table; 
	private List<ClientData> clients = new ArrayList<>();
	
	public PrintStream out = System.out; 
	
	public Server(Table table) {
		this.table = table; 
	}
	
	public void run(int port) throws IOException {
		ServerSocket listen = new ServerSocket(port);
		
		//Wait for enough players 
		for(long pid : table.allPlayers()) {
			while(true) {
				try {
					this.out.printf("Waiting for player %d...\n", pid);
					Socket s = listen.accept();
					
					//Don't care for join requests atm, just approve them as player
					ClientData cd = new ClientData(pid, s);
					cd.send(new JoinResponseMessage(pid, true));
					clients.add(cd);
					
					this.out.printf("Accepted player %d\n", pid);
					break;
				}
				catch(IOException e) {
					this.out.printf("Tried to accept player %d, exception happened\n", pid);
					e.printStackTrace();
				}
			}
		}
		
		listen.close();
	}
}
