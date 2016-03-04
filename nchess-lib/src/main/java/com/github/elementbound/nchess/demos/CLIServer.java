package com.github.elementbound.nchess.demos;

import java.io.IOException;

import com.github.elementbound.nchess.net.Server;
import com.github.elementbound.nchess.util.JsonTableLoader;

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
		
		JsonTableLoader jsonLoader = new JsonTableLoader(this.getClass().getClassLoader().getResourceAsStream(fname));
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
