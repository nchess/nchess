package com.github.elementbound.nchess.demos;

import com.github.elementbound.nchess.util.JsonTableLoader;
import com.github.elementbound.nchess.view.DefaultTablePanelListener;
import com.github.elementbound.nchess.view.TablePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Window.Type;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ViewDemo {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewDemo window = new ViewDemo();
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
		
		TablePanel panel = new TablePanel();
		panel.setBounds(frame.getContentPane().getBounds());
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		String fname = "2hexa.json";
		if(this.getClass().getClassLoader().getResourceAsStream(fname) == null) {
			System.out.println("Couldn't load " + fname);
			return; 
		}
		
		JsonTableLoader jsonLoader = new JsonTableLoader(this.getClass().getClassLoader().getResourceAsStream(fname));
		if(!jsonLoader.parse()) {
			System.out.println("Ill-formatted json");
			return; 
		}
		
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
				panel.pieceImages.put(e.getKey(), image);
				System.out.println("success");
			} catch (IOException e1) {
				System.out.println("fail");
				e1.printStackTrace();
			}
		}

		panel.setPreferredSize(new Dimension(800, 600));
		frame.pack();
		panel.assignTable(jsonLoader.getResult());
		panel.setForeground(Color.black);
		panel.setBackground(Color.white);
		
		DefaultTablePanelListener listener = new DefaultTablePanelListener();
		listener.assignTo(panel);
	}
}
