package de.webtwob.agd.project.model;

import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
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

	}

	/**
	 * A version of ElkGraphUtil.getTargetNode which doesn't throw if more than one
	 * Target is present
	 */
	public static ElkNode getTarget(ElkEdge edge) {
		if (edge.getTargets().isEmpty()) {
			throw new IllegalArgumentException("Passed Egde does not have any Targets!");
		}

		return ElkGraphUtil.connectableShapeToNode(edge.getTargets().get(0));

	}

	/**
	 * A version of ElkGraphUtil.getSourceNode which doesn't throw if more than one
	 * Target is present
	 */
	public static ElkNode getSource(ElkEdge edge) {
		if (edge.getSources().isEmpty()) {
			throw new IllegalArgumentException("Passed Egde does not have any Sources!");
		}

		return ElkGraphUtil.connectableShapeToNode(edge.getSources().get(0));

	}

	public static boolean isSimpleEdge(ElkEdge edge) {
		return edge.getTargets().size() == 1 && edge.getSources().size() == 1;
	}

	public static void replaceEnds(ElkEdge edge, ElkConnectableShape start, ElkConnectableShape end) {

		edge.getSources().clear();
		edge.getTargets().clear();
		edge.getSources().add(start);
		edge.getTargets().add(end);
	}

}
