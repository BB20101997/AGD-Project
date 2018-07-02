package de.webtwob.agd.project.view;

import de.webtwob.agd.project.api.ControllerModel;
import de.webtwob.agd.project.api.GraphState;
import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.api.util.GraphStateUtil;
import de.webtwob.agd.project.api.util.Pair;
import org.eclipse.elk.graph.ElkNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

/**
 * A JComponent for displaying IAnimations
 * */
public class AnimatedView extends JComponent {

	/**
	 * generated serialVarsionUID
	 */
	private static final long serialVersionUID = -6226316608311632721L;

	/**
	 * if true scale won't be changed by scrolling and origin won't be changed by
	 * dragging
	 */
	private boolean fixed = false;

	// variables determining the position and size of animation
	private volatile double scale = 1;
	private volatile Point2D.Double origin = new Point2D.Double(0, 0);

	private transient volatile IAnimation animation;

	private transient ControllerModel model;

	/**
	 * @param syncThread the ControllerModel to be used by this AnmatedView
	 */
	public AnimatedView(ControllerModel syncThread) {
		setDoubleBuffered(true);
		setBackground(Color.WHITE);

		model = syncThread;

		model.subscribeToAnimationEvent(e -> this.repaint());

		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);

		MouseAdapter adapter = new MouseAdapter() {

			Point mouseClick = new Point();
			Point2D.Double oldOrigin = new Point2D.Double();

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (fixed) {
					return;
				}
				var oldScale = scale;
				/*
				 * increase/decrease by smaller amounts
				 */
				scale = scale + scale * e.getUnitsToScroll() / 32;
				if (scale < 1) {
					scale = 1;
				}
				
				var scaleFactor = scale/oldScale;
								
				//update the origin so that the same point is under the mouse before and after zooming
				origin.setLocation(e.getPoint().getX()-(e.getPoint().getX()-origin.getX())*scaleFactor,e.getPoint().getY()-(e.getPoint().getY()-origin.getY())*scaleFactor);
				//this makes zooming and drag and drop simultaneous behave better
				oldOrigin.setLocation(e.getPoint().getX()-(e.getPoint().getX()-oldOrigin.getX())*scaleFactor,e.getPoint().getY()-(e.getPoint().getY()-oldOrigin.getY())*scaleFactor);		
				repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (fixed) {
					return;
				}
				mouseClick.setLocation(e.getPoint());
				oldOrigin.setLocation(origin);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (fixed) {
					return;
				}
				origin = new Point2D.Double(oldOrigin.getX() + e.getX() - mouseClick.getX(),
						oldOrigin.getY() + e.getY() - mouseClick.getY());
				repaint();
			}

		};

		addMouseWheelListener(adapter);
		addMouseListener(adapter);
		addMouseMotionListener(adapter);
	}

	/**
	 * @param fixed should this not be zoom-able and pan-able
	 */
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}


	/**
	 * @param eg the graph to create a still Animation for
	 */
	public void setGraph(ElkNode eg) {
		setAnimation(new Animation(eg, GraphStateUtil.createMapping(eg, eg), 2));
		repaint();
	}

	/**
	 * @param graph
	 *            the graph that shall be animated
	 * @param mapping
	 *            the mappings for the end and starting configuration of the graph
	 * @param length
	 *            how many frames long shall the animation be at speed 1
	 *            <p>
	 *            If length is less or equal to 0 DEFAULT_ANIMATIN_LENGTH will be
	 *            used instead of length
	 */
	public void animateGraph(ElkNode graph, Pair<GraphState> mapping, int length) {
		setAnimation(new Animation(graph, mapping, length));
	}

	/**
	 * @param animation
	 *            the animation this View shall display
	 */
	public void setAnimation(IAnimation animation) {
		model.removeAnimation(this.animation);
		this.animation = animation;
		model.addAnimation(this.animation);
		if (animation != null) {
			var minDim = new Dimension((int) Math.ceil(animation.getWidth()), (int) Math.ceil(animation.getHeight()));
			setMinimumSize(minDim);
			var prefDim = getPreferredSize();
			setPreferredSize(new Dimension((int)Math.max(minDim.getWidth(), prefDim.getWidth()),(int)Math.max(minDim.getHeight(), prefDim.getHeight())));
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(getForeground());

		if (animation == null) {
			return;
		}
		Graphics2D graphic = (Graphics2D) g.create();

		graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphic.setRenderingHint(RenderingHints.KEY_RESOLUTION_VARIANT,
				RenderingHints.VALUE_RESOLUTION_VARIANT_DPI_FIT);
		graphic.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		graphic.translate(origin.getX(), origin.getY());
		var subScale = Math.min(getWidth() / animation.getWidth(), getHeight() / animation.getHeight());
		graphic.scale(subScale, subScale);
		graphic.scale(scale, scale);

		animation.generateFrame(model.getFrame(), graphic);

		graphic.dispose();
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

}
