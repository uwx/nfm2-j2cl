package java.awt;

import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLElement;
import java_impl.WebGraphics2D;

import static elemental2.dom.DomGlobal.document;
import static elemental2.dom.DomGlobal.window;

public class Panel extends Container {

	private HTMLCanvasElement htmlCanvas;

	public Panel() {
		this(new FlowLayout());
	}

	public Panel(LayoutManager layout) {
		setLayout(layout);
	}

	@Override
	public void createHTML() {
		if (htmlElement != null) {
			return;
		}
		htmlElement = (HTMLElement) document.createElement("div");
	}

	@Override
	public Graphics getGraphics() {
		return new WebGraphics2D(htmlCanvas);
	}

	@Override
	public void setBackground(Color background) {
		super.setBackground(background);
		if (htmlElement != null) {
			htmlElement.style.backgroundColor = null;
		}
		if (htmlCanvas != null) {
			if (background != null) {
				htmlCanvas.style.backgroundColor = background.toHTML();
			}
		}
	}

	@Override
	public void doPaintInternal() {
		if (htmlCanvas.width == 0 && htmlCanvas.height == 0) {
			htmlCanvas.width = htmlElement.offsetWidth;
			htmlCanvas.height = htmlElement.offsetHeight;
		}
		super.doPaintInternal();
	}

	@Override
	public void initHTML() {
		super.initHTML();
		if (htmlCanvas == null) {
			htmlCanvas = (HTMLCanvasElement) document.createElement("canvas");
			htmlElement.appendChild(htmlCanvas);
			window.onresize = e -> {
				if ((htmlCanvas.width != htmlElement.offsetWidth) || (htmlCanvas.height != htmlElement.offsetHeight)) {
					htmlCanvas.width = htmlElement.offsetWidth;
					htmlCanvas.height = htmlElement.offsetHeight;
					repaint();
				}
				return e;
			};
		}
		if (background != null) {
			htmlElement.style.backgroundColor = null;
			htmlCanvas.style.backgroundColor = background.toHTML();
		}
		htmlCanvas.width = htmlElement.offsetWidth;
		htmlCanvas.height = htmlElement.offsetHeight;
		htmlCanvas.style.position = "absolute";
		htmlCanvas.style.zIndex = CSSProperties.ZIndexUnionType.of("1");
	}

}
