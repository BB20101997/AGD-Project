package de.webtwob.agd.project.model.alg;

import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.AnimationSyncThread;
import de.webtwob.agd.project.api.interfaces.*;
import de.webtwob.agd.project.api.util.GraphMapping;
import de.webtwob.agd.project.api.util.InitialLayoutUtil;
import de.webtwob.agd.project.model.Model;
import de.webtwob.agd.project.view.AnimatedView;
import de.webtwob.agd.project.view.Animation;

public class GreedyCycleBreakAlgorithm implements IAlgorithm{

	@Override
	public AnimationSyncThread getAnimationPanel(JPanel panel ,ElkNode graph) {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		InitialLayoutUtil.setForceLayoutAlgorithm(graph);
		InitialLayoutUtil.layout(graph);
		
		Model.getSteps(graph, new LinkedList<>(), new LinkedList<>());
		
		AnimationSyncThread syncThread = new AnimationSyncThread();
		
		IAnimation anim = new Animation(graph,new GraphMapping()/**TODO*/,20/**TODO*/);
		
		var animView = new AnimatedView(syncThread);
		animView.setAnimation(anim);
		
		panel.add(animView);
		
		panel.repaint();
		
		return syncThread;
		
	}

	@Override
	public String getName() {
		return "Cyclebreak Greedy";
	}

}
