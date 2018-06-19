package de.webtwob.agd.project.api.interfaces;

import java.awt.Graphics2D;

import de.webtwob.agd.project.api.GraphState;
import de.webtwob.agd.project.api.util.Pair;

public interface IAnimation {

	/**
	 * @param frame
	 *            the Frame to draw
	 * @param graphic
	 *            the Graphics2D object to draw to
	 */
	void generateFrame(long frame, Graphics2D graphic);

	Pair<GraphState> getGraphStatesForFrame(long frame);

	/**
	 * @return the length of the animation in frames
	 */
	long getLength();

	double getWidth();

	double getHeight();

}
