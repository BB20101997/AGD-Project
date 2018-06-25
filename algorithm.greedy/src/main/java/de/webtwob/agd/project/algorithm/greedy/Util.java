package de.webtwob.agd.project.algorithm.greedy;

import org.eclipse.elk.core.math.KVectorChain;
import org.eclipse.elk.core.util.ElkUtil;
import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

@SuppressWarnings("exports")
public class Util {

	private Util() {
	}

	/**
	 * @param edge
	 *            the edge to reverse
	 * 
	 *            This method expects a simple Edge and will swap source and target
	 */
	public static void reverseEdge(ElkEdge edge) {
		replaceEnds(edge, getTarget(edge), getSource(edge));
		for (ElkEdgeSection sect : edge.getSections()) {
			// swap start points
			var tmpStartX = sect.getStartX();
			var tmpStartY = sect.getStartY();
			sect.setStartLocation(sect.getEndX(), sect.getEndY());
			sect.setEndLocation(tmpStartX, tmpStartY);

			// reverse bend points
			if (!sect.getBendPoints().isEmpty()) {
				var chain = new KVectorChain();
				for (var bend : sect.getBendPoints()) {
					chain.addFirst(bend.getX(), bend.getY());
				}
				ElkUtil.applyVectorChain(chain, sect);
			}
		}
	}

	/**
	 * @param edge
	 *            the edge to get the first target from
	 * @return the first target of the edge
	 * 
	 *         A version of ElkGraphUtil.getTargetNode which doesn't throw if more
	 *         than one Target is present
	 */
	public static ElkNode getTarget(ElkEdge edge) {
		if (edge.getTargets().isEmpty()) {
			throw new IllegalArgumentException("Passed Egde does not have any Targets!");
		}

		return ElkGraphUtil.connectableShapeToNode(edge.getTargets().get(0));

	}

	/**
	 * @param edge
	 *            the edge to get the first source from
	 * @return the first source of the edge
	 * 
	 *         A version of ElkGraphUtil.getSourceNode which doesn't throw if more
	 *         than one Target is present
	 */
	public static ElkNode getSource(ElkEdge edge) {
		if (edge.getSources().isEmpty()) {
			throw new IllegalArgumentException("Passed Egde does not have any Sources!");
		}

		return ElkGraphUtil.connectableShapeToNode(edge.getSources().get(0));

	}

	/**
	 * @param edge the edge to check
	 * @return true if edge has exactly one source and one target
	 * */
	public static boolean isSimpleEdge(ElkEdge edge) {
		return edge.getTargets().size() == 1 && edge.getSources().size() == 1;
	}

	/**
	 * @param edge the edge to change
	 * @param start the new source for edge
	 * @param end the new target for edge
	 * 
	 * sets the source list to contain start and the target list to contain end
	 * */
	public static void replaceEnds(ElkEdge edge, ElkConnectableShape start, ElkConnectableShape end) {

		edge.getSources().clear();
		edge.getTargets().clear();
		edge.getSources().add(start);
		edge.getTargets().add(end);
	}

}
