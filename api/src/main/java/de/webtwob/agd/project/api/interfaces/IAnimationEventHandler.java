package de.webtwob.agd.project.api.interfaces;

import de.webtwob.agd.project.api.events.IAnimationEvent;

/**
 * An Interface for EventHandler handling IAnimation Events
 */
@FunctionalInterface
public interface IAnimationEventHandler {

	/**
	 * @param event the animationEvent to handle
	 * */
	void animationEvent(IAnimationEvent event);

}
