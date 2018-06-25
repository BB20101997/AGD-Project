package de.webtwob.agd.project.api.interfaces;

import java.awt.Graphics2D;

import de.webtwob.agd.project.api.GraphState;

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

}
