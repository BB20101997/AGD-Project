package de.webtwob.agd.project.api.events;

public class AnimationSpeedUpdateEvent implements IAnimationEvent {
	
	private final double speed;
	
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
