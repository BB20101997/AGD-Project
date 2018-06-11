package de.webtwob.agd.project.api.interfaces;

import de.webtwob.agd.project.api.events.GraphUpdateEvent;

public interface IGraphUpdateEventHandler {
	
	void graphUpdate(GraphUpdateEvent event);

}
