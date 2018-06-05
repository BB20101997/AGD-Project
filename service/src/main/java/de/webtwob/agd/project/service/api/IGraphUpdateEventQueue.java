package de.webtwob.agd.project.service.api;

public interface IGraphUpdateEventQueue {
	
	public void subscribeToEventQueue(IGraphUpdateEventHandler igueh);

	public void unsubscribeFromEventQueue(IGraphUpdateEventHandler igueh);
	
	public void publishEvent(GraphUpdateEvent gue);
	
}
