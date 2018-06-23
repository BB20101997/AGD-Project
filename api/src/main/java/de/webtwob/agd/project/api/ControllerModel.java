package de.webtwob.agd.project.api;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import de.webtwob.agd.project.api.enums.LoopEnum;
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

	public void stop() {
		if (syncThread != null) {
			synchronized (this) {
				if (syncThread != null) {
					stop = true;
					syncThread.interrupt();
					while (syncThread.isAlive())
						try {
							syncThread.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					syncThread = null;
				}
			}
		}
	}

	private void run() {

		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();

		while (!stop) {
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
				Thread.interrupted(); // clear interrupt flag

				end = System.currentTimeMillis();
			} else {
				start = end;
				subFrame = 0;
				synchronized (syncThread) {
					while (speed == 0 || paused) {
						try {
							syncThread.wait();
						} catch (InterruptedException e) {
							if (stop) {
								return;
							}
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
		if (this.paused && !paused) {
			this.paused = paused;
			synchronized (syncThread) {
				syncThread.notify();
			}
		} else {
			this.paused = paused;
		}
	}

	public long getEndAnimationAt() {
		return endAnimationAt;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double d) {
		if(speed==0&&d!=0) {
			speed = d;
			synchronized (syncThread) {
				syncThread.notify();
			}	
		}else {
			speed = d;
		}
		fireEvent(new AnimationSpeedUpdateEvent(speed));
	}

}
