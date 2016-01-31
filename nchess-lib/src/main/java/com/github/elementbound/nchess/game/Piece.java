package com.github.elementbound.nchess.game;

import java.util.List;

public abstract class Piece {
	public abstract String getName(); 
	public abstract List<Move> getMoves(); 
}
