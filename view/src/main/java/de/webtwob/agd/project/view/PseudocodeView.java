package de.webtwob.agd.project.view;

import java.awt.Color;
import java.io.IOException;
import java.lang.Thread.State;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import de.webtwob.agd.project.api.AnimationSyncThread;
import de.webtwob.agd.project.api.events.AnimationUpdateEvent;
import de.webtwob.agd.project.api.events.IAnimationEvent;
import de.webtwob.agd.project.api.interfaces.IAnimation;
import de.webtwob.agd.project.api.interfaces.IAnimationEventHandler;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class PseudocodeView extends JTextPane {
	/**
	 * generated serialVarsionUID
	 */
	private static final long serialVersionUID = -6226316608311632721L;

	private final transient IAnimationEventHandler eventHandler = this::updateAnimation;
	private final HTMLDocument doc = new HTMLDocument();

	private transient AnimationSyncThread frameSync = new AnimationSyncThread();
	private transient IAnimation animation;

	public PseudocodeView() {
		setDoubleBuffered(true);
		this.setContentType("text/html");
		super.setDocument(doc);
		setEditable(false);
	}

	public PseudocodeView(String codeLines, AnimationSyncThread syncThread, IAnimation animation) {
		this();
		setText(codeLines);
		setAnimation(animation);
		setSyncThread(syncThread);
	}

	public PseudocodeView(String codeLines, IAnimation animation) {
		this(codeLines, new AnimationSyncThread(), animation);
		if (frameSync.getState() == State.NEW) {
			frameSync.start();
		}
	}

	@SuppressWarnings("exports") // automatic modules should not be exported
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

	public void setAnimation(IAnimation animation) {
		if (frameSync != null) {
			frameSync.removeAnimation(this.animation);
			frameSync.addAnimation(animation);
		}
		this.animation = animation;
	}

	public void setSyncThread(AnimationSyncThread thread) {
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
		if (animation == null || frameSync == null) {
			return;
		}
		if (event instanceof AnimationUpdateEvent) {
			var updateEvent = (AnimationUpdateEvent) event;
			var line = animation.getGraphStatesForFrame(updateEvent.getFrame()).getPseudoCodeLine();
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
