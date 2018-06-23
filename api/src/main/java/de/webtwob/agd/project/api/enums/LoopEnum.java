package de.webtwob.agd.project.api.enums;

import de.webtwob.agd.project.api.ControllerModel;

public enum LoopEnum {
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
	REVERSE {
		@Override
		public void handle(ControllerModel syncThread) {
			syncThread.setSpeed(syncThread.getSpeed() * -1);
		}
	};

	public abstract void handle(ControllerModel syncThread);
}
