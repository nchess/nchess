package com.github.elementbound.nchess.demos;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClientUI {

	private JFrame frame;

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
		
		JPanel GamePanel = new JPanel();
		frame.getContentPane().add(GamePanel, BorderLayout.CENTER);
		
		JPanel ToolsPanel = new JPanel();
		frame.getContentPane().add(ToolsPanel, BorderLayout.EAST);
		ToolsPanel.setLayout(new BoxLayout(ToolsPanel, BoxLayout.Y_AXIS));
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.print("Whoa!");
			}
		});
		ToolsPanel.add(btnConnect);
		
		JButton btnResign = new JButton("Resign");
		ToolsPanel.add(btnResign);
		
		JButton btnQuit = new JButton("Quit");
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showConfirmDialog(null, "Are you sure?");
				if(n == 0)
					System.exit(0);
			}
		});
		ToolsPanel.add(btnQuit);
	}

}
