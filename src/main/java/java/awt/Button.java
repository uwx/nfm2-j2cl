package java.awt;

import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import java_impl.HTMLComponent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static elemental2.dom.DomGlobal.document;

public class Button extends Component implements HTMLComponent {

	ActionListener actionListener;
	String actionCommand;
	String label;
	Color background;

	public Button(String label) {
		this.label = label;
		this.actionCommand = label;
	}

	@Override
	public HTMLButtonElement getHTMLElement() {
		return (HTMLButtonElement) super.getHTMLElement();
	}

	@Override
	public void createHTML() {
		if (super.htmlElement != null) {
			return;
		}
		super.htmlElement = (HTMLElement) document.createElement("button");
	}

	@Override
	public void initHTML() {
		super.initHTML();
		super.htmlElement.innerHTML = label;
		initActionListener();
	}

	@Override
	public void doPaintInternal() {
	}

	private void initActionListener() {
		if (actionListener != null) {
			super.htmlElement.onclick = e -> {
				this.actionListener.actionPerformed(new ActionEvent(this, 0, actionCommand));
				return e;
			};
		}
	}

	public void addActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
		if (super.htmlElement != null) {
			initActionListener();
		}
	}

	public final void setBackground(Color background) {
		this.background = background;
	}

}
