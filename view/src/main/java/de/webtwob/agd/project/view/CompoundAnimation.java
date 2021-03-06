package de.webtwob.agd.project.view;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.api.GraphState;
import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.api.interfaces.IVerbosity;
import de.webtwob.agd.project.api.util.Pair;

/**
 * An Animation for concatenating IAnimations
 * */
public class CompoundAnimation implements IAnimation {

	List<IAnimation> animationList = new ArrayList<>();
	IAnimation currentAnimation;
	List<Long> animationStarts = new ArrayList<>();
	List<Long> animationEnds = new ArrayList<>();
	long from = 0;
	long to = 0;

	// animation can be ~300 Million Years Long at normal speed
	// if an animation needs to be longer we still can treat long as unsigned
	// if that is still not enough we would need to change to BitInteger or
	// something
	// but for now signed long should suffice
	long totalLength = 0;
	private double width;
	private double height;

	/**
	 *  A Factory for IAnimation Obejct
	 */
	public interface IAnimationFactory {
		/**
		 * @param node the root node of the graph to be animated
		 * @param states the start and end state for the animation
		 * @param length the length in milliseconds at speed 1
		 * @return the resulting IAnimation
		 */
		public IAnimation createAnimation(ElkNode node, Pair<GraphState> states, int length);
	}

	/**
	 * Creates an empty Compound Animation
	 */
	public CompoundAnimation() {

	}

	/**
	 * @param root     the graph to animate
	 * @param mappings a List of configurations for the graph
	 * @param length   the frames between each state
	 *
	 *                 Creates a CompoundAnimation containing equal long animation
	 *                 of the passed mappings, each animation is length long
	 */
	public CompoundAnimation(ElkNode root, List<GraphState> mappings, int length) {
		this(root, mappings, length, Animation::new);
	}

	/**
	 * @param root     the graph to animate
	 * @param mappings a List of configurations for the graph
	 * @param length   the frames between each state
	 *
	 *                 Creates a CompoundAnimation containing equal long animation
	 *                 of the passed mappings, each animation is length long
	 * 
	 * @param factory  the factory for the containing Animations
	 */
	public CompoundAnimation(ElkNode root, List<GraphState> mappings, int length, IAnimationFactory factory) {
		if (mappings.size() == 1) {
			// only one mapping
			addAnimation(factory.createAnimation(root, new Pair<>(mappings.get(0), mappings.get(0)), length));
		} else if (!mappings.isEmpty()) {
			// more than one mapping
			for (int i = 1; i < mappings.size(); i++) {
				addAnimation(factory.createAnimation(root, new Pair<>(mappings.get(i - 1), mappings.get(i)), length));
			}
		}
	}

	/**
	 * @param anim the animation to add at the end
	 */
	public void addAnimation(IAnimation anim) {
		animationList.add(anim);
		// add new start and end
		animationStarts.add(totalLength);
		animationEnds.add(totalLength + anim.getLength());
		// update total length
		totalLength += anim.getLength();

		// update dimensions
		width = Math.max(width, anim.getWidth());
		height = Math.max(height, anim.getHeight());

	}

	@Override
	public void generateFrame(long frame, Graphics2D graphic) {
		if (frame < from || frame > to) {
			IntStream.range(0, animationList.size())
					.filter(i -> animationStarts.get(i) <= frame && animationEnds.get(i) >= frame).findFirst()
					.ifPresent(i -> {
						currentAnimation = animationList.get(i);
						from = animationStarts.get(i);
						to = animationEnds.get(i);
					});
		}
		if (currentAnimation != null) {
			currentAnimation.generateFrame(frame - from, graphic);
		}

	}

	@Override
	public long getLength() {
		return totalLength;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public GraphState getGraphStatesForFrame(long frame) {
		var start = this.from;
		var animation = this.currentAnimation;
		if (frame < start || frame > to) {
			var opt = IntStream.range(0, animationList.size())
					.filter(i -> animationStarts.get(i) <= frame && animationEnds.get(i) >= frame).findFirst();
			if (opt.isPresent()) {
				var i = opt.getAsInt();
				animation = animationList.get(i);
				start = animationStarts.get(i);
			}
		}
		if (animation != null) {
			return animation.getGraphStatesForFrame(frame - start);
		}
		return null;
	}

	@Override
	public OptionalLong nextStep(long frame, boolean forward, IVerbosity verbosity) {
		LongStream stream;
		if(forward) {
			stream = LongStream.range(0, animationList.size()) //need a LongStream for the flatMap
					.dropWhile(index->animationEnds.get((int)index)<frame);
					
		}else {
			stream = LongStream.iterate(animationList.size()-1L, l->l>=0, l->l-1) //need a LongStream for the flatMap
					.dropWhile(index->animationStarts.get((int)index)>=frame);
					
		}
		
		return stream.flatMap(i-> this.nextStepForIndex((int)i, frame, forward, verbosity).stream())
				.findFirst();
		
	}
	
	private OptionalLong nextStepForIndex(int i, long frame, boolean forward, IVerbosity verbosity) {
		var offset = animationStarts.get(i);
		return animationList.get(i).nextStep(frame-offset, forward, verbosity).stream().map(l->l+offset).findAny();
	}

}
