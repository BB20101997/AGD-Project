package de.webtwob.agd.project.view;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import de.webtwob.agd.project.api.interfaces.IAnimation;

public class CompoundAnimation implements IAnimation {


	private double width;
	private double height;
	
	List<IAnimation> animationList = new ArrayList<>();

	IAnimation currentAnimation;

	List<Long> animationStarts = new ArrayList<>();
	List<Long> animationEnds = new ArrayList<>();

	long from = 0, to = 0;

	// animation can be ~300 Million Years Long at normal speed
	//if an animation needs to be longer we still can treat long as unsigned 
	// if that is still not enough we would need to change to BitInteger or something
	//but for now signed long should suffice
	long totalLength = 0;

	public CompoundAnimation() {

	}

	void addAnimation(IAnimation anim) {
		animationList.add(anim);
		//add new start and end
		animationStarts.add(totalLength);
		animationEnds.add(totalLength+anim.getLength());
		//update total length
		totalLength += anim.getLength();
		
		//update dimensions
		width = Math.max(width, anim.getWidth());
		height = Math.max(height, anim.getHeight());
		
	}

	@Override
	public void generateFrame(long frame, Graphics2D graphic) {
		if (frame < from || frame > to) {
			IntStream.range(0, animationList.size())
					.filter(i -> animationStarts.get(i) <= frame && animationEnds.get(i) >= frame).findFirst().ifPresent(i -> {
						currentAnimation = animationList.get(i);
						from = animationStarts.get(i);
						to = animationEnds.get(i);
					});
		}
		

		double width = graphic.getClipBounds().getWidth();
		double height = graphic.getClipBounds().getHeight();

		double scale = Math.min(width / getWidth(), height / getHeight());

		graphic.scale(scale, scale);
		
		Graphics2D subGraphic = (Graphics2D) graphic.create(0, 0,(int) currentAnimation.getWidth(), (int)currentAnimation.getHeight());
		
		currentAnimation.generateFrame(frame - from,subGraphic);
		
		subGraphic.dispose();

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

}
