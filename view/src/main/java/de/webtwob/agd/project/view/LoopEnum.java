package de.webtwob.agd.project.view;

import java.util.function.DoubleConsumer;
import java.util.function.LongConsumer;

public enum LoopEnum {
	STOP {
		@Override
		public void handle(DoubleConsumer setSpeed, LongConsumer setFrame, long animationLength,long frame,double speed) {
			setSpeed.accept(0);
			if(frame>0) {
				setFrame.accept(animationLength-1);
			}else {
				setFrame.accept(0);
			}
		}
	},
	LOOP {
		@Override
		public void handle(DoubleConsumer setSpeed, LongConsumer setFrame, long animationLength, long frame,double speed) {
			if(frame>0) {
				setFrame.accept(0);
			}else {
				setFrame.accept(animationLength-1);
			}
		}
	},
	REVERSE {
		@Override
		public void handle(DoubleConsumer setSpeed, LongConsumer setFrame, long animationLength, long frame,double speed) {
			setSpeed.accept(speed*-1);
			
		}
	};

	public abstract void handle(DoubleConsumer setSpeed, LongConsumer setFrame, long animationLength, long frame,double speed);
}
