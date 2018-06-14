package de.webtwob.agd.project.api;

public enum LoopEnum {
	STOP {
		@Override
		public void handle(AnimationSyncThread syncThread) {
			syncThread.setPaused(true);
			if (syncThread.getFrame() > 0) {
				syncThread.setFrame(syncThread.getEndAnimationAt()-1);

			} else {
				syncThread.setFrame(0);
			}
		}
	},
	LOOP {
		@Override
		public void handle(AnimationSyncThread syncThread) {
			if (syncThread.getFrame() > 0) {
				syncThread.setFrame(0);
			} else {
				syncThread.setFrame(syncThread.getEndAnimationAt()-1);
			}
		}
	},
	REVERSE {
		@Override
		public void handle(AnimationSyncThread syncThread) {
			syncThread.setSpeed(syncThread.getSpeed()* -1);
		}
	};

	public abstract void handle(AnimationSyncThread syncThread);
}
