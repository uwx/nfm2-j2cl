package j2ts_awt_swing.impl;

import elemental2.dom.HTMLElement;

public abstract class AbstractHTMLComponent implements HTMLComponent {

	HTMLElement htmlElement;

	@Override
	public void bindHTML(HTMLElement htmlElement) {
		this.htmlElement = htmlElement;
	}

	@Override
	public final HTMLElement getHTMLElement() {
		if (htmlElement == null) {
			createHTML();
			initHTML();
		}
		return htmlElement;
	}

	@Override
	public void initHTML() {
		if (htmlElement == null) {
			createHTML();
		}
	}

}
