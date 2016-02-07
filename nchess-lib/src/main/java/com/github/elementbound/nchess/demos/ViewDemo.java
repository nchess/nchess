package com.github.elementbound.nchess.demos;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Window.Type;

import javax.swing.JFrame;

import com.github.elementbound.nchess.util.JsonTableLoader;
import com.github.elementbound.nchess.view.DefaultTablePanelListener;
import com.github.elementbound.nchess.view.TablePanel;

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
		frame.getContentPane().setLayout(null);
		
		TablePanel panel = new TablePanel();
		panel.setBounds(0, 0, 800, 600);
		frame.getContentPane().add(panel);
		
		String fname = "hexamap.json";
		if(this.getClass().getClassLoader().getResourceAsStream(fname) == null) {
			System.out.println("Couldn't load " + fname);
			return; 
		}
		
		JsonTableLoader jsonLoader = new JsonTableLoader(this.getClass().getClassLoader().getResourceAsStream(fname));
		if(!jsonLoader.parse()) {
			System.out.println("Ill-formatted json");
			return; 
		}
		
		panel.assignTable(jsonLoader.getResult());
		panel.setForeground(Color.black);
		panel.setBackground(Color.white);
		panel.addListener(new DefaultTablePanelListener());
	}
}
