package de.webtwob.agd.project.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.webtwob.agd.project.api.util.GraphLoaderHelper;
import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.AnimationSyncThread;
import de.webtwob.agd.project.api.LoopEnum;
import de.webtwob.agd.project.api.util.GraphState;
import de.webtwob.agd.project.api.util.InitialLayoutUtil;
import de.webtwob.agd.project.api.util.Pair;
import de.webtwob.agd.project.api.util.ViewUtil;
import de.webtwob.agd.project.view.AnimatedView;
import de.webtwob.agd.project.view.Animation;
import de.webtwob.agd.project.view.CompoundAnimation;

public class AnimationElkForce {

	public static void main(String[] args) {

		AnimationSyncThread syncThread = new AnimationSyncThread();

		AnimatedView sgv = new AnimatedView(syncThread);

		sgv.setMinimumSize(new Dimension(400, 400));
		sgv.setPreferredSize(new Dimension(400, 400));

		ElkNode start = GraphLoaderHelper.loadGraph(new File("../main/src/main/resources/example.json")).orElse(null);

		InitialLayoutUtil.setForceLayoutAlgorithm(start);

		var mapping = new Pair<GraphState>(GraphState::new);

		ViewUtil.saveState(start, mapping.getStart());

		InitialLayoutUtil.layout(start);

		ViewUtil.saveState(start, mapping.getEnd());

		var pause = new GraphState();

		ViewUtil.saveState(start, pause);

		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.add(sgv, BorderLayout.CENTER);
		frame.setTitle("Loads predefined Json Files and displayes an Animated Graph!");
		frame.setVisible(true);
		frame.pack();

		CompoundAnimation comAnim = new CompoundAnimation();

		comAnim.addAnimation(new Animation(start, mapping, 2000));
		comAnim.addAnimation(new Animation(start, new Pair<>(pause,pause), 500));

		syncThread.setLoopAction(LoopEnum.LOOP);
		syncThread.start();

		sgv.setAnimation(comAnim);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

}
