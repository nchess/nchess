package com.github.elementbound.nchess.demos;

import com.github.elementbound.nchess.game.*;
import com.github.elementbound.nchess.game.exception.InvalidMoveException;
import com.github.elementbound.nchess.net.Client;
import com.github.elementbound.nchess.util.event.client.ConnectFailEvent;
import com.github.elementbound.nchess.util.event.client.ConnectSuccessEvent;
import com.github.elementbound.nchess.util.event.client.GameStateUpdateEvent;
import com.github.elementbound.nchess.util.event.client.MoveEvent;
import com.github.elementbound.nchess.view.GamePanel;
import com.github.elementbound.nchess.view.event.NodeSelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

public class ClientUI {
    private static Logger LOGGER = LoggerFactory.getLogger(ClientUI.class);

    private JFrame frame;
    private JPanel toolsPanel;
    private JButton btnConnect;
    private JButton btnResign;
    private JButton btnQuit;
    private JTextPane eventLog;
    private GamePanel gamePanel;

    private Client client = null;

    private Optional<Node> selectedNode = Optional.empty();

    private void onGameStateUpdate(GameStateUpdateEvent event) {
        LOGGER.info("Game state update", event);

        initState(event.getGameState());
    }

    private void onFailedConnect(ConnectFailEvent event) {
        LOGGER.info("Connection failed!", event);

        btnConnect.setText("Connect");
        btnConnect.setEnabled(true);
    }

    private void onSuccessfulConnect(ConnectSuccessEvent event) {
        LOGGER.info("Connection successful!", event);

        btnConnect.setText("Connect");
        btnConnect.setEnabled(false);
    }

    private void onMove(MoveEvent event) {
        LOGGER.info("Move event", event);
        gamePanel.setGameState(event.getGameState());
    }

    private void onNodeSelect(NodeSelectEvent event) {
        GamePanel gamePanel = event.getSource();
        Set<Node> highlitNodes = gamePanel.getHighlitNodes();
        GameState gameState = gamePanel.getGameState();
        Node node = event.getNode();
        Optional<Piece> pieceAtNode = gameState.getPieceAt(node);

        highlitNodes.clear();

        if(selectedNode.isPresent()) {
            Node selected = selectedNode.get();
            Optional<Piece> piece = gameState.getPieceAt(selected);

            LOGGER.info("Selected node: {}", selected);
            LOGGER.info("Selected piece: {}", piece);

            try {
                Move move = new Move(selected, node);
                GameState newState = gameState.applyMove(move);
                gamePanel.setGameState(newState);

                selectedNode = Optional.empty();
            } catch (InvalidMoveException e) {
                LOGGER.warn("Failed move", e);
            }
        } else if(pieceAtNode.isPresent()) {
            Piece piece = pieceAtNode.get();

            // Highlight piece and nodes it can reach
            highlitNodes.clear();
            highlitNodes.add(node);
            piece.getMoves(gameState).stream()
                    .map(Move::getTo)
                    .forEach(highlitNodes::add);
        } else {
            selectedNode = Optional.empty();
        }

        gamePanel.repaint();
    }

    private void initState(GameState gameState) {
        if (gamePanel == null) {
            gamePanel = new GamePanel();

            LOGGER.info("Loading piece images");
            Map<String, String> imageFiles = new HashMap<>();
            imageFiles.put("pawn", "pieces/pawn.png");
            imageFiles.put("rook", "pieces/rook.png");
            imageFiles.put("knight", "pieces/knight.png");
            imageFiles.put("bishop", "pieces/bishop.png");
            imageFiles.put("queen", "pieces/queen.png");
            imageFiles.put("king", "pieces/king.png");

            for (Entry<String, String> e : imageFiles.entrySet()) {
                String pieceFile = e.getValue();
                String pieceName = e.getKey();

                LOGGER.info("Loading {} for {}", pieceFile, pieceName);

                Image image;
                try {
                    image = ImageIO.read(getResourceAsStream(pieceFile));
                    gamePanel.pieceImages.put(pieceName, image);
                } catch (IOException e1) {
                    LOGGER.error("Couldn't load {}", pieceFile, e1);
                }
            }

            gamePanel.getNodeSelectEventEventSource().subscribe(this::onNodeSelect);
        }

        gamePanel.add(gamePanel, BorderLayout.CENTER);
        gamePanel.setGameState(gameState);
        gamePanel.setBounds(gamePanel.getBounds());
        gamePanel.repaint();
    }

    private InputStream getResourceAsStream(String pieceFile) {
        return this.getClass().getClassLoader().getResourceAsStream(pieceFile);
    }

    private void deinitState() {
        gamePanel.remove(gamePanel);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ClientUI window = new ClientUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
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

        gamePanel = new GamePanel();
        frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
        gamePanel.setLayout(new BorderLayout(0, 0));

        toolsPanel = new JPanel();
        frame.getContentPane().add(toolsPanel, BorderLayout.EAST);
        toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.Y_AXIS));

        btnConnect = new JButton("Connect");
        btnConnect.addActionListener(this::onConnectPressed);

        toolsPanel.add(btnConnect);

        btnResign = new JButton("Resign");
        toolsPanel.add(btnResign);

        btnQuit = new JButton("Quit");
        btnQuit.addActionListener(this::onQuitPressed);
        toolsPanel.add(btnQuit);

        eventLog = new JTextPane();
        frame.getContentPane().add(eventLog, BorderLayout.SOUTH);
    }

    private void onQuitPressed(ActionEvent event) {
        int n = JOptionPane.showConfirmDialog(null, "Are you sure?");
        if (n == 0)
            System.exit(0);
    }

    private void onConnectPressed(ActionEvent event) {
        String host;
        String portStr;
        int port;

        host = "localhost"; //JOptionPane.showInputDialog("Host address?");
        if (host == null)
            return;

        while (true) {
            portStr = "60001"; //JOptionPane.showInputDialog("Host port?");
            if (portStr == null)
                return;

            try {
                port = Integer.parseInt(portStr);
                if (port < 0 || port > 65535)
                    throw new NumberFormatException();

                break;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Enter an integer between 0 and 65535 inclusive");
            }
        }

        client = new Client(host, port);
        subscribeToClientEvents(client);

        btnConnect.setText("...");
        btnConnect.setEnabled(false);

        Thread clientThread = new Thread(client, "comm");
        clientThread.start();
    }

    private void subscribeToClientEvents(Client client) {
        client.getSuccessfulConnectEventSource().subscribe(this::onSuccessfulConnect);
        client.getFailedConnectEventSource().subscribe(this::onFailedConnect);
        client.getGameStateUpdateEventSource().subscribe(this::onGameStateUpdate);
        client.getMoveEventSource().subscribe(this::onMove);
    }

}
