package com.github.elementbound.nchess.demos;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.Window.Type;
import javax.swing.JPanel;

import com.github.elementbound.nchess.view.NchessPanel;

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
		frame.setType(Type.UTILITY);
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new NchessPanel();
		panel.setBounds(0, 0, 800, 600);
		frame.getContentPane().add(panel);
	}
}
