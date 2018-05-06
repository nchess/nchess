package com.github.elementbound.nchess.net;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.net.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

	private final String host;
	private final int port;

	private GameState gameState;
	private String playerId;
	private boolean myTurn;
    private PrintStream out;

	private ClientEventListener listener = null; 
	
	public Client(String host, int port) {
		this.host = host; 
		this.port = port; 
	}

	protected void send(PrintStream out, Message msg) {
		out.print(msg.toJSON());
	}
	
	public void run() {
		try {
			LOGGER.info("Connecting to {}:{}", host, port);
			Socket socket;

			try {
				socket = new Socket(host, port);
			}
			catch (IOException e) {
                LOGGER.error("Connecting to {}:{} failed!", host, port);
				
				if(listener != null)
					listener.onFailedConnect(this, e);
				return; 
			}
			
			out = new PrintStream(socket.getOutputStream());
			InputStream in = socket.getInputStream();
			Scanner sin = new Scanner(in);
			
			if(listener != null)
				listener.onSuccessfulConnect(this);
			
			while(sin.hasNext()) {
				String line = sin.nextLine();
				
				Message msg = ParsingMessageFactory.from(line);
				if(msg == null) {
					LOGGER.error("Unknown message! Contents: {}", line);
					continue; 
				}
				
				if(msg instanceof JoinResponseMessage) {
					if(listener != null)
						listener.onJoinResponse(this, 
								((JoinResponseMessage) msg).isApproved(),
								((JoinResponseMessage) msg).getPlayerId());
					
					if(!((JoinResponseMessage) msg).isApproved())
						return; 
					
					playerId = ((JoinResponseMessage) msg).getPlayerId();
					LOGGER.info("Server approved as player {}", playerId);
				} 
				else if(msg instanceof PlayerTurnMessage) {
					myTurn = (((PlayerTurnMessage) msg).getPlayerId() == playerId);
					LOGGER.info("Current player is {}", ((PlayerTurnMessage) msg).getPlayerId());
					
					if(listener != null && isMyTurn())
						listener.onMyTurn(this);
				}
				else if(msg instanceof MoveMessage) {
					gameState = gameState.applyMove(((MoveMessage) msg).getMove());
					
					if(this.listener != null)
						this.listener.onMove(this, gameState, ((MoveMessage) msg).getMove());
				}
				else if(msg instanceof TableUpdateMessage) {
					TableUpdateMessage tmsg = (TableUpdateMessage)msg;
					
					LOGGER.info("Updated table with {} nodes, {} pieces, and {} players\n",
							tmsg.getTable().getNodes().size(),
							tmsg.getTable().allPieces().size(),
							tmsg.getTable().allPlayers().size());
					
					this.gameState = tmsg.getTable();
					if(listener != null)
						listener.onTableUpdate(this, gameState);
				}
			}
			
			out.close();
			socket.close();
		} catch (IOException e) {
			LOGGER.error("Socket communication error!", e);
		}
	}
	
	public boolean isMyTurn() {
		return this.myTurn;
	}

    public GameState getGameState() {
        return gameState;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setListener(ClientEventListener listener) {
		this.listener = listener; 
	}
	
	public boolean move(Move move) {
		if(!isMyTurn())
			return false; 

		send(out, new MoveMessage(move));
		return true;
	}
}
