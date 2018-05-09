package com.github.elementbound.nchess.demos;

import com.github.elementbound.nchess.game.GameState;
import com.github.elementbound.nchess.marshalling.JsonGameStateParser;
import com.github.elementbound.nchess.view.DefaultGamePanelListener;
import com.github.elementbound.nchess.view.GamePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Window.Type;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ViewDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ViewDemo.class);

	private JFrame frame;

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
	 * Create the application.
	 */
	public ViewDemo() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setType(Type.NORMAL);
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GamePanel panel = new GamePanel();
		panel.setBounds(frame.getContentPane().getBounds());
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		String fname = "2hexa.json";
		InputStream fileInput = getResourceAsStream(fname);
		if(fileInput == null) {
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
		
		for(Entry<String, String> e: imageFiles.entrySet()) {
            String pieceFile = e.getValue();
            String pieceName = e.getKey();
            LOGGER.info("Loading {} for {}... ", pieceFile, pieceName);

			Image image;
			try {
				image = ImageIO.read(getResourceAsStream(pieceFile));
				panel.pieceImages.put(pieceName, image);
			} catch (IOException e1) {
			    LOGGER.error("Failed loading {}", pieceFile);
			}
		}

		panel.setPreferredSize(new Dimension(800, 600));
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
