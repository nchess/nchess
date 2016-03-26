package com.github.elementbound.nchess.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.json.stream.JsonParsingException;

import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.net.protocol.JoinResponseMessage;
import com.github.elementbound.nchess.net.protocol.Message;
import com.github.elementbound.nchess.net.protocol.MessageParser;
import com.github.elementbound.nchess.net.protocol.MoveMessage;
import com.github.elementbound.nchess.net.protocol.PlayerTurnMessage;
import com.github.elementbound.nchess.net.protocol.TableUpdateMessage;

public class Server {
	public class ClientData {
		public Socket socket; 
		public PrintStream out; 
		public InputStream in; 
		public long id; 
		
		public ClientData(long id, Socket socket) throws IOException {
			this.id = id; 
			this.socket = socket; 
			this.out = new PrintStream(socket.getOutputStream());
			this.in = socket.getInputStream();
		}
		
		public void send(Message msg) {
			this.out.println(msg.toJSON());
			this.out.flush();
			
			System.out.printf("--- --- ---\n > %d\n%s\n\n", this.id, msg.toJSON());
		}
		
		public Message receive() throws IOException {
			StringBuilder strb = new StringBuilder();
			byte[] buffer = new byte[4096];
			while(in.available() != 0) {
				int read = in.read(buffer, 0, 4096);
				strb.append(new String(buffer, 0, read));
			}

			String msgstr = strb.toString();
			if(msgstr.isEmpty())
				return null; 
			
			System.out.printf("--- --- ---\n < %d\n%s\n\n", this.id, msgstr);
			try {
				return MessageParser.parse(strb.toString());
			}
			catch(JsonParsingException e) {
				System.out.printf("[Warning]Malformed JSON: %s\n", e.getMessage());
				return null;
			}
		}
	}
	
	private Table table; 
	private List<ClientData> clients = new ArrayList<>();
	
	public PrintStream out = System.out; 
	
	public Server(Table table) {
		this.table = table; 
	}
	
	private void broadcast(Message msg) {
		for(ClientData cd : clients)
			cd.send(msg);
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
					cd.send(new TableUpdateMessage(this.table));
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
		
		//Play for some steps then quit 
		for(int i = 0; i < 64; i++) {	
			for(ClientData cd : clients) {
				//Send a player turn notif
				//cd.send(new PlayerTurnMessage(cd.id));
				broadcast(new PlayerTurnMessage(cd.id));
				
				//Wait for response
				Message msg = null;
				int responseTime = 5000;
				try {
					for(long till = System.currentTimeMillis() + responseTime; 
							System.currentTimeMillis() < till;
							Thread.sleep(1000)) {
						msg = cd.receive();
						if(msg != null)
							break; 
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if(msg == null) {
					this.out.printf("No response from %d in %d ms, skipping\n", cd.id, responseTime);
					continue;
				}
				
				if(msg instanceof MoveMessage) {
					MoveMessage movemsg = (MoveMessage)msg;
					table.applyMove(movemsg.move());
				}
				else {
					//!
				}
			}
		}
	}
}
