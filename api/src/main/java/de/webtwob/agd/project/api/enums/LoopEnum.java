package de.webtwob.agd.project.api.enums;

import de.webtwob.agd.project.api.ControllerModel;

/**
 * The options for what should happen at the end of an Animation
 */
public enum LoopEnum {
	/**
	 * Stop at the end of the animation
	 */
	STOP {
		@Override
		public void handle(ControllerModel syncThread) {
			syncThread.setPaused(true);
			if (syncThread.getFrame() > 0) {
				syncThread.setFrame(syncThread.getEndAnimationAt() - 1);

			} else {
				syncThread.setFrame(0);
			}
		}
	},
	/**
	 * Start over at the end of the animation
	 */
	LOOP {
		@Override
		public void handle(ControllerModel syncThread) {
			if (syncThread.getFrame() > 0) {
				syncThread.setFrame(0);
			} else {
				syncThread.setFrame(syncThread.getEndAnimationAt() - 1);
			}
		}
	},
	/**
	 * Reverse the direction at the end of the animation
	 */
	REVERSE {
		@Override
		public void handle(ControllerModel syncThread) {
			if (syncThread.getFrame() < 0) {
				syncThread.setSpeed(Math.abs(syncThread.getSpeed()));
				syncThread.setFrame(0);
			} else {
				syncThread.setSpeed(-Math.abs(syncThread.getSpeed()));
				syncThread.setFrame(syncThread.getEndAnimationAt()-1);
			}
		}
	};

	/**
	 * @param syncThread the model to operate on
	 * 
	 *                   Performs the action to be performed at the end of the
	 *                   animation
	 */
	public abstract void handle(ControllerModel syncThread);
}
