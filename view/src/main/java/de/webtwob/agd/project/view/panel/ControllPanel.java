package de.webtwob.agd.project.view.panel;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.webtwob.agd.project.api.ControllerModel;
import de.webtwob.agd.project.api.enums.Direction;
import de.webtwob.agd.project.api.enums.LoopEnum;
import de.webtwob.agd.project.api.enums.VerbosityEnum;
import de.webtwob.agd.project.api.events.AnimationSpeedUpdateEvent;
import de.webtwob.agd.project.api.interfaces.IAlgorithm;
import de.webtwob.agd.project.api.interfaces.IAnimationEventHandler;
import de.webtwob.agd.project.api.util.AlgorithmLoaderHelper;

/**
 * The JPanel holding the ANimation Controlls
 */
public class ControllPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Map<String, IAlgorithm> algorithms = new HashMap<>();

	private transient ControllerModel model;

	static {
		// load all algorithms into the algorithm map
		AlgorithmLoaderHelper.getAlgorithms().forEach(alg -> algorithms.put(alg.getName(), alg));
	}

	private MainPanel mainPanel;

	private JComboBox<String> algChoise;
	private JComboBox<LoopEnum> loopChoise;
	private JComboBox<VerbosityEnum> verbosChoise;

	private JButton play;
	private JButton reversedPlay;
	private JButton pause;
	private JButton stepForward;
	private JButton stepBackward;
	private JFormattedTextField speedField;

	private transient IAnimationEventHandler speedUpdate = e -> {
		if (e instanceof AnimationSpeedUpdateEvent) {
			speedField.setText(Double.toString(((AnimationSpeedUpdateEvent) e).getSpeed()));
		}
	};

	/**
	 * Createt new Controll Panel
	 */
	public ControllPanel() {

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// setup elements
		algChoise = new JComboBox<>(algorithms.keySet().toArray(new String[0]));
		algChoise.setPreferredSize(
				new Dimension(algChoise.getPreferredSize().width, algChoise.getMinimumSize().height));

		loopChoise = new JComboBox<>(LoopEnum.values());
		loopChoise.setSelectedItem(LoopEnum.LOOP);

		verbosChoise = new JComboBox<>(VerbosityEnum.values());
		verbosChoise.setSelectedItem(VerbosityEnum.OFF);

		
		stepForward = new JButton("\u23ED");
		stepBackward = new JButton("\u23EE");
		reversedPlay = new JButton("\u23F4");
		play = new JButton("\u23F5");
		pause = new JButton("\u23F8");

		var format = DecimalFormat.getInstance(Locale.ENGLISH);
		format.setParseIntegerOnly(false);
		
		speedField = new JFormattedTextField(format);
		speedField.setValue(1);

		// create boxes
		var algBox = Box.createHorizontalBox();
		var loopChoiseBox = Box.createHorizontalBox();
		var verbosChoiseBox = Box.createHorizontalBox();
		var actionBox = Box.createHorizontalBox();
		var speedBox = Box.createHorizontalBox();

		// fill boxes
		algBox.add(new JLabel("Algorithm:"));
		algBox.add(algChoise);

		loopChoiseBox.add(new JLabel("After Animation:"));
		loopChoiseBox.add(loopChoise);
		
		verbosChoiseBox.add(new JLabel("Verbosity:"));
		verbosChoiseBox.add(verbosChoise);

		actionBox.add(stepBackward);
		actionBox.add(reversedPlay);
		actionBox.add(pause);
		actionBox.add(play);
		actionBox.add(stepForward);

		speedBox.add(new JLabel("Speed:"));
		speedBox.add(speedField);

		// add boxes to panel
		add(algBox);
		add(loopChoiseBox);
		add(verbosChoiseBox);
		add(speedBox);
		add(actionBox);

		// add ActionListeners

		speedField.addActionListener(e -> {
			var speed = Double.parseDouble(speedField.getText());
			model.setSpeed(speed);
			
		});
		
		stepForward.addActionListener(event -> model.step(Direction.FORWARD));
		stepBackward.addActionListener(event -> model.step(Direction.BACKWARD));

		reversedPlay.addActionListener(event -> {
			if (model != null) {
				model.playContinuosly();
				model.setDirection(Direction.BACKWARD);

			}
		});

		play.addActionListener(event -> {
			if (model != null) {
				model.playContinuosly();
				model.setDirection(Direction.FORWARD);
			}
		});

		pause.addActionListener(event -> {
			if (model != null) {
				model.setDirection(Direction.PAUSE);
			}
		});
		
		
		loopChoise.addItemListener(event -> model.setLoopAction((LoopEnum) event.getItem()));
		
		verbosChoise.addItemListener(event -> model.setVerbosity((VerbosityEnum) event.getItem()));

		algChoise.addItemListener(event -> mainPanel.setAlgorithm(algorithms.get(event.getItem())));

		if (!algorithms.isEmpty()) {
			// if an implementation is found default to the first one
			algChoise.setSelectedIndex(0);
		}

	}
	

	/**
	 * @param mainPanel the main Panel to inform about changes 
	 */
	public void setMainPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
		if (mainPanel != null && !algorithms.isEmpty()) {
			mainPanel.setAlgorithm(algorithms.get(algChoise.getSelectedItem()));
		}
	}

	/**
	 * @param thread the model to be used from now on
	 * 
	 * */
	public void setModel(ControllerModel thread) {
		if (model != null) {
			model.unsubscribeFromAnimationEvent(speedUpdate);
		}
		model = thread;
		if (model != null) {
			model.subscribeToAnimationEvent(speedUpdate);
			loopChoise.setSelectedItem(model.getLoopAction());
		}
	}
	
	

}
