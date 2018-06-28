import de.webtwob.agd.project.file.json.JSONGraphLoader;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
@SuppressWarnings("module")
module de.webtwob.agd.project.file.json {
	requires org.eclipse.elk.graph;
	requires org.eclipse.elk.graph.json;
	requires gson;

	requires de.webtwob.agd.project.api;

	exports de.webtwob.agd.project.file.json to de.webtwob.agd.project.test;

	provides de.webtwob.agd.project.api.interfaces.IGraphLoader with JSONGraphLoader;
}
