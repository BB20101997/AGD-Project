package de.webtwob.agd.project.view.panel;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import de.webtwob.agd.project.api.interfaces.IAlgorithm;
import de.webtwob.agd.project.api.interfaces.IController;

public class ControllPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Map<String, IAlgorithm> algorithms = new HashMap<>();

	static {
		// load all algorithms into the algorithm map
		ServiceLoader.load(IAlgorithm.class).forEach(alg -> algorithms.put(alg.getName(), alg));
	}

	private MainPanel mainPanel;

	private JComboBox<String> algorithmChoises = new JComboBox<>(algorithms.keySet().toArray(new String[0]));

	public ControllPanel(MainPanel mainPanel, IController controller) {

		this.mainPanel = mainPanel;

		setBackground(Color.YELLOW);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(algorithmChoises);

		algorithmChoises.addItemListener(this::algorithmChangeEvent);

		if (!algorithms.isEmpty()) {
			// if an implementation is found default to the first one
			algorithmChoises.setSelectedIndex(0);
			mainPanel.setAlgorithm(algorithms.get(algorithmChoises.getSelectedItem()));
		}

	}

	private void algorithmChangeEvent(ItemEvent event) {
		mainPanel.setAlgorithm(algorithms.get(event.getItem()));
		// TODO update other stuff
	}

}
