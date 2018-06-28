import de.webtwob.agd.project.test.algorith.force.ForceTestAlgorithm;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
@SuppressWarnings("module")
module de.webtwob.agd.project.test.algorithm.force {

	requires org.eclipse.elk.graph;

	requires de.webtwob.agd.project.api;
	requires de.webtwob.agd.project.view;
	requires java.desktop;

	provides de.webtwob.agd.project.api.interfaces.IAlgorithm with ForceTestAlgorithm;
}
