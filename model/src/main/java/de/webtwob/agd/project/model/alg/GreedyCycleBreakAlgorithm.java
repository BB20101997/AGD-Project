package de.webtwob.agd.project.model.alg;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.AnimationSyncThread;
import de.webtwob.agd.project.api.interfaces.IAlgorithm;
import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.api.util.InitialLayoutUtil;
import de.webtwob.agd.project.model.Model;
import de.webtwob.agd.project.view.AnimatedView;
import de.webtwob.agd.project.view.CompoundAnimation;

public class GreedyCycleBreakAlgorithm implements IAlgorithm{

	@Override
	public AnimationSyncThread getAnimationPanel(JPanel panel ,ElkNode graph) {
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		InitialLayoutUtil.setForceLayoutAlgorithm(graph);
		InitialLayoutUtil.layout(graph);
		
		var steps = Model.getSteps(graph);
		
		AnimationSyncThread syncThread = new AnimationSyncThread();
		
		IAnimation anim = new CompoundAnimation(graph,steps, 1000);
		
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
