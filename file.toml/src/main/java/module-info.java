import de.webtwob.agd.project.file.toml.TOMLGraphLoader;

/**
 * Created by BB20101997 on 31. Mai. 2018.
 */
module de.webtwob.agd.project.file.toml {
	requires org.eclipse.elk.core;
	requires org.eclipse.elk.graph;
	
	requires java.sql; //needed for the toml4j library to work
	requires toml4j;

	requires de.webtwob.agd.project.api;

	exports de.webtwob.agd.project.file.toml to de.webtwob.agd.project.test;

	provides de.webtwob.agd.project.api.interfaces.IGraphLoader with TOMLGraphLoader;
}
