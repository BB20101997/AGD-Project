package de.webtwob.agd.project.api;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.eclipse.elk.graph.ElkNode;

public interface IGraphLoader {
	
	static List<IGraphLoader> loader = ServiceLoader.load(IGraphLoader.class).stream().map(Provider::get)
			.collect(Collectors.toList());

	@SuppressWarnings("exports")
	public Optional<ElkNode> loadGraphFromFile(File file);
	
	public FileFilter getFileFilter();
	
	@SuppressWarnings("exports")
	public static Optional<ElkNode> loadGraph(){
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
	public static Optional<ElkNode> loadGraph(File file){
		if(file==null||file.isDirectory()) {
			return Optional.<ElkNode>empty();
		}
		return loader.stream().filter(load->load.getFileFilter().accept(file)).flatMap(l->l.loadGraphFromFile(file).stream()).findFirst();
	} 
	
}
