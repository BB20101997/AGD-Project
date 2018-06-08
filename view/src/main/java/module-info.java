module de.webtwob.agd.project.view{
	
	//java requires
	//java requires transitive
	requires transitive java.desktop;
	
	//external requires
	requires org.eclipse.elk.graph;
	requires org.eclipse.emf.ecore;
	
	//internal requires
	
	//internal requires transitive
	requires transitive de.webtwob.agd.project.service;
	
	//exports
	exports de.webtwob.agd.project.view;
	exports de.webtwob.agd.project.view.panel;
	
}
