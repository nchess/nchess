package com.github.elementbound.nchess.swingui;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.game.Move;
import com.github.elementbound.nchess.game.Node;
import com.github.elementbound.nchess.game.Piece;
import com.github.elementbound.nchess.game.Player;
import com.github.elementbound.nchess.net.Client;
import com.github.elementbound.nchess.net.event.client.ConnectFailEvent;
import com.github.elementbound.nchess.net.event.client.ConnectSuccessEvent;
import com.github.elementbound.nchess.net.event.client.GameStateUpdateEvent;
import com.github.elementbound.nchess.net.event.client.MoveEvent;
import com.github.elementbound.nchess.view.GamePanel;
import com.github.elementbound.nchess.view.event.NodeSelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

/**
 * A game client with a Swing-based UI.
 */
public class SwingUI {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwingUI.class);

    private JFrame frame;
    private JPanel toolsPanel;
    private JPanel gamePanelWrapper;
    private JButton btnConnect;
    private JButton btnResign;
    private JButton btnQuit;
    private JTextPane eventLog;
    private GamePanel gamePanel;

    private String message;
    private Client client = null;

    private Optional<Node> selectedNode = Optional.empty();

    /**
     * Create the application.
     */
    public SwingUI() {
        initialize();
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                SwingUI window = new SwingUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

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
        setMessage("Connection successful!");

        btnConnect.setText("Connect");
        btnConnect.setEnabled(false);
    }

    private void onMove(MoveEvent event) {
        Player currentPlayer = gamePanel.getGameState().getCurrentPlayer();
        Player nextPlayer = event.getGameState().getCurrentPlayer();
        Player myPlayer = client.getPlayer();

        LOGGER.info("Move event", event);
        setMessage(String.format("Last move by %s, next up is %s ( you are %s )", currentPlayer, nextPlayer, myPlayer));

        gamePanel.setGameState(event.getGameState());
    }

    private void onNodeSelect(NodeSelectEvent event) {
        GamePanel gamePanel = event.getSource();
        Set<Node> highlitNodes = gamePanel.getHighlitNodes();
        GameState gameState = gamePanel.getGameState();
        Node node = event.getNode();
        Optional<Piece> pieceAtNode = gameState.getPieceAt(node).filter(p -> p.getPlayer().equals(client.getPlayer()));

        highlitNodes.clear();

        if (selectedNode.isPresent()) {
            Node selected = selectedNode.get();
            Optional<Piece> piece = gameState.getPieceAt(selected);

            LOGGER.info("Selected node: {}", selected);
            LOGGER.info("Selected piece: {}", piece);

            Move move = new Move(selected, node);
            LOGGER.info("Trying move: {}", move);

            client.move(move);
            selectedNode = Optional.empty();
        } else if (pieceAtNode.isPresent()) {
            LOGGER.info("Selecting node: {}", node);

            Piece piece = pieceAtNode.get();

            // Highlight piece and nodes it can reach
            highlitNodes.clear();
            highlitNodes.add(node);
            piece.getMoves(gameState).stream()
                    .map(Move::getTo)
                    .forEach(highlitNodes::add);

            selectedNode = Optional.of(node);
        } else {
            LOGGER.info("Clearing selection");
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

            Map<String, Image> pieceImages = new HashMap<>();
            for (Entry<String, String> e : imageFiles.entrySet()) {
                String pieceFile = e.getValue();
                String pieceName = e.getKey();

                LOGGER.info("Loading {} for {}", pieceFile, pieceName);

                Image image;
                try {
                    image = ImageIO.read(getResourceAsStream(pieceFile));
                    pieceImages.put(pieceName, image);
                } catch (IOException e1) {
                    LOGGER.error("Couldn't load {}", pieceFile, e1);
                }
            }

            gamePanel.setPieceImages(pieceImages);

            gamePanel.getNodeSelectEventEventSource().subscribe(this::onNodeSelect);

            gamePanelWrapper.add(gamePanel, BorderLayout.CENTER);
            gamePanel.setGameState(gameState);
            gamePanel.setBounds(gamePanelWrapper.getBounds());
            gamePanel.repaint();
        }
    }

    private InputStream getResourceAsStream(String pieceFile) {
        return this.getClass().getClassLoader().getResourceAsStream(pieceFile);
    }

    private void deinitState() {
        gamePanelWrapper.remove(gamePanel);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        gamePanelWrapper = new JPanel();
        frame.getContentPane().add(gamePanelWrapper, BorderLayout.CENTER);
        gamePanelWrapper.setLayout(new BorderLayout(0, 0));

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
        if (n == 0) {
            System.exit(0);
        }
    }

    private void onConnectPressed(ActionEvent event) {
        String host;
        String portStr;
        int port;

        host = "localhost"; //JOptionPane.showInputDialog("Host address?");
        if (host == null) {
            return;
        }

        while (true) {
            portStr = "60001"; //JOptionPane.showInputDialog("Host port?");
            if (portStr == null) {
                return;
            }

            try {
                port = Integer.parseInt(portStr);
                if (port < 0 || port > 65535) {
                    throw new NumberFormatException();
                }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        String dateString = Date.from(Instant.now()).toString();
        this.message = message;

        eventLog.setText(String.format("[%s] %s", dateString, message));
    }
}
