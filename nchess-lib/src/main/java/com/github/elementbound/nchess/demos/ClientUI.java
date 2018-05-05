package com.github.elementbound.nchess.demos;

import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Table;
import com.github.elementbound.nchess.net.Client;
import com.github.elementbound.nchess.net.ClientEventListener;
import com.github.elementbound.nchess.view.TablePanel;
import com.github.elementbound.nchess.view.TablePanelListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ClientUI {

	private JFrame frame;
	private JPanel gamePanel;
	private JPanel toolsPanel; 
	private JButton btnConnect; 
	private JButton btnResign; 
	private JButton btnQuit; 
	private JTextPane eventLog; 
	private TablePanel tablePanel = null;
	
	private Client client = null;
	
	class ClientHandler implements ClientEventListener {

		@Override
		public void onTableUpdate(Client client, Table table) {
			System.out.println("Received table data!");
			
			initTable(table);
		}

		@Override
		public void onJoinResponse(Client client, boolean approved, long playerId) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMyTurn(Client client) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFailedConnect(Client client, IOException e) {
			btnConnect.setText("Connect");
			btnConnect.setEnabled(true);
		}

		@Override
		public void onSuccessfulConnect(Client client) {
			btnConnect.setText("Connect");
			btnConnect.setEnabled(false);
		}

		@Override
		public void onMove(Client client, Table table, Move move) {
			System.out.printf("Got move %s, repainting field\n", move.toString());
			tablePanel.repaint();
		}
	}

	class ViewHandler implements TablePanelListener {
		long selectedNode = -1;

		@Override
		public void nodeSelect(TablePanel source, long nodeId) {
			source.clearHighlights();
			
			if(selectedNode >= 0) {
				long pieceId = source.getTable().pieceAt(selectedNode);
				Piece piece = source.getTable().getPiece(pieceId);
				
				System.out.printf("Selected node: %d\n", selectedNode);
				System.out.printf("Selected piece: %d\n", pieceId);
				System.out.printf("Selected piece: %s\n", piece.toString());
				
				Move moveCandidate = new Move(selectedNode, nodeId);
				if(piece.hasMove(moveCandidate, source.getTable()))
					client.move(moveCandidate);
				
				source.repaint();
				selectedNode = -1;
				return; 
			}
			
			selectedNode = -1;
			
			long pieceId = source.getTable().pieceAt(nodeId);
			if(pieceId < 0) {
				source.repaint();
				return; 
			}
			
			Piece piece = source.getTable().getPiece(pieceId);
			
			source.highlightNode(nodeId);
			if(piece.player() == client.playerId()) {
				for(Move move : piece.getMoves(source.getTable()))
					source.highlightNode(move.to());
				
				selectedNode = nodeId;
				System.out.printf("Saved node %d as selected\n", selectedNode);
			}
			
			source.repaint();
		}
		
	}
	
	private void initTable(Table table) {
		if(tablePanel == null) {
			tablePanel = new TablePanel();
			
			Map<String, String> imageFiles = new HashMap<>();
			imageFiles.put("pawn", "pieces/pawn.png");
			imageFiles.put("rook", "pieces/rook.png");
			imageFiles.put("knight", "pieces/knight.png");
			imageFiles.put("bishop", "pieces/bishop.png");
			imageFiles.put("queen", "pieces/queen.png");
			imageFiles.put("king", "pieces/king.png");
			
			for(Entry<String, String> e: imageFiles.entrySet()) {
				System.out.printf("Loading %s for %s... ", e.getValue(), e.getKey());
				Image image;
				try {
					image = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(e.getValue()));
					tablePanel.pieceImages.put(e.getKey(), image);
					System.out.println("success");
				} catch (IOException e1) {
					System.out.println("fail");
					e1.printStackTrace();
				}
			}
			
			tablePanel.addListener(new ViewHandler());
		}
		
		gamePanel.add(tablePanel, BorderLayout.CENTER);
		tablePanel.assignTable(table);
		tablePanel.setBounds(gamePanel.getBounds());
		gamePanel.repaint();
	}
	
	private void deinitTable() {
		gamePanel.remove(tablePanel);
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientUI window = new ClientUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		gamePanel = new JPanel();
		frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
		gamePanel.setLayout(new BorderLayout(0, 0));
		
		toolsPanel = new JPanel();
		frame.getContentPane().add(toolsPanel, BorderLayout.EAST);
		toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.Y_AXIS));
		
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String host; 
				String portStr; 
				int port; 
				
				host = "localhost"; //JOptionPane.showInputDialog("Host address?");
				if(host == null)
					return; 
				
				while(true) {
					portStr = "60001"; //JOptionPane.showInputDialog("Host port?");
					if(portStr == null)
						return; 
					
					try {
						port = Integer.parseInt(portStr);
						if(port < 0 || port > 65535)
							throw new NumberFormatException();
						
						break;
					}
					catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(null, "Enter an integer between 0 and 65535 inclusive");
					}
				}
				
				client = new Client(host, port);
				client.setListener(new ClientHandler());
				
				btnConnect.setText("...");
				btnConnect.setEnabled(false);
				
				Thread clientThread = new Thread(client, "comm");
				clientThread.start();
			}
		});
		toolsPanel.add(btnConnect);
		
		btnResign = new JButton("Resign");
		toolsPanel.add(btnResign);
		
		btnQuit = new JButton("Quit");
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showConfirmDialog(null, "Are you sure?");
				if(n == 0)
					System.exit(0);
			}
		});
		toolsPanel.add(btnQuit);
		
		eventLog = new JTextPane();
		frame.getContentPane().add(eventLog, BorderLayout.SOUTH);
	}

}
