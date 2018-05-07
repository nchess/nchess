package com.github.elementbound.nchess.net;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Player;
import com.github.elementbound.nchess.util.event.EventSource;
import com.github.elementbound.nchess.util.event.client.*;
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
	private Player player;
	private boolean myTurn;
    private PrintStream out;

	// Event sources
    private final EventSource<GameStateUpdateEvent> gameStateUpdateEventSource = new EventSource<>();
    private final EventSource<JoinResponseEvent> joinResponseEventSource = new EventSource<>();
    private final EventSource<TurnEvent> turnEventSource = new EventSource<>();
    private final EventSource<MoveEvent> moveEventSource = new EventSource<>();

    private final EventSource<ConnectFailEvent> failedConnectEventSource = new EventSource<>();
    private final EventSource<ConnectSuccessEvent> successfulConnectEventSource = new EventSource<>();
	
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
				failedConnectEventSource.emit(new ConnectFailEvent(this, e, host, port));
				return; 
			}
			
			out = new PrintStream(socket.getOutputStream());
			InputStream in = socket.getInputStream();
			Scanner sin = new Scanner(in);
			successfulConnectEventSource.emit(new ConnectSuccessEvent(this, host, port));
			
			while(sin.hasNext()) {
				String line = sin.nextLine();
				
				Message msg = ParsingMessageFactory.from(line);
				if(msg == null) {
					LOGGER.error("Unknown message! Contents: {}", line);
					continue; 
				}
				
				if(msg instanceof JoinResponseMessage) {
				    joinResponseEventSource.emit(new JoinResponseEvent(this,
                            ((JoinResponseMessage) msg).isApproved(),
                            ((JoinResponseMessage) msg).getPlayer()));
					
					if(!((JoinResponseMessage) msg).isApproved())
						return; 
					
					player = ((JoinResponseMessage) msg).getPlayer();
					LOGGER.info("Server approved as player {}", player);
				} 
				else if(msg instanceof PlayerTurnMessage) {
					myTurn = (((PlayerTurnMessage) msg).getPlayer().equals(player));
					LOGGER.info("Current player is {}", ((PlayerTurnMessage) msg).getPlayer());

					turnEventSource.emit(new TurnEvent(this, ((PlayerTurnMessage) msg).getPlayer(), isMyTurn()));
				}
				else if(msg instanceof MoveMessage) {
					Move move = ((MoveMessage) msg).getMove(gameState);
					gameState = gameState.applyMove(move);
					moveEventSource.emit(new MoveEvent(this, gameState, move));
				}
				else if(msg instanceof GameStateUpdateMessage) {
					GameStateUpdateMessage stateUpdateMessage = (GameStateUpdateMessage)msg;

					LOGGER.info("Updated table with {} nodes, {} pieces, and {} players", new Object[]{
                            stateUpdateMessage.getGameState().getTable().getNodes().size(),
                            stateUpdateMessage.getGameState().getPieces().size(),
                            stateUpdateMessage.getGameState().getPlayers().size()
                    });
					
					gameState = stateUpdateMessage.getGameState();
					gameStateUpdateEventSource.emit(new GameStateUpdateEvent(this, gameState));
				}
			}
			
			out.close();
			socket.close();
		} catch (IOException e) {
			LOGGER.error("Socket communication error!", e);
		}
	}
	
	public boolean move(Move move) {
		if(!isMyTurn())
			return false; 

		send(out, new MoveMessage(move));
		return true;
	}

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public EventSource<GameStateUpdateEvent> getGameStateUpdateEventSource() {
        return gameStateUpdateEventSource;
    }

    public EventSource<JoinResponseEvent> getJoinResponseEventSource() {
        return joinResponseEventSource;
    }

    public EventSource<TurnEvent> getTurnEventSource() {
        return turnEventSource;
    }

    public EventSource<MoveEvent> getMoveEventSource() {
        return moveEventSource;
    }

    public EventSource<ConnectFailEvent> getFailedConnectEventSource() {
        return failedConnectEventSource;
    }

    public EventSource<ConnectSuccessEvent> getSuccessfulConnectEventSource() {
        return successfulConnectEventSource;
    }
}
