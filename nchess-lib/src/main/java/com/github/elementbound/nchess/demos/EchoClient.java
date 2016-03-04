package com.github.elementbound.nchess.demos;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
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
				System.out.println(sin.nextLine());
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
