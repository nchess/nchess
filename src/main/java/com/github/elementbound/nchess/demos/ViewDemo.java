package com.github.elementbound.nchess.demos;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.marshalling.JsonGameStateParser;
import com.github.elementbound.nchess.view.DefaultGamePanelListener;
import com.github.elementbound.nchess.view.GamePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Window.Type;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>Simple application showing off the game state visualizer.
 *
 * @see GamePanel
 */
public class ViewDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewDemo.class);
    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;
    public static final int DEFAULT_X = 100;
    public static final int DEFAULT_Y = 100;

    private JFrame frame;

    /**
     * Create the application.
     */
    public ViewDemo() {
        initialize();
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ViewDemo window = new ViewDemo();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setType(Type.NORMAL);
        frame.setBounds(DEFAULT_X, DEFAULT_Y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GamePanel panel = new GamePanel();
        panel.setBounds(frame.getContentPane().getBounds());
        frame.getContentPane().add(panel, BorderLayout.CENTER);

        String fname = "2hexa.json";
        InputStream fileInput = getResourceAsStream(fname);
        if (fileInput == null) {
            LOGGER.error("Couldn't load {}", fname);
            return;
        }

        JsonGameStateParser jsonLoader = new JsonGameStateParser();
        GameState gameState = jsonLoader.parse(fileInput);
        LOGGER.info("Map {} successfully loaded", fname);

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
            LOGGER.info("Loading {} for {}... ", pieceFile, pieceName);

            Image image;
            try {
                image = ImageIO.read(getResourceAsStream(pieceFile));
                pieceImages.put(pieceName, image);
            } catch (IOException e1) {
                LOGGER.error("Failed loading {}", pieceFile);
            }
        }

        panel.setPieceImages(pieceImages);
        panel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        frame.pack();

        panel.setGameState(gameState);
        panel.setForeground(Color.black);
        panel.setBackground(Color.white);

        DefaultGamePanelListener listener = new DefaultGamePanelListener();
        listener.attachTo(panel);
    }

    private InputStream getResourceAsStream(String value) {
        return this.getClass().getClassLoader().getResourceAsStream(value);
    }
}
