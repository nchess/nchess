package com.github.elementbound.nchess.net.protocol;

public abstract class Message implements JSONable {
	public abstract String toJSON(); 
}
