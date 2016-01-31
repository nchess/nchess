package com.github.elementbound.nchess.view;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.github.elementbound.nchess.game.Table;

public class NchessPanel extends JPanel {
	private Table table; 
	private List<Polygon> polys = new ArrayList<>();
	
	public NchessPanel() {

	}

	public void assignTable(Table table) {
		this.table = table; 
		
		polys.clear();
		//TODO: Create polys
	}
}
