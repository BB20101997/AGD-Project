package de.webtwob.agd.project.main;

import java.awt.BorderLayout;
import java.io.File;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.IController;
import de.webtwob.agd.project.api.IGraphLoader;
import de.webtwob.agd.project.control.Control;
import de.webtwob.agd.project.view.panel.MainPanel;

public class Main {

	public static void main(String[] args) {

		boolean fileSelected = false;
		File tmpFile = null;

		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				if (args[i].equals("-file") && i + 1 < args.length) {
					tmpFile = new File(args[i + 1]);
				}
			}
		}

		List<IGraphLoader> loader = ServiceLoader.load(IGraphLoader.class).stream().map(Provider::get)
				.collect(Collectors.toList());

		if (!fileSelected) {
			JFileChooser chooser = new JFileChooser(".");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setDialogTitle("Select Graph to be animated!");
			chooser.setMultiSelectionEnabled(false);
			
			for (IGraphLoader load : loader) {
				chooser.addChoosableFileFilter(load.getFileFilter());
			}

			switch (chooser.showOpenDialog(null)) {
			case JFileChooser.APPROVE_OPTION: {
				tmpFile = chooser.getSelectedFile();
				break;
			}
			case JFileChooser.CANCEL_OPTION: {
				// User Canceled File selection will be treated as application closed with normal exit
				System.exit(0);
				break;
			}
			case JFileChooser.ERROR_OPTION:
			default: {//the three cases above should cover every case but just to be sure
				//Error Occurred
				System.exit(1);
			}
			}

		}
		
		final File finFile = tmpFile;
		
		ElkNode graph = loader.stream().filter(load->load.getFileFilter().accept(finFile)).flatMap(load->load.loadGraphFromFile(finFile).stream()).findFirst().orElse(null);

		if(graph==null) {
			System.exit(2);
		}
		
		JFrame frame = new JFrame("Cycle Break Animation");
		
		IController controller = new Control();

		MainPanel mainPanel = new MainPanel(graph,controller);

		frame.setLayout(new BorderLayout());

		frame.add(mainPanel, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.pack();

	}

}
