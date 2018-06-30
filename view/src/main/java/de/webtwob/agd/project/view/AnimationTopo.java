package de.webtwob.agd.project.view;

import static de.webtwob.agd.project.view.util.ViewUtil.getCurrent;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.GraphState;
import de.webtwob.agd.project.api.util.Pair;
import de.webtwob.agd.project.view.util.ViewUtil;

/**
 * An animation between two GraphStates animating the order of nodes based on the position mapping
 * */
public class AnimationTopo extends Animation {

	private static final double NODE_SIZE = 60;
	private static final double SPACING = 10;

	/**
	 *
	 * @param root
	 *            the graph to be animated
	 * @param mapping
	 *            the start and end state for the aimation
	 * @param length
	 *            in frames
	 */
	public AnimationTopo(ElkNode root, Pair<GraphState> mapping, int length) {
		super(root, mapping, length);
	}

	@Override
	public void generateFrame(long frame, Graphics2D graphic) {

		for (ElkNode child : root.getChildren()) {
			Integer pos = mapping.getEnd().getPosition(child);
			if (pos != null) {
				drawNode(child, graphic, frame);
			}
		}
	}

	@Override
	protected void drawNode(ElkNode node, Graphics2D graphic, long frame) {

		Rectangle2D.Double rect = getCurrent(mapping.getStart().getMapping(node), mapping.getEnd().getMapping(node),
				frame, lengthInMills);
		int pos = mapping.getEnd().getPosition(node);
		int size = root.getChildren().size();
		if (pos >= 0) {
			rect.setRect(SPACING, SPACING + (NODE_SIZE + SPACING) * pos, NODE_SIZE, NODE_SIZE);
		} else {

			rect.setRect(SPACING, SPACING + (NODE_SIZE + SPACING) * (size + pos), NODE_SIZE, NODE_SIZE);
		}

		var color = getCurrent(mapping.getStart().getHighlight(node), mapping.getEnd().getHighlight(node),
				graphic.getBackground(), frame, lengthInMills);

		ViewUtil.drawNode((Graphics2D) graphic.create(), node.getIdentifier(), rect, color);

		// draw the sub-graph
		Graphics2D subGraphic = (Graphics2D) graphic.create((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(),
				(int) rect.getHeight());
		subGraphic.dispose();
	}

	@Override
	public double getWidth() {
		if (root.getChildren().isEmpty()) {
			return 0;
		}
		return NODE_SIZE + 2 * SPACING;
	}

	@Override
	public double getHeight() {
		if (root.getChildren().isEmpty()) {
			return 0;
		}
		return root.getChildren().size() * (NODE_SIZE + SPACING) + SPACING;
	}
}
