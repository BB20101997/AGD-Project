package de.webtwob.agd.project.api.util;

import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ViewUtil {

	private ViewUtil() {
	}

	/**
	 * timeLength >= 2
	 */
	public static double getCurrent(double oldPos, double newPos, double timePos, double timeLength) {
		return oldPos + (newPos - oldPos) * timePos / (timeLength - 1);
	}

	/**
	 * timeLength >= 2
	 */
	public static Point2D getCurrent(Point2D oldPos, Point2D newPos, double timePos, double timeLength) {
		return new Point2D.Double(getCurrent(oldPos.getX(), newPos.getX(), timePos, timeLength),
				getCurrent(oldPos.getY(), newPos.getY(), timePos, timeLength));
	}

	/**
	 * timeLength >= 2
	 */
	public static Rectangle2D.Double getCurrent(Rectangle2D.Double oldPos, Rectangle2D.Double newPos, double timePos,
			double timeLength) {
		return new Rectangle2D.Double(getCurrent(oldPos.getX(), newPos.getX(), timePos, timeLength),
				getCurrent(oldPos.getY(), newPos.getY(), timePos, timeLength),
				getCurrent(oldPos.getWidth(), newPos.getWidth(), timePos, timeLength),
				getCurrent(oldPos.getHeight(), newPos.getHeight(), timePos, timeLength));
	}

	public static Color getCurrent(Color start, Color end, double timePos, double totalTime) {
		
		if(start == null) {
			return end;
		}
		if(end == null) {
			return start;
		}

		var startAlpha = start.getAlpha() * start.getAlpha();
		var startBlue = start.getBlue() * start.getBlue();
		var startGreen = start.getGreen() * start.getGreen();
		var startRed = start.getRed() * start.getRed();

		var endAlpha = end.getAlpha() * end.getAlpha();
		var endBlue  = end.getBlue()  * end.getBlue();
		var endGreen = end.getGreen() * end.getGreen();
		var endRed   = end.getRed()   * end.getRed();
		
		var resAlpha = (int)Math.sqrt(getCurrent(startAlpha,endAlpha,timePos,totalTime));
		var resBlue  = (int)Math.sqrt(getCurrent(startBlue,endBlue  ,timePos,totalTime));
		var resGreen = (int)Math.sqrt(getCurrent(startGreen,endGreen,timePos,totalTime));
		var resRed   = (int)Math.sqrt(getCurrent(startRed,endRed    ,timePos,totalTime));	
		
		return new Color(resRed,resGreen,resBlue,resAlpha);
	}

	@SuppressWarnings("exports")
	public static Pair<GraphState> createMapping(ElkNode start, ElkNode end) {
		var mapping = new Pair<>(GraphState::new);

		insertNodeMapping(start, end, mapping);

		return mapping;
	}

	private static void insertNodeMapping(ElkNode start, ElkNode end, Pair<GraphState> mapping) {
		var startMapping = mapping.getStart().getMapping(start);
		var endMapping = mapping.getEnd().getMapping(start);

		startMapping.setFrame(new Rectangle2D.Double(start.getX(), start.getY(), start.getWidth(), start.getHeight()));
		endMapping.setFrame(new Rectangle2D.Double(end.getX(), end.getY(), end.getWidth(), end.getHeight()));

		for (ElkNode startChild : start.getChildren()) {
			end.getChildren().stream().filter(endChild -> endChild.getIdentifier().equals(startChild.getIdentifier()))
					.findFirst().ifPresent(endChild -> insertNodeMapping(startChild, endChild, mapping));
		}

		for (ElkEdge startEdge : start.getContainedEdges()) {
			end.getContainedEdges().stream()
					.filter(endEdge -> endEdge.getIdentifier().equals(startEdge.getIdentifier())).findFirst()
					.ifPresent(endEdge -> insertEdgeMapping(startEdge, endEdge, mapping));
		}

	}

	private static void insertEdgeMapping(ElkEdge start, ElkEdge end, Pair<GraphState> mapping) {
		int count = Math.min(start.getSections().size(), end.getSections().size());

		for (int i = 0; i < count; i++) {
			insertSectionMapping(start.getSections().get(i), end.getSections().get(i), mapping);
		}
	}

	private static void insertSectionMapping(ElkEdgeSection start, ElkEdgeSection end, Pair<GraphState> mapping) {

		var startMapping = mapping.getStart().getMapping(start);
		var endMapping = mapping.getEnd().getMapping(start);

		startMapping.setLine(new Line2D.Double(start.getStartX(), start.getStartY(), start.getEndX(), start.getEndY()));
		endMapping.setLine(new Line2D.Double(end.getStartX(), end.getStartY(), end.getEndX(), end.getEndY()));

		int count = Math.min(start.getBendPoints().size(), end.getBendPoints().size());
		for (int i = 0; i < count; i++) {
			insertBendPointMapping(start.getBendPoints().get(i), end.getBendPoints().get(i), mapping);
		}

	}

	private static void insertBendPointMapping(ElkBendPoint start, ElkBendPoint end, Pair<GraphState> mapping) {
		var startMapping = mapping.getStart().getMapping(start);
		var endMapping = mapping.getEnd().getMapping(start);

		startMapping.setLocation(new Point2D.Double(start.getX(), start.getY()));
		endMapping.setLocation(new Point2D.Double(end.getX(), end.getY()));

	}

	/**
	 * Save the current state of the Graph in the start part of the mapping
	 */
	public static void saveState(@SuppressWarnings("exports") ElkNode graph, GraphState state) {
		var nodeMapping = state.getMapping(graph);

		nodeMapping.setFrame(new Rectangle2D.Double(graph.getX(), graph.getY(), graph.getWidth(), graph.getHeight()));

		for (var child : graph.getChildren()) {
			saveState(child, state);
		}

		for (var edge : graph.getContainedEdges()) {
			saveState(edge, state);
		}
	}

	private static void saveState(ElkEdge edge, GraphState state) {

		for (var sect : edge.getSections()) {
			saveState(sect, state);
		}

	}

	private static void saveState(ElkEdgeSection sect, GraphState state) {

		var mapping = state.getMapping(sect);
		mapping.setLine(sect.getStartX(), sect.getStartY(), sect.getEndX(), sect.getEndY());

		for (var bend : sect.getBendPoints()) {
			saveState(bend, state);
		}

	}

	private static void saveState(ElkBendPoint bend, GraphState state) {
		var mapping = state.getMapping(bend);

		mapping.setLocation(bend.getX(), bend.getY());

	}

}
