package de.webtwob.agd.project.api.events;

import java.util.EventObject;

import org.eclipse.elk.graph.ElkNode;

public class GraphUpdateEvent extends EventObject {

	/**
	 * generated serialVersionUID
	 */
	private static final long serialVersionUID = -3492269923712207423L;

	/**
	 * @param The
	 *            object that was the source of the event
	 */
	@SuppressWarnings("exports")
	public GraphUpdateEvent(ElkNode source) {
		super(source);
	}

}
