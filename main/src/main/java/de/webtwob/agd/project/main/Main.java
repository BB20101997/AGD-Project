package de.webtwob.agd.project.main;

import java.awt.BorderLayout;
import java.io.File;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import de.webtwob.agd.project.api.IGraphLoader;
import de.webtwob.agd.project.view.panel.MainPanel;

public class Main {

	public static void main(String[] args) {

		File file = null;

		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				if (args[i].equals("-file") && i + 1 < args.length) {
					file = new File(args[i + 1]);
				}
			}
		}

		List<IGraphLoader> loader = ServiceLoader.load(IGraphLoader.class).stream().map(Provider::get)
				.collect(Collectors.toList());

		if (file == null) {
			JFileChooser chooser = new JFileChooser(".");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setDialogTitle("Select Graph to be animated!");
			chooser.setMultiSelectionEnabled(false);
			
			for (IGraphLoader load : loader) {
				chooser.addChoosableFileFilter(load.getFileFilter());
			}

			switch (chooser.showOpenDialog(null)) {
			case JFileChooser.APPROVE_OPTION: {
				file = chooser.getSelectedFile();
				break;
			}
			default: {
				// User Canceled the Dialog or an Error Occurred
				System.exit(0);
			}
			}

		}

		JFrame frame = new JFrame("Cycle Break Animation");

		MainPanel mainPanel = new MainPanel();

		frame.setLayout(new BorderLayout());

		frame.add(mainPanel, BorderLayout.CENTER);

		frame.setVisible(true);
		frame.pack();

	}

}
