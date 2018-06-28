package de.webtwob.agd.project.view;

import static de.webtwob.agd.project.view.util.ViewUtil.getCurrent;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.elk.graph.ElkBendPoint;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.GraphState;
import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.api.util.Pair;
import de.webtwob.agd.project.view.util.ViewUtil;

public class Animation implements IAnimation {

	protected final long lengthInMills;
	protected final Pair<GraphState> mapping;
	protected final ElkNode root;

	/**
	 *
	 * @param root
	 *            the graph to be animated
	 * @param mapping
	 *            the start and end state for the aimation
	 * @param length
	 *            in frames
	 */
	public Animation(ElkNode root, Pair<GraphState> mapping, int length) {
		this.root = root;
		this.mapping = mapping;
		lengthInMills = length;
	}

	@Override
	public void generateFrame(long frame, Graphics2D graphic) {

		for (ElkNode child : root.getChildren()) {
			drawNode(child, graphic, frame);
		}

		for (ElkEdge edge : root.getContainedEdges()) {
			drawEdge(edge, graphic, frame);
		}
	}

	protected void drawNode(ElkNode node, Graphics2D graphic, long frame) {

		Rectangle2D.Double rect = getCurrent(mapping.getStart().getMapping(node), mapping.getEnd().getMapping(node),
				frame, lengthInMills);

		var color = getCurrent(mapping.getStart().getHighlight(node), mapping.getEnd().getHighlight(node),
				graphic.getBackground(), frame, lengthInMills);

		ViewUtil.drawNode((Graphics2D) graphic.create(), node.getIdentifier(), rect, color);

		// draw the sub-graph
		Graphics2D subGraphic = (Graphics2D) graphic.create((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(),
				(int) rect.getHeight());
		node.getChildren().forEach(child -> drawEdge((ElkEdge) child, subGraphic, frame));
		node.getContainedEdges().forEach(edge -> drawEdge(edge, subGraphic, frame));
		subGraphic.dispose();
	}

	protected void drawEdge(ElkEdge e, Graphics2D g, long frame) {
		var forground = g.getColor();

		var color = getCurrent(mapping.getStart().getHighlight(e), mapping.getEnd().getHighlight(e), forground, frame,
				lengthInMills);
		if (color != null) {
			g.setColor(color);
		}

		e.getSections().forEach(s -> drawEdgeSection(s, g, frame));
		g.setColor(forground);
	}

	private void drawEdgeSection(ElkEdgeSection s, Graphics2D g, long frame) {
		// mapping.pointInTime.EndOfSection.pos
		Path2D path = new Path2D.Double();

		Point2D point = getCurrent(mapping.getStart().getMapping(s).getP1(), mapping.getEnd().getMapping(s).getP1(),
				frame, lengthInMills);

		path.moveTo(point.getX(), point.getY());

		var secondToLast = point;

		for (int i = 0; i < s.getBendPoints().size(); i++) {
			point = getBendPoint(s.getBendPoints().get(i), frame);
			path.lineTo(point.getX(), point.getY());
			secondToLast = point;
		}

		point = getCurrent(mapping.getStart().getMapping(s).getP2(), mapping.getEnd().getMapping(s).getP2(), frame,
				lengthInMills);

		path.lineTo(point.getX(), point.getY());

		var diff = new Point2D.Double(point.getX() - secondToLast.getX(), point.getY() - secondToLast.getY());

		var color = getCurrent(mapping.getStart().getHighlight(s), mapping.getEnd().getHighlight(s), g.getColor(),
				frame, lengthInMills);
		
		ViewUtil.drawEdgeSection((Graphics2D) g.create(), path, Math.atan2(diff.getX(),-diff.getY()), color);
	}

	private Point2D getBendPoint(ElkBendPoint p, long frame) {
		return getCurrent(mapping.getStart().getMapping(p), mapping.getEnd().getMapping(p), frame, lengthInMills);
	}

	@Override
	public double getWidth() {
		return Math.max(mapping.getStart().getMapping(root).getWidth(), mapping.getEnd().getMapping(root).getWidth());
	}

	@Override
	public double getHeight() {
		return Math.max(mapping.getStart().getMapping(root).getHeight(), mapping.getEnd().getMapping(root).getHeight());
	}

	@Override
	public long getLength() {
		return lengthInMills;
	}

	@Override
	public GraphState getGraphStatesForFrame(long frame) {
		if(frame<lengthInMills/2) {
			return mapping.getStart();
		}else {
			return mapping.getEnd();
		}
	}

}
