package de.webtwob.agd.project.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.ControllerModel;
import de.webtwob.agd.project.api.GraphState;
import de.webtwob.agd.project.api.enums.LoopEnum;
import de.webtwob.agd.project.api.util.GraphLoaderHelper;
import de.webtwob.agd.project.api.util.GraphStateUtil;
import de.webtwob.agd.project.api.util.InitialLayoutUtil;
import de.webtwob.agd.project.api.util.Pair;
import de.webtwob.agd.project.view.AnimatedView;
import de.webtwob.agd.project.view.Animation;
import de.webtwob.agd.project.view.CompoundAnimation;

@SuppressWarnings("javadoc")
public class AnimationElkForce {

	public static void main(String[] args) {

		ControllerModel model = new ControllerModel();

		AnimatedView sgv = new AnimatedView(model);

		sgv.setMinimumSize(new Dimension(400, 400));
		sgv.setPreferredSize(new Dimension(400, 400));

		ElkNode start = GraphLoaderHelper.loadGraph(new File("../main/src/main/resources/example.json")).orElse(null);

		InitialLayoutUtil.setForceLayoutAlgorithm(start);

		var mapping = new Pair<GraphState>(GraphState::new);

		GraphStateUtil.saveState(start, mapping.getStart());

		InitialLayoutUtil.layout(start);

		GraphStateUtil.saveState(start, mapping.getEnd());

		var pause = new GraphState();

		GraphStateUtil.saveState(start, pause);

		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.add(sgv, BorderLayout.CENTER);
		frame.setTitle("Loads predefined Json Files and displayes an Animated Graph!");
		frame.setVisible(true);
		frame.pack();

		CompoundAnimation comAnim = new CompoundAnimation();

		comAnim.addAnimation(new Animation(start, mapping, 2000));
		comAnim.addAnimation(new Animation(start, new Pair<>(pause, pause), 500));

		model.setLoopAction(LoopEnum.LOOP);
		
		model.start();
		
		sgv.setAnimation(comAnim);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

}
