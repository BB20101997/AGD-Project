package de.webtwob.agd.project.main;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import de.webtwob.agd.project.api.util.GraphLoaderHelper;
import de.webtwob.agd.project.view.panel.MainPanel;

public class Main {

	/**
	 * This is the beginning of everything
	 * */
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

		JFrame frame = new JFrame("Cycle Break Animation");

		MainPanel mainPanel = new MainPanel();
		
		if (tmpFile != null) {
			// try to load file passed via the command line
			GraphLoaderHelper.loadGraph(tmpFile).ifPresent(mainPanel::setGraph);
		}

		var menuBar = new JMenuBar();
		var fileMenu = new JMenu("File");
		var loadButton = new JMenuItem("Load");

		loadButton.addActionListener(e -> GraphLoaderHelper.loadGraph().ifPresent(mainPanel::setGraph));

		fileMenu.add(loadButton);
		fileMenu.add(mainPanel.getSaveMenuItem());
		menuBar.add(fileMenu);
		frame.setJMenuBar(menuBar);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.setLayout(new BorderLayout());

		frame.add(mainPanel, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.pack();

	}

}
