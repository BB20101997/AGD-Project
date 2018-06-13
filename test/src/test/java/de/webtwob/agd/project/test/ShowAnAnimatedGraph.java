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
import de.webtwob.agd.project.api.util.ViewUtil;
import de.webtwob.agd.project.view.AnimatedView;

public class ShowAnAnimatedGraph {

	public static void main(String[] args) {

		AnimationSyncThread syncThread = new AnimationSyncThread();

		AnimatedView sgv = new AnimatedView(syncThread);

		sgv.setMinimumSize(new Dimension(400, 400));
		sgv.setPreferredSize(new Dimension(400, 400));

		ElkNode start = GraphLoaderHelper.loadGraph(new File("src/test/resources/animationTestStart.json"))
				.orElse(null);
		ElkNode end = GraphLoaderHelper.loadGraph(new File("src/test/resources/animationTestEnd.json")).orElse(null);

		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.add(sgv, BorderLayout.CENTER);
		frame.setTitle("Loads predefined Json Files and displayes an Animated Graph!");
		frame.setVisible(true);
		frame.pack();

		syncThread.setLoopAction(LoopEnum.REVERSE);
		syncThread.start();

		sgv.animateGraph(start, ViewUtil.createMapping(start, end), 20000);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

}
