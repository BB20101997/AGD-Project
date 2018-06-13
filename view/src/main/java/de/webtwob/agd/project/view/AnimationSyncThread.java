package de.webtwob.agd.project.view;

import de.webtwob.agd.project.api.IAnimationEventHandler;
import de.webtwob.agd.project.api.LoopEnum;
import de.webtwob.agd.project.api.events.IAnimationEvent;
import de.webtwob.agd.project.api.interfaces.IAnimation;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class AnimationSyncThread extends Thread {

	List<IAnimationEventHandler> handlerList = new LinkedList<>();
	List<Runnable> synced = new LinkedList<>();
	List<IAnimation> animations = new LinkedList<>();

	/**
	 * The frame that should currently be displayed
	 */
	private volatile long frame;

	/**
	 * If the speed is not an integer this will accumulate the fractions
	 */
	private double subFrame;

	/**
	 * Frames per Millisecond
	 */
	private double speed = 1;

	/**
	 * Represents the action to perform when the animation reaches the end e.g.
	 * reverse/loop/stop
	 */
	LoopEnum endAction = LoopEnum.STOP;

	/**
	 * Determines where to start the animation and where to stop if playing
	 * backwards Must always be greater or equal to 0
	 */
	private long startAnimationAt;

	/**
	 * Determines where to end the animation and where to start if playing backwards
	 * Must always be less than the length of the shortest animation
	 * (maxEndAnimationAt)
	 */
	private long endAnimationAt = Long.MAX_VALUE;

	/**
	 * The maximum value endAnimationAt may contain
	 */
	private long maxEndAnimationAt = Long.MAX_VALUE;

	public AnimationSyncThread() {
		this.setDaemon(true);
		this.setName("AnimationSyncThread");
	}

	@Override
	public void run() {

		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();

		while (true) {
			if (speed != 0) {

				// update current frame
				subFrame += (end - start) * speed;
				frame += (long) subFrame;
				subFrame %= 1;

				// set start to current time
				start = System.currentTimeMillis();

				// inform all about new frame
				synced.parallelStream().forEach(Runnable::run);

				// have we reached the end of the Animation
				if (getFrame() < startAnimationAt || getFrame() > endAnimationAt) {
					endAction.handle(nSpeed -> speed = nSpeed, this::setFrame, endAnimationAt, frame, speed);
					handlerList.parallelStream()
							.forEach(h -> EventQueue.invokeLater(() -> h.animationEvent(new IAnimationEvent() {
								/* TODO */})));
				}

				//
				end = System.currentTimeMillis();
			}
		}

	}

	public void subscribeToAnimationEvent(IAnimationEventHandler aeh) {
		handlerList.add(aeh);
	}

	public void addFrameChangeCallback(Runnable run) {
		synced.add(run);
	}

	public void setFrame(long frame) {
		this.frame = frame;
		this.subFrame = 0;
	}

	public long getFrame() {
		return frame;
	}

	public void setLoopAction(LoopEnum loopAction) {
		endAction = loopAction;
	}

	public void setAnimationEnd(long end) {
		endAnimationAt = Math.min(end, maxEndAnimationAt);
	}

	public void addAnimation(IAnimation animation) {
		animations.add(animation);
		updateMaxEnd();
	}

	public void removeAnimation(IAnimation animation) {
		animations.remove(animation);
		updateMaxEnd();
	}

	public void updateMaxEnd() {
		maxEndAnimationAt = animations.stream().mapToLong(IAnimation::getLength).min().orElse(Long.MAX_VALUE);
		endAnimationAt = Math.min(endAnimationAt, maxEndAnimationAt);
	}

}
