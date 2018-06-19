package de.webtwob.agd.project.api.events;

public class AnimationUpdateEvent implements IAnimationEvent {

	private final long frame;
	
	public AnimationUpdateEvent(long newFrame) {
		frame = newFrame;
	}
	
	public long getFrame() {
		return frame;
	}

}
