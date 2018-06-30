package de.webtwob.agd.project.view;

import java.awt.Color;
import java.io.IOException;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import de.webtwob.agd.project.api.ControllerModel;
import de.webtwob.agd.project.api.events.AnimationUpdateEvent;
import de.webtwob.agd.project.api.events.IAnimationEvent;
import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.api.interfaces.IAnimationEventHandler;

/**
 * A JTextPane pre-configured for displaying Pseudocode
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class PseudocodeView extends JTextPane {
	/**
	 * generated serialVarsionUID
	 */
	private static final long serialVersionUID = -6226316608311632721L;

	private final transient IAnimationEventHandler eventHandler = this::updateAnimation;
	private final HTMLDocument doc = new HTMLDocument();

	private transient ControllerModel frameSync = new ControllerModel();
	private transient IAnimation animation;

	/**
	 *  Create new default PseudoCodeView
	 */
	public PseudocodeView() {
		setDoubleBuffered(true);
		this.setContentType("text/html");
		super.setDocument(doc);
		setEditable(false);
	}

	/**
	 * @param codeLines the codeLines to be shown
	 * @param syncThread the syncThread to be used
	 * @param animation the animation to be used 
	 */
	public PseudocodeView(String codeLines, ControllerModel syncThread, IAnimation animation) {
		this();
		setText(codeLines);
		setAnimation(animation);
		setModel(syncThread);
	}

	/**
	 * @param codeLines the codeLines to be shown
	 * @param animation the animation to be used 
	 */
	public PseudocodeView(String codeLines, IAnimation animation) {
		this(codeLines, new ControllerModel(), animation);
		frameSync.start();
	}

	/**
	 * @param codeLines the new codeLines to be used
	 */
	public void setCode(String codeLines) {
		this.setContentType("text/html");
		try {
			doc.setOuterHTML(doc.getDefaultRootElement(), codeLines);
		} catch (BadLocationException | IOException ignore) {
			// we will just leave the view empty
		}
		this.setText(codeLines);
		repaint();
	}

	/**
	 * @param animation change the used Animation
	 */
	public void setAnimation(IAnimation animation) {
		if (frameSync != null) {
			frameSync.removeAnimation(this.animation);
			frameSync.addAnimation(animation);
		}
		this.animation = animation;
	}

	
	/**
	 * @param thread change the model to be used
 	 */
	public void setModel(ControllerModel thread) {
		if (frameSync != null) {
			frameSync.removeAnimation(animation);
			frameSync.unsubscribeFromAnimationEvent(eventHandler);
		}
		frameSync = thread;
		if (frameSync != null) {
			frameSync.addAnimation(animation);
			frameSync.subscribeToAnimationEvent(eventHandler);
		}
	}

	@Override
	public void setDocument(javax.swing.text.Document doc) {
		//we want to keep our HTMLDocument
	}

	private void updateAnimation(IAnimationEvent event) {
		if (animation == null || frameSync == null || event == null) {
			return;
		}
		if (event instanceof AnimationUpdateEvent) {
			var updateEvent = (AnimationUpdateEvent) event;
			var state = animation.getGraphStatesForFrame(updateEvent.getFrame());
			if(state==null)
				return;
			var line = state.getPseudoCodeLine();
			Element elem;
			if (line != null) {
				elem = doc.getElement(line);
			} else {
				elem = null;
			}
			getHighlighter().removeAllHighlights();
			if (elem != null) {
				try {
					getHighlighter().addHighlight(elem.getStartOffset(), elem.getEndOffset(),
							new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY));
				} catch (BadLocationException ignore) {
					// something went wrong so we just won't highlight anything
				}
			}
			repaint();
		}
	}

}
