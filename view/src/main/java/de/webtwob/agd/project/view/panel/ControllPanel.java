package de.webtwob.agd.project.view.panel;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import de.webtwob.agd.project.api.LoopEnum;
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

		// setBackground(Color.YELLOW);

		var boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);

		setLayout(boxLayout);

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		var algChoise = Box.createHorizontalBox();

		algChoise.add(algorithmChoises);

		add(algChoise);

		var loopChoiseBox = Box.createHorizontalBox();

		var loopChoiseCombo = new JComboBox<>(LoopEnum.values());

		loopChoiseCombo.setSelectedItem(LoopEnum.LOOP);

		loopChoiseBox.add(loopChoiseCombo);

		add(loopChoiseBox);

		var actionBox = Box.createHorizontalBox();

		var playButton = new JButton("\u23F5");
		var pauseButton = new JButton("\u23F8");

		playButton.setMinimumSize(new Dimension(100, 100));

		actionBox.add(playButton);
		actionBox.add(pauseButton);

		add(actionBox);

		playButton.addActionListener(event -> {
			mainPanel.getSyncThread().setPaused(false);
		});

		pauseButton.addActionListener(event -> {
			mainPanel.getSyncThread().setPaused(true);
		});

		loopChoiseCombo.addItemListener(event -> {
			mainPanel.setLoopType((LoopEnum) event.getItem());
		});

		algorithmChoises.addItemListener(this::algorithmChangeEvent);

		if (!algorithms.isEmpty()) {
			// if an implementation is found default to the first one
			algorithmChoises.setSelectedIndex(0);
			mainPanel.setAlgorithm(algorithms.get(algorithmChoises.getSelectedItem()));
		}

	}

	private void algorithmChangeEvent(ItemEvent event) {
		mainPanel.setAlgorithm(algorithms.get(event.getItem()));
	}

}
