package java.applet;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

import elemental2.dom.Element;
import elemental2.dom.HTMLCollection;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.Node;
import j2ts_awt_swing.impl.CanvasImage;
import j2ts_awt_swing.impl.HTMLComponent;

import static elemental2.dom.DomGlobal.document;
import static elemental2.dom.DomGlobal.window;

public abstract class Applet extends Panel {

	static {
//		window.addEventListener("load", e -> {
//			HTMLCollection<elemental2.dom.Element> divList = document.getElementsByClassName("applet");
//			if (divList.length == 0) {
//				return null;
//			}
//			HTMLDivElement div = (HTMLDivElement) divList.getAt(0);
//			if (div.getAttribute("data-applet") != null) {
//				String[] names = div.getAttribute("data-applet").split(".");
//				Object constructor = window;
//				for (String name : names) {
//					constructor = constructor;
//				}
//				Applet applet = $new(constructor);
//
//				applet.setSize(Integer.parseInt(div.getAttribute("data-width")),
//						Integer.parseInt(div.getAttribute("data-height")));
//
//				((HTMLComponent)applet).bindHTML(div);
//				applet.init();
//				applet.doPaintInternal();
//			}
//			return null;
//		});
	}

	public Applet() {
	}

	public boolean keyDown(Event event, int key) {
		return true;
	}

	public void stop() {
	}

	public boolean lostFocus(Event event, Object obj) {
		return true;
	}

	public boolean gotFocus(Event event, Object obj) {
		return true;
	}

	public boolean keyUp(Event event, int key) {
		return true;
	}

	public void start() {
	}

	public boolean mouseDown(Event event, int x, int y) {
		return true;
	}

	public boolean mouseMove(Event event, int x, int y) {
		return true;
	}

	public void init() {
	}

	public String getParameter(String param) {
		Element element;
		for (int i = 0; i < this.htmlElement.childNodes.length ; i++) {
			Node node = this.htmlElement.childNodes.item(i);
			if (node.nodeType != Node.ELEMENT_NODE) continue;

			element = (Element) node;
			if (element.tagName.equals("PARAM")) {
				if (element.getAttribute("name").equals(param)) {
					return element.getAttribute("value");
				}
			}
		}
		return null;
	}

	public Image createImage(int width, int height) {
		return new CanvasImage(width, height);
	}

	public URL getCodeBase() {
        try {
            return new URL(window.location.href);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
