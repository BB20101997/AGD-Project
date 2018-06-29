package de.webtwob.agd.project.api;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import de.webtwob.agd.project.api.enums.LoopEnum;
import de.webtwob.agd.project.api.enums.VerbosityEnum;
import de.webtwob.agd.project.api.events.AnimationSpeedUpdateEvent;
import de.webtwob.agd.project.api.events.AnimationUpdateEvent;
import de.webtwob.agd.project.api.events.IAnimationEvent;
import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.api.interfaces.IAnimationEventHandler;

public class ControllerModel {

	List<IAnimationEventHandler> handlerList = new LinkedList<>();
	List<IAnimation> animations = new LinkedList<>();

	private volatile Thread syncThread;

	// should the thread terminate
	private volatile boolean stop = false;

	private volatile boolean step = false;
	private volatile long nextStepStop;

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
	private volatile LoopEnum endAction = LoopEnum.STOP;

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

	/**
	 * Are we in debug mode?
	 */
	private boolean debug;

	/**
	 * Start the animation thread
	 */
	public void start() {
		if (syncThread == null) {
			synchronized (this) {
				if (syncThread == null) {
					stop = false;
					syncThread = new Thread(this::run);
					syncThread.setDaemon(true);
					syncThread.setName("Animation Update Thread");
					syncThread.start();
				}
			}
		}
	}

	/**
	 * Stop the animation Thread
	 */
	public void stop() {
		if (syncThread != null) {
			synchronized (this) {
				if (syncThread != null) {
					stop = true;
					notifyAll();
					while (syncThread.isAlive())
						try {
							syncThread.join();
						} catch (InterruptedException ignore) {
							// should never happen
							Thread.currentThread().interrupt();
						}
					syncThread = null;
				}
			}
		}
	}

	@SuppressWarnings("squid:S3776") // this method is a bit too complex
	private void run() {

		long start = System.currentTimeMillis();
		long end = start;

		while (!stop) {
			if (speed != 0 && !paused) {
				// update current frame
				subFrame += (end - start) * speed;
				frame += (long) subFrame;
				subFrame %= 1; // yes, this actually does something since subFrame is a double

				// set start to current time
				start = System.currentTimeMillis();

				if (step && (speed > 0 ? frame >= nextStepStop : frame <= nextStepStop)) {
					frame = nextStepStop;
					paused = true;
				}

				// have we reached the end of the Animation
				if (getFrame() < startAnimationAt || getFrame() > endAnimationAt) {
					endAction.handle(this);
				}

				updateFrame(frame);

				end = System.currentTimeMillis();
			} else {
				start = end;
				subFrame = 0;
				synchronized (this) {
					while (speed == 0 || paused) {
						try {
							wait();
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}

					}
				}

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
				EventQueue.invokeAndWait(() -> fireEventOnEventQueue(syncedCopy, event));
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
	 * This method handles dispatching Events, it assumes to be called on the
	 * DispatchThread
	 */
	private void fireEventOnEventQueue(List<IAnimationEventHandler> handlers, IAnimationEvent event) {
		if (!EventQueue.isDispatchThread()) {
			throw new IllegalThreadStateException("This methode might only be called on the DispatchThread!");
		}
		handlers.stream().forEach(h -> {
			try {
				h.animationEvent(event);
			} catch (Exception ignore) {
				// we want to keep running even when the event handler fails
			}
		});
	}

	/**
	 * @param aeh the EventHandler to add the the subscriber list
	 */
	public void subscribeToAnimationEvent(IAnimationEventHandler aeh) {
		if (aeh == null) {
			return;
		}
		synchronized (handlerList) {
			handlerList.add(aeh);
		}
	}

	/**
	 * @param eventHandler the EventHandler to remove from the subscriber list
	 */
	public void unsubscribeFromAnimationEvent(IAnimationEventHandler eventHandler) {
		if (eventHandler == null) {
			return;
		}
		synchronized (handlerList) {
			handlerList.remove(eventHandler);
		}
	}

	/**
	 * @param frame the frame this should be set to
	 */
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

	/**
	 * @return the current frame
	 */
	public long getFrame() {
		return frame;
	}

	/**
	 * @param loopAction the Action to perform at the end of the animation
	 */
	public void setLoopAction(LoopEnum loopAction) {
		endAction = loopAction;
	}

	/**
	 * @return the current Action to be performed at the end of the animation
	 */
	public LoopEnum getLoopAction() {
		return endAction;
	}

	/**
	 * @param end at the animation at this frame
	 * 
	 *            Use this to end an animation early
	 */
	public void setAnimationEnd(long end) {
		endAnimationAt = Math.min(end, maxEndAnimationAt);
	}

	/**
	 * @param animation the animation to add the the registered ones
	 */
	public void addAnimation(IAnimation animation) {
		if (animation == null) {
			return;
		}
		animations.add(animation);
		updateMaxEnd();
	}

	/**
	 * @param animation the animation to remove from the registered ones
	 */
	public void removeAnimation(IAnimation animation) {
		if (animation == null) {
			return;
		}
		animations.remove(animation);
		updateMaxEnd();
	}

	/**
	 * remove all registered Animations
	 */
	public void removeAllAnimations() {
		animations.clear();
		updateMaxEnd();
	}

	/**
	 * recalculate the index of the last frame of the shortest registered animation
	 */
	public void updateMaxEnd() {
		maxEndAnimationAt = animations.stream().mapToLong(IAnimation::getLength).min().orElse(Long.MAX_VALUE);
		endAnimationAt = Math.min(endAnimationAt, maxEndAnimationAt);
	}

	/**
	 * @return true if the animation is paused
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * @param paused should this set the animation to be paused or resume
	 */
	public void setPaused(boolean paused) {
		if (this.paused && !paused) {
			this.paused = paused;
			synchronized (this) {
				notifyAll();
			}
		} else {
			this.paused = paused;
		}
	}

	/**
	 * @return the index the animation will currently end at
	 */
	public long getEndAnimationAt() {
		return endAnimationAt;
	}

	/**
	 * @return the animation playback speed not accounting for paused
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @param d the value to set the animation playback speed to
	 */
	public void setSpeed(double d) {
		if (speed == 0 && d != 0) {
			speed = d;
			synchronized (this) {
				notifyAll();
			}
		} else {
			speed = d;
		}
		fireEvent(new AnimationSpeedUpdateEvent(speed));
	}

	/**
	 * @param debug what the debug mode flag should be set to
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * @return if debug mode is set
	 */
	public boolean getDebug() {
		return debug;
	}

	public void playContinuosly() {
		step = false;
	}

	public void step(boolean forward) {
		setSpeed(Math.abs(speed) * (forward ? 1 : -1));
		step = true;
		nextStepStop = animations.get(0).nextStep(frame, forward, VerbosityEnum.DEPTH_2)
				.orElse(forward ? animations.get(0).getLength() - 1 : 0);
		paused = false;
		synchronized (this) {
			notifyAll();
		}
	}

}
