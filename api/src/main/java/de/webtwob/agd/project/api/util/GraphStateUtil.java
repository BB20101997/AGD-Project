package de.webtwob.agd.project.api.util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.GraphState;

/**
 * Created by BB20101997 on 17. Jun. 2018.
 */
public class GraphStateUtil {

	private GraphStateUtil() {
	}

	/**
	 * @param start
	 *            the graph to start at
	 * @param end
	 *            the graph to end at
	 * @return a mapping of the graph start starting at the values of start and
	 *         stopping at the values of end
	 */
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
	 * @param graph
	 *            the graph to save
	 * @param state
	 *            the object to save to
	 *
	 *            Save the current state of the Graph in the start part of the
	 *            mapping
	 */
	public static void saveState(ElkNode graph, GraphState state) {
		var nodeMapping = state.getMapping(graph);

		nodeMapping.setFrame(new Rectangle2D.Double(graph.getX(), graph.getY(), graph.getWidth(), graph.getHeight()));

		for (var child : graph.getChildren()) {
			saveState(child, state);
		}

		for (var edge : graph.getContainedEdges()) {
			saveState(edge, state);
		}
	}

	/**
	 *
	 * @param edge
	 *            the edge to save
	 * @param state
	 *            the object to save to
	 */
	public static void saveState(ElkEdge edge, GraphState state) {

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
