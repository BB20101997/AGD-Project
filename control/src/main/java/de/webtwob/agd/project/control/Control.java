package de.webtwob.agd.project.control;

import java.util.List;

import org.eclipse.elk.graph.ElkNode;
import de.webtwob.agd.project.service.api.GraphUpdateEvent;
//import de.webtwob.agd.project.view;

public class Control {
	
	private int currentStep;
	private List<ElkNode> steps;

	public Control() {
		//TODO Create model
	}
	
	public void stepForward() {
		GraphUpdateEvent event = new GraphUpdateEvent(steps.get(++currentStep));
		//View.graphUpdate steps.get(++currentStep);
	}
	
	
}
