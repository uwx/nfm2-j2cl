package javax.swing;

import elemental2.core.JsArray;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static elemental2.dom.DomGlobal.document;
import static java_impl.Iter.iter;

public class JButton extends AbstractButton {

	JsArray<ActionListener> actionListeners = new JsArray<>();
	String actionCommand;
	String label;
	Color background;
	ImageIcon icon;

	public JButton(String label) {
		this(label, null);
	}

	public JButton(String label, ImageIcon icon) {
		this.label = label;
		this.actionCommand = label;
		this.icon = icon;

		this.actionListener = actionEvent -> {
			for (ActionListener listener : iter(actionListeners)) {
				listener.actionPerformed(actionEvent);
			}
		};
	}

	@Override
	public HTMLButtonElement getHTMLElement() {
		return (HTMLButtonElement) super.getHTMLElement();
	}

	@Override
	public void createHTML() {
		if (htmlElement != null) {
			return;
		}
		htmlElement = (HTMLElement) document.createElement("button");
	}

	@Override
	public void initHTML() {
		super.initHTML();
		if (icon != null)
			getHTMLElement().appendChild(icon.getInternalHTMLImageElement());

		if (!label.isEmpty())
			htmlElement.innerHTML = label;

		initActionListener();
	}

	private void initActionListener() {
		htmlElement.onclick = e -> {
			this.actionListener.actionPerformed(new ActionEvent(this, 0, actionCommand));
			return e;
		};
	}

	public void addActionListener(ActionListener actionListener) {
		actionListeners.unshift(actionListener);
	}


	public void removeActionListener(ActionListener actionListener) {
		int index = actionListeners.indexOf(actionListener);
		if (index > -1) {
			actionListeners = actionListeners.splice(index, 1);
		}
	}

	public final void setBackground(Color background) {
		this.background = background;
	}
}
