package de.webtwob.agd.project.test.algorith.force;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.LinkedList;

import javax.swing.JPanel;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.AnimationSyncThread;
import de.webtwob.agd.project.api.GraphState;
import de.webtwob.agd.project.api.interfaces.IAlgorithm;
import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.api.util.GraphStateListBuilder;
import de.webtwob.agd.project.api.util.InitialLayoutUtil;
import de.webtwob.agd.project.view.AnimatedView;
import de.webtwob.agd.project.view.CompoundAnimation;

public class ForceTestAlgorithm implements IAlgorithm {

	@Override
	public IAnimation getAnimationPanel(JPanel panel, ElkNode graph, AnimationSyncThread syncThread) {
		panel.setLayout(new BorderLayout());

		LinkedList<GraphState> steps = new LinkedList<>();

		var builder = GraphStateListBuilder.createBuilder().startWith(graph).atLine("line0");
		
		InitialLayoutUtil.setForceLayoutAlgorithm(graph);
		InitialLayoutUtil.layout(graph);	
		
		builder.atLine("line0").updateNode(graph);
		
		steps.addAll(builder.getList());
		
		IAnimation anim = new CompoundAnimation(graph, steps, 500);

		var animView = new AnimatedView(syncThread);
		animView.setAnimation(anim);

		panel.setPreferredSize(new Dimension((int) Math.ceil(anim.getWidth()), (int) Math.ceil(anim.getHeight())));

		panel.add(animView, BorderLayout.CENTER);
		panel.repaint();

		return anim;
	}

	@Override
	public String getPseudoCode() {
		return "<code id='line0'>applyForceDirectedLayoutAlgorithm(graph);</code>";
	}

	@Override
	public String getName() {
		return "Test Algorithm, Force Directed";
	}

}
