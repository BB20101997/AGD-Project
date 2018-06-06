package de.webtwob.agd.project.view;

import static de.webtwob.agd.project.service.util.ViewUtil.getCurrent;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.service.util.GraphMapping;
import de.webtwob.agd.project.service.util.GraphMapping.Pair;

public class Animation {

	long lengthInMills;
	double speed;

	GraphMapping mapping;
	ElkNode root;

	/**
	 * length in Frames
	 */
	@SuppressWarnings("exports")
	public Animation(ElkNode root, GraphMapping mapping, int length) {
		this.root = root;
		this.mapping = mapping;
		lengthInMills = length;
	}

	@SuppressWarnings("exports")
	public void generateFrame(long frame, Graphics2D graphic) {

		double width = graphic.getClipBounds().getWidth();
		double height = graphic.getClipBounds().getHeight();

		double scale = Math.min(width / getWidth(), height / getHeight());

		graphic.scale(scale, scale);

		for (ElkNode child : root.getChildren()) {
			drawNode(child, graphic, frame);
		}

		for (ElkEdge edge : root.getContainedEdges()) {
			drawEdge(edge, graphic, frame);
		}
	}

	private void drawNode(ElkNode node, Graphics2D graphic, long frame) {

		Rectangle2D.Double rect = getCurrent(mapping.getMapping(node).start, mapping.getMapping(node).end, frame,
				lengthInMills);
		graphic.draw(rect);

		// draw the sub-graph
		Graphics2D subGraphic = (Graphics2D) graphic.create((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(),
				(int) rect.getHeight());
		node.getChildren().forEach(child -> drawEdge((ElkEdge) child, subGraphic, frame));
		node.getContainedEdges().forEach(edge -> drawEdge(edge, subGraphic, frame));
		subGraphic.dispose();
	}

	protected void drawEdge(ElkEdge e, Graphics2D g, long frame) {
		e.getSections().forEach(s -> drawEdgeSection(s, g, frame));
	}

	private void drawEdgeSection(ElkEdgeSection s, Graphics2D g, long frame) {
		// TODO optionally draw arrows at the end of an edge
		// mapping.pointInTime.EndOfSection.pos
		Path2D.Double path = new Path2D.Double();

		Point2D.Double point = getCurrent(mapping.getMapping(s).start.start, mapping.getMapping(s).end.start, frame,
				lengthInMills);
		path.moveTo(point.getX(), point.getY());

		for (int i = 0; i < s.getBendPoints().size(); i++) {
			point = getBendPoint(s.getBendPoints().get(i), frame);
			path.lineTo(point.getX(), point.getY());
		}

		point = getCurrent(mapping.getMapping(s).start.end, mapping.getMapping(s).end.end, frame, lengthInMills);
		path.lineTo(point.getX(), point.getY());

		g.draw(path);
	}

	private Point2D.Double getBendPoint(ElkBendPoint p, long frame) {
		Pair<Point2D.Double> bendMapping = mapping.getMapping(p);
		return getCurrent(bendMapping.start, bendMapping.end, frame, lengthInMills);
	}

	public double getWidth() {
		return Math.max(mapping.getMapping(root).start.getWidth(), mapping.getMapping(root).end.getWidth());
	}

	public double getHeight() {
		return Math.max(mapping.getMapping(root).start.getHeight(), mapping.getMapping(root).end.getHeight());
	}

	public void setLength(int length) {
		lengthInMills = length;
	}
}
