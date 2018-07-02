package de.webtwob.agd.project.api.enums;

public enum Direction {

	FORWARD(1),
	BACKWARD(-1),
	PAUSE(0);
	
	public final int multiplyier;
	
	private Direction(int dir) {
		multiplyier = dir;
	}
}
