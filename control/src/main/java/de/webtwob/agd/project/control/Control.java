package de.webtwob.agd.project.control;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.file.GraphImport;
import de.webtwob.agd.project.service.api.GraphUpdateEvent;
import de.webtwob.agd.project.view.*;

public class Control {

	private int currentStep; // Initalize with any new loaded graph
	private int sizeOfSteps = 1;
	private int autoplayTime = 1;
	private boolean goForwardAutoplay;

	private List<ElkNode> steps;// Initalize with any new loaded graph

	public Control() {
		// TODO Create model
		// TODO ADD view
	}

	public boolean isGoForwardAutoplay() {
		return goForwardAutoplay;
	}

	// TODO set to toggle Button
	public void setGoForwardAutoplay(boolean goForwardAutoplay) {
		this.goForwardAutoplay = goForwardAutoplay;
	}

	public int getSizeOfSteps() {
		return sizeOfSteps;
	}

	public void setSizeOfSteps(int newStepSize) {
		this.sizeOfSteps = newStepSize;
	}

	/**
	 * 
	 */
	public void autoplay() {
		// TODO Add listener for Button event and replace it with this
		// TODO wait for Toggle removed
		while ((goForwardAutoplay && currentStep != steps.size() - 1)
				|| (!goForwardAutoplay && goForwardAutoplay && currentStep != 0)) {
            if (goForwardAutoplay) {
            	buttonForward();
            } else {
            	buttonBackward();
            }
            try {
				Thread.sleep(autoplayTime*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}
	}

	public void buttonForward() {

		GraphUpdateEvent event = new GraphUpdateEvent(steps.get(addStep()));
		// TODO View.graphUpdate steps.get(event);
	}

	private int addStep() {
		if (sizeOfSteps + currentStep >= steps.size()) {
			currentStep = steps.size() - 1;
			return currentStep;
		}
		currentStep += sizeOfSteps;
		return currentStep;
	}

	public void buttonBackward() {
		GraphUpdateEvent event = new GraphUpdateEvent(steps.get(subStep()));
		// TODO View.graphUpdate steps.get(event);
	}

	private int subStep() {
		if (currentStep - sizeOfSteps < 0) {
			currentStep = 0;
			return currentStep;
		}
		currentStep -= sizeOfSteps;
		return currentStep;
	}

	/**
	 * Changes the current graph returns true if successful (false to give
	 * errormessage in view)
	 * 
	 * @param file
	 * @return
	 */
	public boolean changeGraph(File file) {
		Optional<ElkNode> graph = GraphImport.importGraphFromFile(file);
		if (graph.isPresent()) {
			// TODO steps = Model.getSteps(graph.get())
			return true; //
		}
		return false;
	}

	public int getAutoplayTime() {
		return autoplayTime;
	}

	/**
	 * Sets new Autoplay Time in seconds
	 * 
	 * @param autoplayTime
	 */
	public void setAutoplayTime(int autoplayTime) {
		this.autoplayTime = autoplayTime;
	}

}
