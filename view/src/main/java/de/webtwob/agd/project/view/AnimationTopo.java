package de.webtwob.agd.project.view;

import static de.webtwob.agd.project.view.util.ViewUtil.getCurrent;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.GraphState;
import de.webtwob.agd.project.api.util.Pair;
import de.webtwob.agd.project.view.util.ViewUtil;

public class AnimationTopo extends Animation  {
	
	/**
	 *
	 * @param root
	 *            the graph to be animated
	 * @param mapping
	 *            the start and end state for the aimation
	 * @param length
	 *            in frames
	 */
	@SuppressWarnings("exports")
	public AnimationTopo(ElkNode root, Pair<GraphState> mapping, int length) {
		super(root, mapping, length);
	}

	@Override
	public void generateFrame(long frame, Graphics2D graphic) {

		for (ElkNode child : root.getChildren()) {
			Integer pos = mapping.getEnd().getPosition(child);
			if (pos!=null) {
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
		if (pos>0) {
			rect.setRect(10, 20.0*pos, 10, 10);			
		} else {
			
			rect.setRect(10, 20.0*(size-pos), 10, 10);
		}
		
		var color = getCurrent(mapping.getStart().getHighlight(node), mapping.getEnd().getHighlight(node),
				graphic.getBackground(), frame, lengthInMills);

		ViewUtil.drawNode((Graphics2D) graphic.create(), node.getIdentifier(), rect, color);

		// draw the sub-graph
		Graphics2D subGraphic = (Graphics2D) graphic.create((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(),
				(int) rect.getHeight());
		subGraphic.dispose();
	}

	//TODO override getWith and getHeight to return correct values
}
