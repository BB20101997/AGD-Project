package de.webtwob.agd.project.main;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;

import de.webtwob.agd.project.api.util.GraphLoaderHelper;
import de.webtwob.agd.project.control.Control;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.interfaces.IController;
import de.webtwob.agd.project.view.panel.MainPanel;

public class Main {

	@SuppressWarnings("squid:S1066") // not collapsing if statement in case we want to add more commandline parameter
	public static void main(String[] args) {

		File tmpFile = null;

		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				if (args[i].equals("-file") && i + 1 < args.length) {
					tmpFile = new File(args[i + 1]);
				}
			}
		}

		ElkNode graph;

		if (tmpFile != null) {
			// try to load file passed via the command line
			graph = GraphLoaderHelper.loadGraph(tmpFile).orElse(null);
		} else {
			// ask the user for a file and load it
			graph = GraphLoaderHelper.loadGraph().orElse(null);
		}

		if (graph == null) {
			System.exit(2);
		}

		JFrame frame = new JFrame("Cycle Break Animation");

		IController controller = new Control();

		MainPanel mainPanel = new MainPanel(graph, controller);

		frame.setLayout(new BorderLayout());

		frame.add(mainPanel, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.pack();

	}

}
