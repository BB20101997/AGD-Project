package de.webtwob.agd.project.view.panel;

import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.webtwob.agd.project.api.AnimationSyncThread;
import de.webtwob.agd.project.api.LoopEnum;
import de.webtwob.agd.project.api.events.AnimationSpeedUpdateEvent;
import de.webtwob.agd.project.api.interfaces.IAlgorithm;
import de.webtwob.agd.project.api.interfaces.IAnimationEventHandler;

public class ControllPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Map<String, IAlgorithm> algorithms = new HashMap<>();

	private AnimationSyncThread syncThread;

	static {
		// load all algorithms into the algorithm map
		ServiceLoader.load(IAlgorithm.class).forEach(alg -> algorithms.put(alg.getName(), alg));
	}

	private MainPanel mainPanel;

	private JComboBox<String> algChoise;
	private JComboBox<LoopEnum> loopChoise;
	private JButton play;
	private JButton reversedPlay;
	private JButton pause;
	private JFormattedTextField speedField;

	private IAnimationEventHandler speedUpdate = e -> {
		if (e instanceof AnimationSpeedUpdateEvent) {
			speedField.setText(Double.toString(((AnimationSpeedUpdateEvent) e).getSpeed()));
		}
	};

	public ControllPanel() {

		// setBackground(Color.YELLOW);

		var boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);

		setLayout(boxLayout);

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// setup elements
		algChoise = new JComboBox<>(algorithms.keySet().toArray(new String[0]));
		algChoise.setPreferredSize(
				new Dimension(algChoise.getPreferredSize().width, (int) algChoise.getMinimumSize().height));

		loopChoise = new JComboBox<>(LoopEnum.values());
		loopChoise.setSelectedItem(LoopEnum.LOOP);

		reversedPlay = new JButton("\u23F4");

		play = new JButton("\u23F5");
		play.setMinimumSize(new Dimension(100, 100));

		pause = new JButton("\u23F8");

		speedField = new JFormattedTextField(NumberFormat.getNumberInstance());
		speedField.setValue(1);
		speedField.addActionListener(e -> mainPanel.getSyncThread().setSpeed(Double.parseDouble(speedField.getText())));

		// create boxes
		var algBox = Box.createHorizontalBox();
		var loopChoiseBox = Box.createHorizontalBox();
		var actionBox = Box.createHorizontalBox();
		var speedBox = Box.createHorizontalBox();

		// fill boxes
		algBox.add(algChoise);

		loopChoiseBox.add(loopChoise);

		actionBox.add(reversedPlay);
		actionBox.add(play);
		actionBox.add(pause);

		speedBox.add(new JLabel("Speed"));
		speedBox.add(speedField);

		// add boxes to panel
		add(algBox);
		add(loopChoiseBox);
		add(speedBox);
		add(actionBox);

		// add ActionListeners

		reversedPlay.addActionListener(event -> {
			if (syncThread != null) {
				syncThread.setSpeed(-Math.abs(mainPanel.getSyncThread().getSpeed()));
				syncThread.setPaused(false);

			}
		});

		play.addActionListener(event -> {
			if (syncThread != null) {
				syncThread.setSpeed(Math.abs(mainPanel.getSyncThread().getSpeed()));
				syncThread.setPaused(false);
			}
		});

		pause.addActionListener(event -> {
			if (syncThread != null) {
				syncThread.setPaused(true);
			}
		});

		loopChoise.addItemListener(event -> {
			mainPanel.setLoopType((LoopEnum) event.getItem());
		});

		algChoise.addItemListener(event -> mainPanel.setAlgorithm(algorithms.get(event.getItem())));

		if (!algorithms.isEmpty()) {
			// if an implementation is found default to the first one
			algChoise.setSelectedIndex(0);
		}

	}

	public void setMainPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
		if (mainPanel != null && !algorithms.isEmpty()) {
			mainPanel.setAlgorithm(algorithms.get(algChoise.getSelectedItem()));
		}
	}

	public void setSyncThread(AnimationSyncThread thread) {
		if (syncThread != null) {
			syncThread.unsubscribeFromAnimationEvent(speedUpdate);
		}
		syncThread = thread;
		if (syncThread != null) {
			syncThread.subscribeToAnimationEvent(speedUpdate);
		}
	}

}
