package com.github.elementbound.nchess.demos;

import com.github.elementbound.nchess.net.Server;
import com.github.elementbound.nchess.marshalling.JsonTableParser;

import java.io.IOException;

public class CLIServer {
	public static final int PORT = 60001;

	public static void main(String[] args) {
		CLIServer app = new CLIServer();
		app.run(args);
	}

	public void run(String[] args) {
		String fname = "2hexa.json";
		System.out.printf("Loading map %s...\n", fname);
		
		if(this.getClass().getClassLoader().getResourceAsStream(fname) == null) {
			System.out.println("Couldn't load " + fname);
			return; 
		}
		
		JsonTableParser jsonLoader = new JsonTableParser(this.getClass().getClassLoader().getResourceAsStream(fname));
		if(!jsonLoader.parse()) {
			System.out.println("Ill-formatted json");
			return; 
		}
		
		System.out.println("Map loaded!");
		
		//=====================================================================================
		
		System.out.println("Starting server... ");
		Server server = new Server(jsonLoader.getResult());
		try {
			server.run(PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
