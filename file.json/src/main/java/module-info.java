import de.webtwob.agd.project.file.json.JSONGraphLoader;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
module de.webtwob.agd.project.file.json {
	requires org.eclipse.elk.graph;
	requires org.eclipse.elk.graph.json;
	requires gson;

	requires transitive de.webtwob.agd.project.api;

	exports de.webtwob.agd.project.file.json;

	provides de.webtwob.agd.project.api.interfaces.IGraphLoader with JSONGraphLoader;
}
