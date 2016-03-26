package com.github.elementbound.nchess.demos;

import java.util.Scanner;

import com.github.elementbound.nchess.net.Client;

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
		
		Client client = new Client();
		client.run(host, port);
	}
}
