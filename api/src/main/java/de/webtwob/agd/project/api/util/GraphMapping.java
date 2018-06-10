package de.webtwob.agd.project.api.util;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;

public class GraphMapping {

	Map<ElkNode, Pair<Rectangle2D.Double>> elkNodeMap = new HashMap<>();
	Map<ElkEdgeSection, Pair<Line2D.Double>> elkSectionMap = new HashMap<>();
	Map<ElkBendPoint, Pair<Point2D.Double>> elkBendPointMap = new HashMap<>();
	
	Map<Object,Color> highlightMap = new HashMap<>();

	@SuppressWarnings("exports")
	public Pair<Rectangle2D.Double> getMapping(ElkNode node) {
		return elkNodeMap.computeIfAbsent(node, key->new Pair<>(Rectangle2D.Double::new));
	}

	@SuppressWarnings("exports")
	public Pair<Line2D.Double> getMapping(ElkEdgeSection sect) {
		return elkSectionMap.computeIfAbsent(sect,key -> new Pair<>(Line2D.Double::new));
	}

	@SuppressWarnings("exports")
	public Pair<Point2D.Double> getMapping(ElkBendPoint bend) {
		return elkBendPointMap.computeIfAbsent(bend, key->new Pair<>(Double::new));
	}
	
}
