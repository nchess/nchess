package com.github.elementbound.nchess.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import javax.json.stream.JsonParsingException;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.net.protocol.JoinResponseMessage;
import com.github.elementbound.nchess.net.protocol.Message;
import com.github.elementbound.nchess.net.protocol.MessageParser;
import com.github.elementbound.nchess.net.protocol.MoveMessage;
import com.github.elementbound.nchess.net.protocol.PlayerTurnMessage;
import com.github.elementbound.nchess.net.protocol.TableUpdateMessage;

public class Client {
	private Socket socket; 
	private Table table = null; 
	private boolean isMyTurn = false; 
	private long playerId = -1;
	
	private PrintStream out; 
	private InputStream in; 
	private Scanner sin; 

	public void send(Message msg) {
		this.out.print(msg.toJSON());
	}
	
	public Message receive() {
		StringBuilder strb = new StringBuilder();
		byte[] buffer = new byte[4096];

		try {
			while(in.available() != 0) {
				int read;
					read = in.read(buffer, 0, 4096);
				strb.append(new String(buffer, 0, read));
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null; 
		}

		String msgstr = strb.toString();
		if(msgstr.isEmpty())
			return null; 
		
		//System.out.printf("--- --- ---\n < \n%s\n\n", msgstr);
		try {
			return MessageParser.parse(strb.toString());
		}
		catch(JsonParsingException e) {
			System.out.printf("[Warning]Malformed JSON: %s\n", e.getMessage());
			return null;
		}
	}
	
	public void run(String host, int port) {
		try {
			System.out.printf("Connecting to %s:%d\n", host, port);
			
			socket = new Socket(host, port);
			
			out = new PrintStream(socket.getOutputStream());
			in = socket.getInputStream();
			sin = new Scanner(in);
			
			while(sin.hasNext()) {
				String line = sin.nextLine();
				
				Message msg = MessageParser.parse(line);
				if(msg == null) {
					System.out.println("Unknown message!");
					continue; 
				}
				
				if(msg instanceof JoinResponseMessage) {
					if(!((JoinResponseMessage) msg).approved())
						//TODO: Fire unapproved event
						return; 
					
					this.playerId = ((JoinResponseMessage) msg).playerId();
					System.out.printf("Server approved as player %d\n", this.playerId);
				} 
				else if(msg instanceof PlayerTurnMessage) {
					this.isMyTurn = (((PlayerTurnMessage) msg).playerId() == this.playerId);
					System.out.printf("Current player is %d\n", ((PlayerTurnMessage) msg).playerId());
					
					//TODO: fire event
					send(new MoveMessage(new Move(0, 0)));
				}
				else if(msg instanceof TableUpdateMessage) {
					TableUpdateMessage tmsg = (TableUpdateMessage)msg;
					
					System.out.printf("Updated table with %d nodes, %d pieces, and %d players\n", 
							tmsg.table().allNodes().size(),
							tmsg.table().allPieces().size(),
							tmsg.table().allPlayers().size());
					
					this.table = tmsg.table();
				}
			}
			
			out.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isMyTurn() {
		return this.isMyTurn;
	}
	
	public Table table() {
		return this.table; 
	}
}
