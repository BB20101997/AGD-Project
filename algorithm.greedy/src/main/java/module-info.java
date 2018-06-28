import de.webtwob.agd.project.algorithm.greedy.GreedyCycleBreakAlgorithm;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
@SuppressWarnings("module")
module de.webtwob.agd.project.algorithm.greedy {

	requires org.eclipse.elk.graph;
	requires org.eclipse.elk.core;

	requires de.webtwob.agd.project.api;
	requires de.webtwob.agd.project.view;
	requires java.desktop;
	
	provides de.webtwob.agd.project.api.interfaces.IAlgorithm with GreedyCycleBreakAlgorithm;
}
