package de.webtwob.agd.project.api.interfaces;

import de.webtwob.agd.project.api.events.IAnimationEvent;

@FunctionalInterface
public interface IAnimationEventHandler {

	void animationEvent(IAnimationEvent event);

}
