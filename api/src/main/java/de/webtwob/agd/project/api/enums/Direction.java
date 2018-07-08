package de.webtwob.agd.project.api.enums;

/**
 * This Enum is used for the current play direction of an Animation
 * */
public enum Direction {

	/**
	 * Indicates that the animation should be played forward
	 * */
	FORWARD(1),
	/**
	 * Indicates that the animation should be played backward
	 * */
	BACKWARD(-1),
	/**
	 * Indicates that the animation should be paused
	 * */
	PAUSE(0);
	
	/**
	 * The multiplier for the absolute speed
	 * */
	public final int multiplyier;
	
	private Direction(int dir) {
		multiplyier = dir;
	}
}
