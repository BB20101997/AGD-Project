package de.webtwob.agd.project.algorithm.greedy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JPanel;

import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.AnimationSyncThread;
import de.webtwob.agd.project.api.GraphState;
import de.webtwob.agd.project.api.VerbosityEnum;
import de.webtwob.agd.project.api.interfaces.IAlgorithm;
import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.api.util.GraphStateListBuilder;
import de.webtwob.agd.project.api.util.InitialLayoutUtil;
import de.webtwob.agd.project.view.AnimatedView;
import de.webtwob.agd.project.view.CompoundAnimation;

public class GreedyCycleBreakAlgorithm implements IAlgorithm {

	@Override
	public IAnimation getAnimationPanel(JPanel panel, ElkNode graph, AnimationSyncThread thread) {

		panel.setLayout(new BorderLayout());

		if (!graph.getProperty(CoreOptions.NO_LAYOUT)) {
			// apply the force layout algorithm to get an initial layout
			InitialLayoutUtil.setForceLayoutAlgorithm(graph);
			InitialLayoutUtil.layout(graph);
		}

		LinkedList<GraphState> steps = new LinkedList<>();

		getSteps(graph, steps);

		IAnimation anim = new CompoundAnimation(graph, steps, 500);

		var animView = new AnimatedView(thread);
		animView.setAnimation(anim);

		panel.setPreferredSize(new Dimension((int) Math.ceil(anim.getWidth()), (int) Math.ceil(anim.getHeight())));

		panel.add(animView, BorderLayout.CENTER);
		panel.repaint();

		return anim;
	}

	@Override
	public String getName() {
		return "Cyclebreak Greedy";
	}

	@Override
	public String getPseudoCode() {
		String lines = null;
		try {
			var uri = getClass().getResource("de/webtwob/agd/project/algorithm/greedy/GreedyCycleBreakPseudoCode.html");

			if (uri == null) {
				uri = new File(
						"../algorithm.greedy/src/main/resources/de/webtwob/agd/project/algorithm/greedy/GreedyCycleBreakPseudoCode.html")
								.toURI().toURL();
			}

			var resStream = uri.openStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(resStream));
			lines = reader.lines().collect(Collectors.joining());
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

	/**
	 * Greedy cycle break from before
	 *
	 * @param graph
	 *            the graph which cycles shall be broken
	 * @param steps
	 *            the List to store the GraphStates into
	 */
	public static void getSteps(ElkNode graph, List<GraphState> steps) {

		var stateBuilder = GraphStateListBuilder.createBuilder().startWith(graph).atLine("line0");

		// copy child list so we can remove already sorted ones
		List<ElkNode> children = new LinkedList<>(graph.getChildren());

		// sources at the beginning add to the end
		LinkedList<ElkNode> sourceList = new LinkedList<>();
		// sinks at the end add to the beginning
		LinkedList<ElkNode> sinkList = new LinkedList<>();

		if (children.isEmpty()) {
			stateBuilder.atLine("while_has_children");
		}
		// 0 Steps
		while (!children.isEmpty()) {
			stateBuilder.atLine("while_has_children");

			boolean found;
			// sort out source
			do {
				stateBuilder.atLine("do_source_found");

				found = false;

				if (children.isEmpty()) {
					stateBuilder.atLine("for_each_child_source");
				}

				ElkNode lastNode = null;

				for (Iterator<ElkNode> iter = children.iterator(); iter.hasNext();) {
					ElkNode currentNode = iter.next();

					stateBuilder.atLine("for_each_child_source").unhighlight(lastNode).highlight(currentNode).active();

					// is node a Source given the currently present nodes in children
					stateBuilder.atLine("is_source");
					if (currentNode.getIncomingEdges().parallelStream().map(Util::getSource)
							.noneMatch(children::contains)) {
						stateBuilder.atLine("mark_source").addSource(currentNode);
						sourceList.addLast(currentNode);
						iter.remove(); // avoid ConcurrentModificationException
						found = true;
						lastNode = null;
					} else {
						lastNode = currentNode;
					}
				}

				stateBuilder.atLine("while_source_found").unhighlight(lastNode);

			} while (found);// stop when an iteration didn't found sinks

			// sort out sink
			do {
				stateBuilder.atLine("do_sink_found");
				found = false;

				if (children.isEmpty()) {
					stateBuilder.atLine("for_each_child_sink");
				}

				ElkNode lastNode = null;

				for (Iterator<ElkNode> iter = children.iterator(); iter.hasNext();) {

					ElkNode currentNode = iter.next();

					stateBuilder.atLine("for_each_child_sink").unhighlight(lastNode).highlight(currentNode).active();

					// is node a Source given the currently present nodes in children
					stateBuilder.atLine("is_sink");
					if (currentNode.getOutgoingEdges().parallelStream().map(Util::getTarget)
							.noneMatch(children::contains)) {
						sinkList.addFirst(currentNode);

						stateBuilder.atLine("mark_sink").addSink(currentNode);

						iter.remove(); // avoid ConcurrentModificationException
						found = true;
						lastNode = null;
					} else {
						lastNode = currentNode;
					}

				}
				stateBuilder.atLine("while_sink_found").unhighlight(lastNode);
			} while (found);// stop when an iteration didn't found sinks

			// find edge with max in-degree to out-degree difference
			ElkNode maxNode = null;
			int maxDiff = Integer.MIN_VALUE;
			if (children.isEmpty()) {
				stateBuilder.atLine("for_each_remaining");
			}

			ElkNode lastNode = null;
			for (Iterator<ElkNode> iter = children.iterator(); iter.hasNext();) {

				ElkNode currentNode = iter.next();

				stateBuilder.atLine("for_each_remaining").unhighlight(lastNode).highlight(currentNode).active();

				int curVal = currentNode.getOutgoingEdges().size() - currentNode.getIncomingEdges().size();

				stateBuilder.atLine("is_new_max");

				if (curVal > maxDiff) {
					stateBuilder.atLine("set_max").unhighlight(maxNode).highlight(currentNode).in(Color.RED);
					maxDiff = curVal;
					maxNode = currentNode;
					lastNode = null;
				} else {
					lastNode = currentNode;
				}
			}

			stateBuilder.atLine("end_for_each_remaining").unhighlight(lastNode);

			// if we still had nodes add the one with max out to in diff to source list
			if (maxNode != null) {
				sourceList.addLast(maxNode);
				children.remove(maxNode);
				stateBuilder.atLine("max_as_source").addSource(maxNode);
			}

		}

		stateBuilder.atLine("no_children_left");

		// remove cycles
		List<ElkNode> combinedList = new LinkedList<>();
		combinedList.addAll(sourceList);
		combinedList.addAll(sinkList);

		if (graph.getContainedEdges().isEmpty()) {
			stateBuilder.atLine("for_each_edge");
		}

		ElkEdge lastEdge = null;

		for (var edge : graph.getContainedEdges()) {

			stateBuilder.atLine("for_each_edge").unhighlight(lastEdge).highlight(edge).in(Color.BLUE);

			// reverse all edges where the source Node index is higher than the target node
			// index
			stateBuilder.atLine("needs_to_be_reversed");
			if (combinedList.indexOf(Util.getSource(edge)) > combinedList.indexOf(Util.getTarget(edge))) {
				Util.reverseEdge(edge);
				stateBuilder.atLine("reverse_edge").updateEdge(edge).highlight(edge).in(Color.GREEN);
				lastEdge = null;
			} else {
				lastEdge = edge;
			}
		}

		stateBuilder.atLine("the_end").unhighlight(lastEdge).withVerbosity(VerbosityEnum.ALLWAYS);

		steps.addAll(stateBuilder.getList());
	}

}
