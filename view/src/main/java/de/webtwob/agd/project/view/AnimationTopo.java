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

public class AnimationTopo implements IAnimation  {


	private final long lengthInMills;
	private final Pair<GraphState> mapping;
	private final ElkNode root;

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
		this.root = root;
		this.mapping = mapping;
		lengthInMills = length;
	}

	public void generateFrame(long frame, Graphics2D graphic) {

		for (ElkNode child : root.getChildren()) {
			Integer pos = mapping.getEnd().getPosition(child);
			if (pos!=null)
			drawNode(child, graphic, frame);
		}
	}

	private void drawNode(ElkNode node, Graphics2D graphic, long frame) {

		Rectangle2D.Double rect = getCurrent(mapping.getStart().getMapping(node), mapping.getEnd().getMapping(node),
				frame, lengthInMills);
		int pos = mapping.getEnd().getPosition(node);
		int size = root.getChildren().size();
		if (pos>0) {
			rect.setRect(10, 20*pos, 10, 10);			
		} else {
			
			rect.setRect(10, 20*(size-pos), 10, 10);
		}
		
		var color = getCurrent(mapping.getStart().getHighlight(node), mapping.getEnd().getHighlight(node),
				graphic.getBackground(), frame, lengthInMills);

		ViewUtil.drawNode((Graphics2D) graphic.create(), node.getIdentifier(), rect, color);

		// draw the sub-graph
		Graphics2D subGraphic = (Graphics2D) graphic.create((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(),
				(int) rect.getHeight());
		subGraphic.dispose();
	}





	public double getWidth() {
		return Math.max(mapping.getStart().getMapping(root).getWidth(), mapping.getEnd().getMapping(root).getWidth());
	}

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
