import de.webtwob.agd.project.api.interfaces.IAlgorithm;
import de.webtwob.agd.project.model.alg.GreedyCycleBreakAlgorithm;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
module de.webtwob.agd.project.model {

	requires org.eclipse.elk.graph;
	requires org.eclipse.elk.core;
	
	requires de.webtwob.agd.project.api;
	requires de.webtwob.agd.project.view;

	exports de.webtwob.agd.project.model;
	
	provides IAlgorithm with GreedyCycleBreakAlgorithm;
}
