module de.webtwob.agd.project.test {
	requires junit;
	requires java.desktop;
	requires java.base;

	requires org.eclipse.elk.alg.force;
	requires org.eclipse.elk.graph;
	requires org.eclipse.elk.core;

	requires de.webtwob.agd.project.control;
	requires de.webtwob.agd.project.model;
	requires de.webtwob.agd.project.api;
	requires de.webtwob.agd.project.file.json;
	requires de.webtwob.agd.project.file.toml;
	requires de.webtwob.agd.project.main;
	requires de.webtwob.agd.project.view;

}
