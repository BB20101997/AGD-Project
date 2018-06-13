package de.webtwob.agd.project.api.util;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;

public class GraphState {
	
	Map<ElkNode, Rectangle2D.Double> elkNodeMap = new HashMap<>();
	Map<ElkEdgeSection, Line2D.Double> elkSectionMap = new HashMap<>();
	Map<ElkBendPoint, Point2D.Double> elkBendPointMap = new HashMap<>();

	Map<Object, Color> highlightMap = new HashMap<>();

	@SuppressWarnings("exports")
	public Rectangle2D.Double getMapping(ElkNode node) {
		return elkNodeMap.computeIfAbsent(node, key -> new Rectangle2D.Double());
	}

	@SuppressWarnings("exports")
	public Line2D.Double getMapping(ElkEdgeSection sect) {
		return elkSectionMap.computeIfAbsent(sect, key -> new Line2D.Double());
	}

	@SuppressWarnings("exports")
	public Point2D.Double getMapping(ElkBendPoint bend) {
		return elkBendPointMap.computeIfAbsent(bend, key -> new Point2D.Double());
	}

	public Color getHighlight(Object obj) {
		return highlightMap.get(obj);
	}

	public void setHighlight(Object obj, Color col) {
		highlightMap.put(obj, col);
	}


}
