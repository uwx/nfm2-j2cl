package java.awt;

import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static elemental2.dom.DomGlobal.document;

public class TextField extends Component {

	ActionListener actionListener;

	public TextField(int cols) {
	}

	@Override
	public HTMLInputElement getHTMLElement() {
		return (HTMLInputElement) htmlElement;
	}
	
	@Override
	public void createHTML() {
		if (htmlElement != null) {
			return;
		}
		htmlElement = (HTMLElement) document.createElement("input");
		htmlElement.setAttribute("type", "text");
	}

	@Override
	public void initHTML() {
		super.initHTML();
		initActionListener();
	}

	private void initActionListener() {
		if (actionListener != null) {
			htmlElement.onclick = e -> {
				this.actionListener.actionPerformed(new ActionEvent(this, 0, null));
				return e;
			};
		}
	}

	public void addActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
		if (htmlElement != null) {
			initActionListener();
		}
	}

	public void setText(String text) {
		getHTMLElement().value = text;
	}

	public String getText() {
		return getHTMLElement().value;
	}

}
