package de.webtwob.agd.project.api.interfaces;

import java.io.File;

public interface IController {

	/**
	 * @return true if the animation is auto playing forward
	 * */
	boolean isGoForwardAutoplay();

	// TODO set to toggle Button
	/**
	 * @param goForwardAutoplay if the animation should auto play forward
	 * */
	void setGoForwardAutoplay(boolean goForwardAutoplay);

	/**
	 * @return the step size
	 * */
	int getSizeOfSteps();

	/**
	 * @param newStepSize the new Step Size
	 * */
	void setSizeOfSteps(int newStepSize);

	/**
	 * 
	 */
	void autoplay();

	void buttonForward();

	void buttonBackward();

	/**
	 * Changes the current graph returns true if successful (false to give
	 * error message in view)
	 * 
	 * @param file the file to load the graph from 
	 * @return true if the import was successful
	 */
	boolean changeGraph(File file);

	/**
	 * @return //TODO what dose this return 
	 * */
	int getAutoplayTime();

	/**
	 * Sets new Autoplay Time in seconds
	 * 
	 * @param autoplayTime //TODO what does this set
	 */
	void setAutoplayTime(int autoplayTime);

}