package de.webtwob.agd.project.api.events;

/**
 * An event to inform subscribers about a changed animation speed
 */
public class AnimationSpeedUpdateEvent implements IAnimationEvent {
	
	private final double speed;
	
	/**
	 * @param newSpeed the newly set speed
	 */
	public AnimationSpeedUpdateEvent(double newSpeed) {
		speed = newSpeed;
	}
	
	/**
	 * @return The new AnimationSpeed
	 * */
	public double getSpeed(){
		return speed;
	}

}
