package com.github.elementbound.nchess.demos;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import com.github.elementbound.nchess.net.protocol.Message;
import com.github.elementbound.nchess.net.protocol.MessageParser;
import com.github.elementbound.nchess.net.protocol.MoveMessage;
import com.github.elementbound.nchess.net.protocol.PlayerTurnMessage;
import com.github.elementbound.nchess.net.protocol.TableUpdateMessage;

public class EchoClient {
	public static void main(String[] args) {
		new EchoClient().run(args);
	}
	
	public void run(String[] args) {
		Scanner sc = new Scanner(System.in);
		String host = "localhost";
		int port = 60001;
		
		System.out.println("Host: ");
		host = sc.nextLine(); 
		
		System.out.println("Port: "); 
		port = sc.nextInt();
		sc.close();
		
		try {
			Socket socket = new Socket(host, port);
			
			PrintStream out = new PrintStream(socket.getOutputStream());
			InputStream in = socket.getInputStream();
			Scanner sin = new Scanner(in);
			
			while(sin.hasNext()) {
				String line = sin.nextLine();
				//System.out.println(line);
				
				if(line.length() > 64) {
					System.out.printf("%s...%s\n", 
							line.substring(0, 32),
							line.substring(line.length()-32));
				}
				else 
					System.out.println(line);
				
				Message msg = MessageParser.parse(line);
				if(msg == null) {
					System.out.println("Unknown message!");
					continue; 
				}
				
				if(msg instanceof PlayerTurnMessage) {
					Message response = new MoveMessage();
					out.println(response.toJSON());
				}
				else if(msg instanceof TableUpdateMessage) {
					TableUpdateMessage tmsg = (TableUpdateMessage)msg;
					
					System.out.printf("Updated table with %d nodes, %d pieces, and %d players\n", 
							tmsg.table().allNodes().size(),
							tmsg.table().allPieces().size(),
							tmsg.table().allPlayers().size());
				}
			}
			
			out.close();
			sin.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			return; 
		}
	}
}
