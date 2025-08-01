package java_impl;

import elemental2.dom.HTMLElement;

public interface HTMLComponent {

	HTMLElement getHTMLElement();

	void bindHTML(HTMLElement htmlElement);

	void createHTML();

	void initHTML();

	void doPaintInternal();
}
