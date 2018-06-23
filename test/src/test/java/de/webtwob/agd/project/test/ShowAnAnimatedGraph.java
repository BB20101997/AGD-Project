package de.webtwob.agd.project.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.ControllerModel;
import de.webtwob.agd.project.api.enums.LoopEnum;
import de.webtwob.agd.project.api.util.GraphLoaderHelper;
import de.webtwob.agd.project.api.util.GraphStateUtil;
import de.webtwob.agd.project.view.AnimatedView;

public class ShowAnAnimatedGraph {

	public static void main(String[] args) {

		ControllerModel model = new ControllerModel();

		AnimatedView sgv = new AnimatedView(model);

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

		model.setLoopAction(LoopEnum.REVERSE);

		model.start();

		sgv.animateGraph(start, GraphStateUtil.createMapping(start, end), 20000);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

}
