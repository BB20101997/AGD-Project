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

import de.webtwob.agd.project.api.VerbosityEnum;
import de.webtwob.agd.project.api.interfaces.IVerbosity;

public class GraphState {
	
	public GraphState() {}
	
	/**
	 * Copy Constructor
	 * */
	public GraphState(GraphState copy) {
		elkBendPointMap.putAll(copy.elkBendPointMap);
		elkSectionMap.putAll(copy.elkSectionMap);
		elkNodeMap.putAll(copy.elkNodeMap);
		
		highlightMap.putAll(copy.highlightMap);
		verbosity = copy.verbosity;
	}
	
	private Map<ElkNode, Rectangle2D.Double> elkNodeMap = new HashMap<>();
	private Map<ElkEdgeSection, Line2D.Double> elkSectionMap = new HashMap<>();
	private Map<ElkBendPoint, Point2D.Double> elkBendPointMap = new HashMap<>();

	private Map<Object, Color> highlightMap = new HashMap<>();
	
	private IVerbosity verbosity = VerbosityEnum.ONE;

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
		if(obj==null) return;
		if(col==null) {
			highlightMap.remove(obj);
		}
		highlightMap.put(obj, col);
	}

	public IVerbosity getVerbosity() {
		return verbosity;
	}

	public void setVerbosity(IVerbosity verbosity) {
		this.verbosity = verbosity;
	}


}