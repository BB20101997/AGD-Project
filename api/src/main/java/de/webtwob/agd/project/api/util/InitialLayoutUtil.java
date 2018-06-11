package de.webtwob.agd.project.api.util;

import org.eclipse.elk.alg.force.options.ForceMetaDataProvider;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.data.LayoutMetaDataService;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

public class InitialLayoutUtil {

	private InitialLayoutUtil() {
	}

	static {
		initMetaDataService();
	}

	private static void initMetaDataService() {
		LayoutMetaDataService service = LayoutMetaDataService.getInstance();
		service.registerLayoutMetaDataProviders(new ForceMetaDataProvider());
	}

	public static void setForceLayoutAlgorithm(@SuppressWarnings("exports") ElkNode node) {
		node.setProperty(CoreOptions.ALGORITHM, "org.eclipse.elk.force");
	}

	public static void layout(ElkNode node) {
		RecursiveGraphLayoutEngine rgle = new RecursiveGraphLayoutEngine();
		rgle.layout(node, new BasicProgressMonitor());
	}

}