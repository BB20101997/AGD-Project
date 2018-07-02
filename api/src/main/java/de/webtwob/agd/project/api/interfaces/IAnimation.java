package de.webtwob.agd.project.api.interfaces;

import java.awt.Graphics2D;
import java.util.OptionalLong;

import de.webtwob.agd.project.api.GraphState;

/**
 * An Interface for Animations
 */
public interface IAnimation {

	/**
	 * @param frame
	 *            the Frame to draw
	 * @param graphic
	 *            the Graphics2D object to draw to
	 */
	void generateFrame(long frame, Graphics2D graphic);

	/**
	 * @param frame the referenced frame
	 * 
	 * @return the @see GraphState that is mainly responsible for the Frame frame
	 * 
	 * Used by @see PseudocodeView to update the line highlight
	 * */
	GraphState getGraphStatesForFrame(long frame);

	/**
	 * @return the length of the animation in frames
	 */
	long getLength();

	/**
	 * @return the width of the animation
	 * */
	double getWidth();

	/**
	 * @return the height of the animation
	 * */
	double getHeight();
	
	/**
	 * @param frame the frame to start at7
	 * @param forward if true seach forward from frame, else seach backwards
	 * @param verbosity the maximum verbosity
	 * @return the next frame to stop at strictly after frame, of  at most verbosity 
	 * */
	OptionalLong nextStep(long frame,boolean forward ,IVerbosity verbosity);

}
