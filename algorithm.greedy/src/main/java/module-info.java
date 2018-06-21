import de.webtwob.agd.project.algorithm.greedy.GreedyCycleBreakAlgorithm;
import de.webtwob.agd.project.api.interfaces.IAlgorithm;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
module de.webtwob.agd.project.algorithm.greedy {

	requires org.eclipse.elk.graph;
	requires org.eclipse.elk.core;

	requires de.webtwob.agd.project.api;
	requires de.webtwob.agd.project.view;
	requires java.desktop;

	exports de.webtwob.agd.project.algorithm.greedy;

	provides IAlgorithm with GreedyCycleBreakAlgorithm;
}
