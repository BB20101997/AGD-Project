package de.webtwob.agd.project.api;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import de.webtwob.agd.project.api.events.AnimationSpeedUpdateEvent;
import de.webtwob.agd.project.api.events.AnimationUpdateEvent;
import de.webtwob.agd.project.api.events.IAnimationEvent;
import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.api.interfaces.IAnimationEventHandler;

public class AnimationSyncThread extends Thread {

	List<IAnimationEventHandler> handlerList = new LinkedList<>();
	List<IAnimation> animations = new LinkedList<>();

	/**
	 * The frame that should currently be displayed
	 */
	private volatile long frame;

	/**
	 * If the speed is not an integer this will accumulate the fractions
	 */
	private volatile double subFrame;

	/**
	 * Frames per Millisecond
	 */
	private volatile double speed = 1;

	private volatile boolean paused = false;

	/**
	 * Represents the action to perform when the animation reaches the end e.g.
	 * reverse/loop/stop
	 */
	volatile LoopEnum endAction = LoopEnum.STOP;

	/**
	 * Determines where to start the animation and where to stop if playing
	 * backwards Must always be greater or equal to 0
	 */
	private volatile long startAnimationAt;

	/**
	 * Determines where to end the animation and where to start if playing backwards
	 * Must always be less than the length of the shortest animation
	 * (maxEndAnimationAt)
	 */
	private volatile long endAnimationAt = Long.MAX_VALUE;

	/**
	 * The maximum value endAnimationAt may contain
	 */
	private volatile long maxEndAnimationAt = Long.MAX_VALUE;

	public AnimationSyncThread() {
		this.setDaemon(true);
		this.setName("AnimationSyncThread");
	}

	@Override
	public void run() {

		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();

		while (true) {
			if (speed != 0 && !paused) {
				// update current frame
				subFrame += (end - start) * speed;
				frame += (long) subFrame;
				subFrame %= 1; // yes, this actually does something since subFrame is a double

				// set start to current time
				start = System.currentTimeMillis();

				// have we reached the end of the Animation
				if (getFrame() < startAnimationAt || getFrame() > endAnimationAt) {
					endAction.handle(this);
				}

				updateFrame(frame);
				interrupted();//possibly clear interrupt flag

				end = System.currentTimeMillis();
			} else {
				start = end;
				subFrame = 0;
			}
		}

	}

	private void fireEvent(IAnimationEvent event) {

		List<IAnimationEventHandler> syncedCopy;

		synchronized (handlerList) {
			syncedCopy = List.copyOf(handlerList);
		}

		// if we are not paused and on the DispatchThread don't send an event this might
		// cause a positive feedback loop of events
		if (!EventQueue.isDispatchThread()) {
			try {
				EventQueue.invokeAndWait(()->fireEventOnEventQueue(syncedCopy, event));
			} catch (InvocationTargetException ignore) {
				// we want to keep running even when the event handler fails
			} catch (InterruptedException interrupt) {
				Thread.currentThread().interrupt();
			}
		} else {
			fireEventOnEventQueue(syncedCopy, event);
		}
	}
	
	/**
	 * This method handles dispatching Events, it assumes to be called on the DispatchThread
	 * */
	private void fireEventOnEventQueue(List<IAnimationEventHandler> handlers ,IAnimationEvent event) {
		if(!EventQueue.isDispatchThread()) {
			throw new IllegalThreadStateException("This methode might only be called on the DispatchThread!");
		}
		 handlers.stream().forEach(h -> {
			try {
				h.animationEvent(event);
			} catch (ThreadDeath e) {
				throw e;
			} catch (Exception ignore) {
				// we want to keep running even when the event handler fails
			}
		});
	}

	public void subscribeToAnimationEvent(IAnimationEventHandler aeh) {
		if (aeh == null) {
			return;
		}
		synchronized (handlerList) {
			handlerList.add(aeh);
		}
	}

	public void unsubscribeFromAnimationEvent(IAnimationEventHandler eventHandler) {
		if (eventHandler == null) {
			return;
		}
		synchronized (handlerList) {
			handlerList.remove(eventHandler);
		}
	}

	public void setFrame(long frame) {
		if (this.frame != frame) {
			subFrame = 0;
			updateFrame(frame);
		}
	}

	private void updateFrame(long frame) {
		this.frame = frame;
		var update = new AnimationUpdateEvent(frame);

		// inform all about new frame
		fireEvent(update);
	}

	public long getFrame() {
		return frame;
	}

	public void setLoopAction(LoopEnum loopAction) {
		endAction = loopAction;
	}
	
	public LoopEnum getLoopAction() {
		return endAction;
	}

	public void setAnimationEnd(long end) {
		endAnimationAt = Math.min(end, maxEndAnimationAt);
	}

	public void addAnimation(IAnimation animation) {
		if (animation == null) {
			return;
		}
		animations.add(animation);
		updateMaxEnd();
	}

	public void removeAnimation(IAnimation animation) {
		if (animation == null) {
			return;
		}
		animations.remove(animation);
		updateMaxEnd();
	}

	public void updateMaxEnd() {
		maxEndAnimationAt = animations.stream().mapToLong(IAnimation::getLength).min().orElse(Long.MAX_VALUE);
		endAnimationAt = Math.min(endAnimationAt, maxEndAnimationAt);
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public long getEndAnimationAt() {
		return endAnimationAt;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double d) {
		speed = d;
		fireEvent(new AnimationSpeedUpdateEvent(speed));
	}

}
