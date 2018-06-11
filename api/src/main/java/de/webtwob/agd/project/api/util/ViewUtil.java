package de.webtwob.agd.project.api.util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;

public class ViewUtil {

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

	@SuppressWarnings("exports")
	public static GraphMapping createMapping(ElkNode start, ElkNode end) {
		GraphMapping mapping = new GraphMapping();

		insertNodeMapping(start, end, mapping);

		return mapping;
	}

	private static void insertNodeMapping(ElkNode start, ElkNode end, GraphMapping mapping) {
		var nodeMapping = mapping.getMapping(start);
		nodeMapping.start = new Rectangle2D.Double(start.getX(), start.getY(), start.getWidth(), start.getHeight());
		nodeMapping.end = new Rectangle2D.Double(end.getX(), end.getY(), end.getWidth(), end.getHeight());

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

	private static void insertEdgeMapping(ElkEdge start, ElkEdge end, GraphMapping mapping) {
		int count = Math.min(start.getSections().size(), end.getSections().size());

		for (int i = 0; i < count; i++) {
			insertSectionMapping(start.getSections().get(i), end.getSections().get(i), mapping);
		}
	}

	private static void insertSectionMapping(ElkEdgeSection start, ElkEdgeSection end, GraphMapping mapping) {

		var sectMapping = mapping.getMapping(start);
		sectMapping.start = new Line2D.Double(start.getStartX(), start.getStartY(), start.getEndX(), start.getEndY());
		sectMapping.end = new Line2D.Double(end.getStartX(), end.getStartY(), end.getEndX(), end.getEndY());

		int count = Math.min(start.getBendPoints().size(), end.getBendPoints().size());
		for (int i = 0; i < count; i++) {
			insertBendPointMapping(start.getBendPoints().get(i), end.getBendPoints().get(i), mapping);
		}

	}

	private static void insertBendPointMapping(ElkBendPoint start, ElkBendPoint end, GraphMapping mapping) {
		var bendMapping = mapping.getMapping(start);

		bendMapping.start = new Point2D.Double(start.getX(), start.getY());
		bendMapping.end = new Point2D.Double(end.getX(), end.getY());

	}

	/**
	 * Save the current state of the Graph in the start part of the mapping
	 */
	public static void saveStartMapping(@SuppressWarnings("exports") ElkNode graph, GraphMapping mapping) {
		var nodeMapping = mapping.getMapping(graph);

		nodeMapping.start = new Rectangle2D.Double(graph.getX(), graph.getY(), graph.getWidth(), graph.getHeight());

		for (var child : graph.getChildren()) {
			saveStartMapping(child, mapping);
		}

		for (var edge : graph.getContainedEdges()) {
			saveStartMapping(edge, mapping);
		}
	}

	/**
	 * Save the current state of the Graph in the end part of the mapping
	 */
	public static void saveEndMapping(@SuppressWarnings("exports") ElkNode graph, GraphMapping mapping) {
		var nodeMapping = mapping.getMapping(graph);

		nodeMapping.end = new Rectangle2D.Double(graph.getX(), graph.getY(), graph.getWidth(), graph.getHeight());

		for (var child : graph.getChildren()) {
			saveEndMapping(child, mapping);
		}

		for (var edge : graph.getContainedEdges()) {
			saveEndMapping(edge, mapping);
		}
	}

	private static void saveStartMapping(ElkEdge edge, GraphMapping mapping) {

		for (var sect : edge.getSections()) {
			saveStartMapping(sect, mapping);
		}

	}

	private static void saveStartMapping(ElkEdgeSection sect, GraphMapping mapping) {
		var sectMapping = mapping.getMapping(sect);

		sectMapping.start = new Line2D.Double(sect.getStartX(), sect.getStartY(), sect.getEndX(), sect.getEndY());

		for (var bend : sect.getBendPoints()) {
			saveStartMapping(bend, mapping);
		}

	}

	private static void saveStartMapping(ElkBendPoint bend, GraphMapping mapping) {
		var bendMapping = mapping.getMapping(bend);

		bendMapping.start = new Point2D.Double(bend.getX(), bend.getY());
	}

	private static void saveEndMapping(ElkEdge edge, GraphMapping mapping) {

		for (var sect : edge.getSections()) {
			saveEndMapping(sect, mapping);
		}

	}

	private static void saveEndMapping(ElkEdgeSection sect, GraphMapping mapping) {
		var sectMapping = mapping.getMapping(sect);

		sectMapping.end = new Line2D.Double(sect.getStartX(), sect.getStartY(), sect.getEndX(), sect.getEndY());

		for (var bend : sect.getBendPoints()) {
			saveEndMapping(bend, mapping);
		}

	}

	private static void saveEndMapping(ElkBendPoint bend, GraphMapping mapping) {
		var bendMapping = mapping.getMapping(bend);

		bendMapping.end = new Point2D.Double(bend.getX(), bend.getY());
	}

}
