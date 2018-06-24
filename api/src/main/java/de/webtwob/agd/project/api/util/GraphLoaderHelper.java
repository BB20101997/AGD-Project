package de.webtwob.agd.project.api.util;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.interfaces.IGraphLoader;

public class GraphLoaderHelper {
	
	private GraphLoaderHelper() {}

	private static final List<IGraphLoader> loader = ServiceLoader.load(IGraphLoader.class).stream()
			.map(ServiceLoader.Provider::get).collect(Collectors.toList());

	@SuppressWarnings("exports")
	public static Optional<ElkNode> loadGraph() {
		JFileChooser chooser = new JFileChooser(".");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogTitle("Select Graph to be animated!");
		chooser.setMultiSelectionEnabled(false);

		for (IGraphLoader load : loader) {
			chooser.addChoosableFileFilter(load.getFileFilter());
		}

		chooser.showOpenDialog(null);

		return loadGraph(chooser.getSelectedFile());
	}

	@SuppressWarnings("exports")
	public static Optional<ElkNode> loadGraph(File file) {
		if (file == null || file.isDirectory()) {
			return Optional.empty();
		}
		return loader.stream().filter(load -> load.getFileFilter().accept(file))
				.map(load -> GraphLoaderHelper.tryLoader(load, file)).filter(Optional::isPresent).map(Optional::get)
				.findFirst();
	}

	private static Optional<ElkNode> tryLoader(IGraphLoader loader, File file) {
		try {
			return loader.loadGraphFromFile(file);
		} catch (NoClassDefFoundError ignore) {
			//loading failed going to return empty optional
		}
		return Optional.<ElkNode>empty();
	}
}
