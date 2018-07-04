package de.webtwob.agd.project.api;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.enums.VerbosityEnum;
import de.webtwob.agd.project.api.interfaces.IVerbosity;

/**
 * Stores the necessary GraphStates to create an Animation
 */
public class GraphState {

	/**
	 * Create a new GraphState Object
	 */
	public GraphState() {
	}

	/**
	 * @param copy
	 *            the GraphState to copy Copy Constructor
	 */
	public GraphState(GraphState copy) {
		copy.elkBendPointMap.forEach((key, value) -> elkBendPointMap.put(key, (Double) value.clone()));
		copy.elkSectionMap.forEach((key, value) -> elkSectionMap.put(key, (Line2D.Double) value.clone()));
		copy.elkNodeMap.forEach((key, value) -> elkNodeMap.put(key, (Rectangle2D.Double) value.clone()));
		copy.nodePositionsTopological.forEach((key, value) -> nodePositionsTopological.put(key, value));
		
		highlightMap.putAll(copy.highlightMap);
		verbosity = copy.verbosity;
		pseudoCodeLine = copy.pseudoCodeLine;
		
	}

	private Map<ElkNode, Rectangle2D.Double> elkNodeMap = new HashMap<>();
	private Map<ElkEdgeSection, Line2D.Double> elkSectionMap = new HashMap<>();
	private Map<ElkBendPoint, Point2D.Double> elkBendPointMap = new HashMap<>();
	private Map<ElkNode, Integer> nodePositionsTopological = new HashMap<>();

	private Map<Object, Color> highlightMap = new HashMap<>();

	private String pseudoCodeLine = "line0";
	

	private IVerbosity verbosity = VerbosityEnum.OFF;

	/**
	 * @param node the Node to retrieve the mapping for
	 * @return the stored mapping for the node or a new mapping
	 */
	public Rectangle2D.Double getMapping(ElkNode node) {
		return elkNodeMap.computeIfAbsent(node, key -> new Rectangle2D.Double());
	}

	/**
	 * @param sect the Section to retrieve the mapping for
	 * @return the stored mapping for the section or a new mapping
	 */
	public Line2D.Double getMapping(ElkEdgeSection sect) {
		return elkSectionMap.computeIfAbsent(sect, key -> new Line2D.Double());
	}

	/**
	 * @param bend the bend point to retrieve the mapping for
	 * @return the stored mapping for the bend point or a new mapping
	 */
	public Point2D.Double getMapping(ElkBendPoint bend) {
		return elkBendPointMap.computeIfAbsent(bend, key -> new Point2D.Double());
	}

	/**
	 * @param obj the object to retrieve the highlight for
	 * @return the Color obj should be highlighted in or null if not set
	 */
	public Color getHighlight(Object obj) {
		return highlightMap.get(obj);
	}

	/**
	 * @param obj the object to store the highlight for
	 * @param col the Color obj should be highlighted in or null to unset
	 * 
	 * If obj is null this is a noop
	 */
	public void setHighlight(Object obj, Color col) {
		if (obj == null)
			return;
		if (col == null) {
			highlightMap.remove(obj);
		}
		highlightMap.put(obj, col);
	}

	/**
	 * @return the verbosity set for this GrapState
	 */
	public IVerbosity getVerbosity() {
		return verbosity;
	}

	/**
	 * @param verbosity the verbosity to be set for this GraphState
	 */
	public void setVerbosity(IVerbosity verbosity) {
		this.verbosity = verbosity;
	}

	/**
	 * @return the identifier for the line in the pseudocode set for this GraphState
	 */
	public String getPseudoCodeLine() {
		return pseudoCodeLine;
	}

	/**
	 * @param pseudoCodeLine the identifier for the line in the pseudocode to be set for this GraphState
	 */
	public void setPseudoCodeLine(String pseudoCodeLine) {
		this.pseudoCodeLine = pseudoCodeLine;
	}
	
	/**
	 * @param node  the node to set the position for
	 * @param i the position 0 indexed, negative values index from the end
	 * 
	 * */
	public void setPossition(ElkNode node, Integer i) {
		this.nodePositionsTopological.put(node, i);
	}
	
	/**
	 * @param node the node for which to retrieve the position @see {@link GraphState#setPossition(ElkNode, Integer)}
	 */
	public OptionalInt getPosition(ElkNode node) {
		var res = nodePositionsTopological.get(node);
		return res == null ? OptionalInt.empty():OptionalInt.of(res);
	}

	public void applyToNode(ElkNode graph) {
		var nodeMap = getMapping(graph);
		graph.setLocation(nodeMap.getX(), nodeMap.getY());
		graph.setDimensions(nodeMap.getWidth(), nodeMap.getHeight());
		
		for(var child:graph.getChildren()) {
			applyToNode(child);
		}
		for(var edge:graph.getContainedEdges()) {
			for(var section:edge.getSections()) {
				applyToSection(section);
			}
		}
			
	}

	private void applyToSection(ElkEdgeSection section) {
		var sectionMap = getMapping(section);
		
		section.setStartLocation(sectionMap.getX1(), sectionMap.getY1());
		section.setEndLocation(sectionMap.getX2(), sectionMap.getY2());
		
		for(var bendpoint:section.getBendPoints()) {
			applyToBendPoint(bendpoint);
		}
	}

	private void applyToBendPoint(ElkBendPoint bendpoint) {
		var bendMap = getMapping(bendpoint);
		
		bendpoint.set(bendMap.getX(), bendMap.getY());
	}
	
}
