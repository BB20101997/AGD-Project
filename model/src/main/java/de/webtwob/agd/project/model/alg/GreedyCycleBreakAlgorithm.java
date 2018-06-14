package de.webtwob.agd.project.model.alg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.AnimationSyncThread;
import de.webtwob.agd.project.api.interfaces.IAlgorithm;
import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.api.util.GraphState;
import de.webtwob.agd.project.api.util.InitialLayoutUtil;
import de.webtwob.agd.project.model.Model;
import de.webtwob.agd.project.view.AnimatedView;
import de.webtwob.agd.project.view.CompoundAnimation;
import de.webtwob.agd.project.view.PseudocodeView;

public class GreedyCycleBreakAlgorithm implements IAlgorithm {

	@Override
	public AnimationSyncThread getAnimationPanel(JPanel panel, ElkNode graph) {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		//apply the force layout algorithm to get an initial layout
		InitialLayoutUtil.setForceLayoutAlgorithm(graph);
		InitialLayoutUtil.layout(graph);

		LinkedList<GraphState> steps = new LinkedList<>();

		Model.getSteps(graph, steps);

		AnimationSyncThread syncThread = new AnimationSyncThread();

		IAnimation anim = new CompoundAnimation(graph, steps, 500);

		List<String> lines = java.util.Collections.emptyList();

		try {
			var uri = getClass().getResource("de/webtwob/agd/project/model/alg/GreedyCycleBreakPseudoCode.txt");

			if (uri == null) {
				uri = new File(
						"../model/src/main/resources/de/webtwob/agd/project/model/alg/GreedyCycleBreakPseudoCode.txt")
								.toURI().toURL();
				// System.out.println(uri.getFile());
			}

			var resStream = uri.openStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(resStream));
			lines = reader.lines().collect(Collectors.toList());
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		var animationBox = Box.createHorizontalBox();
		var pseudoView = new PseudocodeView(lines, syncThread, anim);
		var animView = new AnimatedView(syncThread);

		animView.setAnimation(anim);

		animationBox.add(pseudoView);
		animationBox.add(animView);

		panel.add(animationBox);
		var slider = new JSlider();
		slider.setMaximum((int) syncThread.getEndAnimationAt());
		slider.setMajorTickSpacing(500);
		slider.setPaintTicks(true);
		syncThread.addFrameChangeCallback(() -> {
			var val = syncThread.getFrame();
			if (slider.getValue() != val) {
				slider.setValue((int) val);
			}
		});
		slider.addChangeListener(event -> syncThread.setFrame(slider.getValue()));
		panel.add(slider);

		panel.repaint();

		return syncThread;

	}

	@Override
	public String getName() {
		return "Cyclebreak Greedy";
	}

}
