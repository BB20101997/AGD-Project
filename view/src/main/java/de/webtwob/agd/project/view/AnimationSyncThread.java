package de.webtwob.agd.project.view;

import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.List;

import de.webtwob.agd.project.api.AnimationEventHandler;
import de.webtwob.agd.project.api.LoopEnum;
import de.webtwob.agd.project.api.events.AnimationEvent;

public class AnimationSyncThread extends Thread {

	List<AnimationEventHandler> handlerList = new LinkedList<>();
	List<Runnable> synced = new LinkedList<>();
	
	private volatile long frame;
	private double subFrame; // off by this from the next frame
	private double speed = 1; // milliseconds skipped per millisecond time passed
	LoopEnum end = LoopEnum.STOP;
	
	/**
	 * Determines where to start the animation and where to stop if playing backwards
	 * Must always be greater or equal to 0
	 * */
	private long startAnimationAt;
	

	/**
	 * Determines where to end the animation and where to start if playing backwards
	 * Must always be less than the length of the shortest animation
	 * */
	private long endAnimationAt;
	
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
				
				subFrame += (end - start) * speed;
				frame += (long)subFrame;
				subFrame %= 1;
				
				start = System.currentTimeMillis();
				
				synced.parallelStream().forEach(Runnable::run);
				
				if (getFrame() < startAnimationAt || getFrame() > endAnimationAt) {
					this.end.handle((nSpeed)->speed=nSpeed,this::setFrame, endAnimationAt, frame, speed);
					handlerList.parallelStream().forEach(h->EventQueue.invokeLater(()->h.animationEvent(new  AnimationEvent())));
				}
				end = System.currentTimeMillis();
			}
		}
		
	}
	
	public void subscribeToAnimationEvent(AnimationEventHandler aeh) {
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
		end = loopAction;
	}
	
	public void setAnimationEnd(long end) {
		endAnimationAt = Math.min(end, maxEndAnimationAt);
	}

	public void updateMaxEnd(long i) {
		maxEndAnimationAt = Math.min(maxEndAnimationAt, i);
		endAnimationAt = Math.min(endAnimationAt, maxEndAnimationAt);
	}
	
}
