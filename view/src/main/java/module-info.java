@SuppressWarnings("module")
module de.webtwob.agd.project.view {

	//requires transitive
	requires transitive java.desktop;
	requires transitive de.webtwob.agd.project.api;

	//requires
	//if this weren't an automatic module this would be transitive
	requires org.eclipse.elk.core;
	requires org.eclipse.elk.graph;
	
	// exports
	exports de.webtwob.agd.project.view;
	exports de.webtwob.agd.project.view.panel;

}
