package de.webtwob.agd.project.api.events;

public class AnimationUpdateEvent implements IAnimationEvent {

	private final long frame;
	
	public AnimationUpdateEvent(long newFrame) {
		frame = newFrame;
	}
	
	/**
	 * @return the frame the animation is now at
	 * */
	public long getFrame() {
		return frame;
	}

}
