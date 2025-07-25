/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package java.awt;

import elemental2.core.JsArray;
import elemental2.dom.CSSProperties.ZIndexUnionType;

public abstract class Container extends Component {

	LayoutManager layoutMgr;
	Component[] components = {};
	Insets insets = new Insets(0, 0, 0, 0);

	public LayoutManager getLayout() {
		return layoutMgr;
	}

	public void setLayout(LayoutManager mgr) {
		if (layoutMgr != null) {
			removeAll();
			if (layoutMgr instanceof LayoutManager2) {
				((LayoutManager2) layoutMgr).invalidateLayout(this);
			}

			if (htmlElement != null) {
				while (htmlElement.firstChild != null) {
					htmlElement.removeChild(htmlElement.firstChild);
				}
			}
		}
		layoutMgr = mgr;
		if (layoutMgr != null) {
			if (htmlElement != null) {
				htmlElement.style.position = "";
				htmlElement.style.top = "";
				htmlElement.style.left = "";
			}
			layoutMgr.layoutContainer(this);
		} else if (htmlElement != null) {
			htmlElement.style.position = "absolute";
			htmlElement.style.top = "0px";
			htmlElement.style.left = "0px";
		}
	}

	public void doLayout() {
		layout();
	}

	@Deprecated
	public void layout() {
		LayoutManager layoutMgr = this.layoutMgr;
		if (layoutMgr != null) {
			layoutMgr.layoutContainer(this);
		}
	}

	public Component add(Component component) {
		add((String) null, component);
		return component;
	}

	public Component add(Component c, int index) {
		components = JsArray.asJsArray(components).splice(index, 0, c).asArray(new Component[0]);
		return c;
	}

	public Component add(String name, Component component) {
		addImpl(component, name, -1);
		return component;
	}

	public void add(Component component, Object constraints) {
		addImpl(component, constraints, -1);
	}

	public void add(Component component, Object constraints, int index) {
		addImpl(component, constraints, index);
	}

	protected void addImpl(Component component, Object constraints, int index) {
		if (component.parent != null) {
			component.parent.remove(component);
		}

		component.initHTML();

		JsArray.asJsArray(components).push(component);

		component.parent = this;

		if (layoutMgr != null) {
			if (layoutMgr instanceof LayoutManager2) {
				((LayoutManager2) layoutMgr).addLayoutComponent(component, constraints);
			} else if (constraints instanceof String || constraints == null){
				layoutMgr.addLayoutComponent((String) constraints, component);
			}
		} else {
			component.getHTMLElement().style.position = "absolute";
		}

		if (component.getHTMLElement().parentNode == null) {
			getHTMLElement().appendChild(component.getHTMLElement());
		}
	}

	@Override
	public void doPaintInternal() {
		super.doPaintInternal();
		for (Component c : components) {
			c.doPaintInternal();
		}
	}

	public int getComponentCount() {
		return components.length;
	}

	public void remove(int index) {
		remove(components[index]);
	}

	public Component getComponent(int n) {
		return components[n];
	}

	public Component[] getComponents() {
		return components;
	}

	public void removeAll() {
		if (layoutMgr != null) {
			JsArray.asJsArray(components).forEach((e, i) -> {
                layoutMgr.removeLayoutComponent(e);
                return null;
            });
		}
		components = new Component[0];
	}

	public void remove(Component comp) {
		int i = JsArray.asJsArray(components).indexOf(comp);
		if (i < 0)
			return;

		if (layoutMgr != null) {
			layoutMgr.removeLayoutComponent(comp);
		}

		comp.parent = null;

		if (comp.getHTMLElement().parentNode != null) {
			comp.getHTMLElement().parentNode.removeChild(comp.getHTMLElement());
		}

		JsArray.asJsArray(components).splice(i, 1);
	}

	public Insets getInsets() {
		return insets;
	}

	public void setInsets(Insets insets) {
		this.insets = insets;
	}

	public void setComponentZOrder(Component component, int zOrder) {
		String order = Integer.toString(getComponentCount() - zOrder);
		if (!component.getHTMLElement().style.zIndex.asString().equals(order))
			component.getHTMLElement().style.zIndex = ZIndexUnionType.of(order);
	}
}
