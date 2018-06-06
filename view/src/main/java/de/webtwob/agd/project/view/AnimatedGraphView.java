package de.webtwob.agd.project.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.function.DoubleConsumer;

import javax.swing.JComponent;
import org.eclipse.elk.graph.ElkNode;

import de.webtwob.agd.project.service.util.GraphMapping;

public class AnimatedGraphView extends JComponent {

	/**
	 * generated serialVarsionUID
	 */
	private static final long serialVersionUID = -6226316608311632721L;
	
	long frame;
	double scale = 1;
	
	Point2D.Double origin = new Point2D.Double(0, 0);

	volatile Animation animation;

	BufferedImage currentFrame;
	double speed = 1; //milliseconds skipped per millisecond  
	Thread animationThread = new Thread(this::animate);

	LoopEnum end = LoopEnum.STOP;
	
	public AnimatedGraphView() {
		setDoubleBuffered(true);
		setBackground(Color.WHITE);
		animationThread.setDaemon(true);
		animationThread.setName("AnimationGraphViewThread");
		animationThread.start();
	}

	@SuppressWarnings("exports") // automatic modules should not be exported
	public void setGraph(ElkNode eg) {
		animation = new Animation(eg, GraphMapping.DummyMapping.getInstance(), 2);
		speed = 0;
		repaint();
	}

	/**
	 * @param length
	 *            how many frames long shall the animation be at speed 1
	 * 
	 *            If length is less or equal to 0 DEFAULT_ANIMATIN_LENGTH will be
	 *            used instead of length
	 */
	@SuppressWarnings("exports") // don't export automatic modules
	public void animateGraph(ElkNode graph, GraphMapping mapping, int length) {
		animation = new Animation(graph, mapping, length);
		speed = 1;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (animation == null)
			return;
		Graphics2D graphic = (Graphics2D) g.create();
		graphic.scale(scale, scale);
		graphic.translate(origin.getX(), origin.getY());

		currentFrame = animation.generateFrame(frame, currentFrame);
		if (currentFrame != null) {
			graphic.drawImage(currentFrame, 0, 0, getWidth(), getHeight(), 0, 0, currentFrame.getWidth(), currentFrame.getHeight(), null);
		}
		//graphic.dispose();
	}

	private void animate() {
		
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();
		
		while (true) {
			if (animation != null&&speed!=0) {
				frame += (end - start)*speed;
				start = System.currentTimeMillis();
				paintImmediately(0, 0, getWidth(), getHeight());
				if(frame<0||frame>animation.lengthInMills) {
					this.end.handle((DoubleConsumer)(nSpeed->this.speed=nSpeed),nFrame->this.frame=nFrame,animation.lengthInMills,frame,speed);
				}
				end = System.currentTimeMillis();
			}
		}
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setLoop(LoopEnum reverse) {
		end = reverse;
	}

}
