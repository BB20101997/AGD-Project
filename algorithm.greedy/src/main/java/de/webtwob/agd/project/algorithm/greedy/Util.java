package de.webtwob.agd.project.algorithm.greedy;

import org.eclipse.elk.core.math.KVector;
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
	 * @param edge
	 *            the edge to check
	 * @return true if edge has exactly one source and one target
	 */
	public static boolean isSimpleEdge(ElkEdge edge) {
		return edge.getTargets().size() == 1 && edge.getSources().size() == 1;
	}

	/**
	 * @param edge
	 *            the edge to change
	 * @param start
	 *            the new source for edge
	 * @param end
	 *            the new target for edge
	 * 
	 *            sets the source list to contain start and the target list to
	 *            contain end
	 */
	public static void replaceEnds(ElkEdge edge, ElkConnectableShape start, ElkConnectableShape end) {

		edge.getSources().clear();
		edge.getTargets().clear();
		edge.getSources().add(start);
		edge.getTargets().add(end);
	}

	public static void routeEdges(ElkNode node) {
		node.getContainedEdges().forEach(Util::routeEdge);
	}

	/**
	 * Taken from our FruchtermanReingold Implementation
	 * */
	private static void routeEdge(ElkEdge edge) {
		// we ignore all but the first source and target
		ElkNode source = ElkGraphUtil.connectableShapeToNode(edge.getSources().get(0));
		ElkNode target = ElkGraphUtil.connectableShapeToNode(edge.getTargets().get(0));

		ElkEdgeSection section = ElkGraphUtil.firstEdgeSection(edge, true, true);

		KVector vector = difference(target, source);

		KVector start = calculateEdgeEndPoint(source, vector);
		KVector end = calculateEdgeEndPoint(target, vector.scale(-1));

		section.setStartLocation(start.x, start.y);
		section.setEndLocation(end.x, end.y);

	}

	/**
	 * Taken from our FruchtermanReingold Implementation
	 * */
	private static KVector calculateEdgeEndPoint(ElkNode node, KVector direction) {
		KVector end = new KVector(node.getX() + node.getWidth() / 2, node.getY() + node.getHeight() / 2);

		if (direction.length() == 0) {
			return end;
		}
		direction.normalize();
		if (direction.x == 0 || direction.y == 0 || node.getWidth() == 0 || node.getHeight() == 0) {
			return end.add(node.getWidth() / 2 * direction.x, node.getHeight() / 2 * direction.y);
		}

		if (Math.abs(direction.x / direction.y) > Math.abs(node.getWidth() / node.getHeight())) {
			end.add(direction.scale(Math.abs(node.getWidth() / 2 / direction.x)));
		} else {
			end.add(direction.scale(Math.abs(node.getHeight() / 2 / direction.y)));
		}

		return end;
	}

	/**
	 * @param a
	 *            Node who's position to subtract from
	 * @param b
	 *            Node who's position to subtract
	 *
	 * @return vector of a.pos - b.pos
	 * 
	 * Taken from our FruchtermanReingold Implementation
	 */
	private static KVector difference(ElkNode a, ElkNode b) {
		return new KVector(a.getX() - b.getX(), a.getY() - b.getY());
	}

}
