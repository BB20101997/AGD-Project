package de.webtwob.agd.project.view;

import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.api.util.GraphMapping;
import de.webtwob.agd.project.api.util.Pair;
import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static de.webtwob.agd.project.api.util.ViewUtil.getCurrent;

public class Animation implements IAnimation {

	private final long lengthInMills;
	private final GraphMapping mapping;
	private final ElkNode root;

	/**
	 * length in Frames
	 */
	@SuppressWarnings("exports")
	public Animation(ElkNode root, GraphMapping mapping, int length) {
		this.root = root;
		this.mapping = mapping;
		lengthInMills = length;
	}

	public void generateFrame(long frame, Graphics2D graphic) {

		for (ElkNode child : root.getChildren()) {
			drawNode(child, graphic, frame);
		}

		for (ElkEdge edge : root.getContainedEdges()) {
			drawEdge(edge, graphic, frame);
		}
	}

	private void drawNode(ElkNode node, Graphics2D graphic, long frame) {

		Rectangle2D.Double rect = getCurrent(mapping.getMapping(node).getStart(), mapping.getMapping(node).getEnd(),
				frame, lengthInMills);
		
		var color = mapping.getHighlight(node);
		
		if(color!=null) {
			var forground = graphic.getColor();
			graphic.setColor(color);
			graphic.fill(rect);
			graphic.setColor(forground);
		}
		
		graphic.draw(rect);

		// draw the sub-graph
		Graphics2D subGraphic = (Graphics2D) graphic.create((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(),
				(int) rect.getHeight());
		node.getChildren().forEach(child -> drawEdge((ElkEdge) child, subGraphic, frame));
		node.getContainedEdges().forEach(edge -> drawEdge(edge, subGraphic, frame));
		subGraphic.dispose();
	}

	protected void drawEdge(ElkEdge e, Graphics2D g, long frame) {
		var forground = g.getColor();
		var color = mapping.getHighlight(e);
		if(color!=null) {
			g.setColor(color);
		}
		e.getSections().forEach(s -> drawEdgeSection(s, g, frame));
		g.setColor(forground);
	}

	private void drawEdgeSection(ElkEdgeSection s, Graphics2D g, long frame) {
		// TODO optionally draw arrows at the end of an edge
		// mapping.pointInTime.EndOfSection.pos
		Path2D path = new Path2D.Double();

		Point2D point = getCurrent(mapping.getMapping(s).getStart().getP1(), mapping.getMapping(s).getEnd().getP1(),
				frame, lengthInMills);
		path.moveTo(point.getX(), point.getY());

		for (int i = 0; i < s.getBendPoints().size(); i++) {
			point = getBendPoint(s.getBendPoints().get(i), frame);
			path.lineTo(point.getX(), point.getY());
		}

		point = getCurrent(mapping.getMapping(s).getStart().getP2(), mapping.getMapping(s).getEnd().getP2(), frame,
				lengthInMills);
		path.lineTo(point.getX(), point.getY());
		
		var color = mapping.getHighlight(s);
		if(color!=null) {
			var forground = g.getColor();
			g.setColor(color);
			g.draw(path);
			g.setColor(forground);
		}else {
			g.draw(path);
		}
	}

	private Point2D getBendPoint(ElkBendPoint p, long frame) {
		Pair<Point2D.Double> bendMapping = mapping.getMapping(p);
		return getCurrent(bendMapping.getStart(), bendMapping.getEnd(), frame, lengthInMills);
	}

	public double getWidth() {
		return Math.max(mapping.getMapping(root).getStart().getWidth(), mapping.getMapping(root).getEnd().getWidth());
	}

	public double getHeight() {
		return Math.max(mapping.getMapping(root).getStart().getHeight(), mapping.getMapping(root).getEnd().getHeight());
	}

	@Override
	public long getLength() {
		return lengthInMills;
	}

}
