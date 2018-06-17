package de.webtwob.agd.project.view.util;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ViewUtil {

	private ViewUtil() {
	}

	/**
	 * @param oldPos
	 *            the last state before the current time
	 * @param newPos
	 *            the next state after the current time
	 * @param timePos
	 *            the current time
	 * @param timeLength
	 *            the distance between oldPos and newPos
	 *
	 *            timeLength &gt;= 2
	 *
	 * @return the current value
	 */
	public static double getCurrent(double oldPos, double newPos, double timePos, double timeLength) {
		return oldPos + (newPos - oldPos) * timePos / (timeLength - 1);
	}

	/**
	 * @param oldPos
	 *            the last state before the current time
	 * @param newPos
	 *            the next state after the current time
	 * @param timePos
	 *            the current time
	 * @param timeLength
	 *            the distance between oldPos and newPos
	 *
	 *            timeLength &gt;= 2
	 *
	 * @return the current value
	 */
	public static Point2D getCurrent(Point2D oldPos, Point2D newPos, double timePos, double timeLength) {
		return new Point2D.Double(getCurrent(oldPos.getX(), newPos.getX(), timePos, timeLength),
				getCurrent(oldPos.getY(), newPos.getY(), timePos, timeLength));
	}

	/**
	 * @param oldPos
	 *            the last state before the current time
	 * @param newPos
	 *            the next state after the current time
	 * @param timePos
	 *            the current time
	 * @param timeLength
	 *            the distance between oldPos and newPos
	 *
	 *            timeLength &gt;= 2
	 *
	 * @return the current value
	 */
	public static Rectangle2D.Double getCurrent(Rectangle2D.Double oldPos, Rectangle2D.Double newPos, double timePos,
			double timeLength) {
		return new Rectangle2D.Double(getCurrent(oldPos.getX(), newPos.getX(), timePos, timeLength),
				getCurrent(oldPos.getY(), newPos.getY(), timePos, timeLength),
				getCurrent(oldPos.getWidth(), newPos.getWidth(), timePos, timeLength),
				getCurrent(oldPos.getHeight(), newPos.getHeight(), timePos, timeLength));
	}

	/**
	 *
	 * @param start
	 *            the last color before the current time
	 * @param end
	 *            the next color after the current time
	 * @param fallback
	 *            the color to use if one of start or end is null
	 *
	 * @param timePos
	 *            the current time
	 * @param totalTime
	 *            the distance between oldPos and newPos
	 *
	 *            timeLength &gt;= 2
	 *
	 * @return the current value
	 */
	public static Color getCurrent(Color start, Color end, Color fallback, double timePos, double totalTime) {

		if (start == end) {
			return start;
		}

		// assumes WHITE Background Color
		if (start == null) {
			start = fallback;
		}
		if (end == null) {
			end = fallback;
		}

		var startAlpha = start.getAlpha() * start.getAlpha();
		var startBlue = start.getBlue() * start.getBlue();
		var startGreen = start.getGreen() * start.getGreen();
		var startRed = start.getRed() * start.getRed();

		var endAlpha = end.getAlpha() * end.getAlpha();
		var endBlue = end.getBlue() * end.getBlue();
		var endGreen = end.getGreen() * end.getGreen();
		var endRed = end.getRed() * end.getRed();

		var resAlpha = (int) Math.sqrt(getCurrent(startAlpha, endAlpha, timePos, totalTime));
		var resBlue = (int) Math.sqrt(getCurrent(startBlue, endBlue, timePos, totalTime));
		var resGreen = (int) Math.sqrt(getCurrent(startGreen, endGreen, timePos, totalTime));
		var resRed = (int) Math.sqrt(getCurrent(startRed, endRed, timePos, totalTime));

		return new Color(resRed, resGreen, resBlue, resAlpha);
	}

}
