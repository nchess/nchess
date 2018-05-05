package com.github.elementbound.nchess.game;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class Move {
	private long fromId; 
	private long toId; 
	
	public Move(long fromId, long toId) {
		this.fromId = fromId;
		this.toId = toId;
	}
	
	public long from() {
		return this.fromId;
	}
	
	public long to() {
		return this.toId; 
	}
	
	@Override 
	public String toString() {
		return ReflectionToStringBuilder.toString(this, SHORT_PREFIX_STYLE);
	}
	
	@Override 
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
