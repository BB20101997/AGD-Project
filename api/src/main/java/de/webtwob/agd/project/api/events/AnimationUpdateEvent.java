package de.webtwob.agd.project.api.events;

/**
 * An Event used to inform subscribers about an updated frame
 * */
public class AnimationUpdateEvent implements IAnimationEvent {

	private final long frame;
	
	/**
	 * @param newFrame  the new frame index to be displayed
	 * 
	 * */
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
