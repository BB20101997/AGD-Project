package de.webtwob.agd.project.api.interfaces;

import java.io.File;

public interface IController {

	boolean isGoForwardAutoplay();

	// TODO set to toggle Button
	void setGoForwardAutoplay(boolean goForwardAutoplay);

	int getSizeOfSteps();

	void setSizeOfSteps(int newStepSize);

	/**
	 * 
	 */
	void autoplay();

	void buttonForward();

	void buttonBackward();

	/**
	 * Changes the current graph returns true if successful (false to give
	 * errormessage in view)
	 * 
	 * @param file
	 * @return
	 */
	boolean changeGraph(File file);

	int getAutoplayTime();

	/**
	 * Sets new Autoplay Time in seconds
	 * 
	 * @param autoplayTime
	 */
	void setAutoplayTime(int autoplayTime);

}