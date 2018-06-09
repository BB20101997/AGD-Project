package de.webtwob.agd.project.control;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.IController;
import de.webtwob.agd.project.api.events.GraphUpdateEvent;
import de.webtwob.agd.project.file.JSONGraphLoader;

public class Control implements IController {

	private int currentStep; // Initalize with any new loaded graph
	private int sizeOfSteps = 1;
	private int autoplayTime = 1;
	private boolean goForwardAutoplay;

	private List<ElkNode> steps;// Initalize with any new loaded graph

	public Control() {
		// TODO Create model
		// TODO ADD view
	}

	/* (non-Javadoc)
	 * @see de.webtwob.agd.project.control.IController#isGoForwardAutoplay()
	 */
	@Override
	public boolean isGoForwardAutoplay() {
		return goForwardAutoplay;
	}

	// TODO set to toggle Button
	/* (non-Javadoc)
	 * @see de.webtwob.agd.project.control.IController#setGoForwardAutoplay(boolean)
	 */
	@Override
	public void setGoForwardAutoplay(boolean goForwardAutoplay) {
		this.goForwardAutoplay = goForwardAutoplay;
	}

	/* (non-Javadoc)
	 * @see de.webtwob.agd.project.control.IController#getSizeOfSteps()
	 */
	@Override
	public int getSizeOfSteps() {
		return sizeOfSteps;
	}

	/* (non-Javadoc)
	 * @see de.webtwob.agd.project.control.IController#setSizeOfSteps(int)
	 */
	@Override
	public void setSizeOfSteps(int newStepSize) {
		this.sizeOfSteps = newStepSize;
	}

	/* (non-Javadoc)
	 * @see de.webtwob.agd.project.control.IController#autoplay()
	 */
	@Override
	public void autoplay() {
		// TODO Add listener for Button event and replace it with this
		// TODO wait for Toggle removed
		while ((goForwardAutoplay && currentStep <= steps.size() - 1|| !goForwardAutoplay && currentStep > 0)
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

	/* (non-Javadoc)
	 * @see de.webtwob.agd.project.control.IController#buttonForward()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see de.webtwob.agd.project.control.IController#buttonBackward()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see de.webtwob.agd.project.control.IController#changeGraph(java.io.File)
	 */
	@Override
	public boolean changeGraph(File file) {
		Optional<ElkNode> graph = JSONGraphLoader.importGraphFromFile(file);
		if (graph.isPresent()) {
			// TODO steps = Model.getSteps(graph.get())
			currentStep=0;
			return true; //
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see de.webtwob.agd.project.control.IController#getAutoplayTime()
	 */
	@Override
	public int getAutoplayTime() {
		return autoplayTime;
	}

	/* (non-Javadoc)
	 * @see de.webtwob.agd.project.control.IController#setAutoplayTime(int)
	 */
	@Override
	public void setAutoplayTime(int autoplayTime) {
		this.autoplayTime = autoplayTime;
	}

}
