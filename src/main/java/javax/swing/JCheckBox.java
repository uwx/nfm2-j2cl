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
package javax.swing;

import elemental2.core.JsArray;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.Text;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventListener;

import static elemental2.dom.DomGlobal.document;
import static java_impl.Iter.iter;

public class JCheckBox extends JToggleButton implements ItemSelectable {

	String label;

	boolean state;

	JsArray<ItemListener> itemListeners;

	HTMLInputElement htmlCheckbox;
	Text htmlLabel;

	private static final String base = "checkbox";
	private static int nameCounter = 0;

	static final long serialVersionUID = 7270714317450821763L;

	public JCheckBox() {
		this("", false);
	}

	public JCheckBox(String label) {
		this(label, false);
	}

	public JCheckBox(String label, boolean state) {
		this.label = label;
		this.state = state;
		this.itemListeners = new JsArray<ItemListener>();
	}

	@Override
	public void createHTML() {
		if (htmlElement != null) {
			return;
		}
		htmlElement = (HTMLElement) document.createElement("label");

		htmlCheckbox = (HTMLInputElement) document.createElement("input");
		htmlCheckbox.type = "checkbox";

		htmlElement.appendChild(htmlCheckbox);
		htmlElement.appendChild(htmlLabel = document.createTextNode(""));
		htmlElement.style.whiteSpace = "nowrap";
		htmlElement.style.display = "inline";
	}

	@Override
	public void initHTML() {
		super.initHTML();
		htmlCheckbox.checked = state;
		htmlLabel.data = label;

		htmlCheckbox.onclick = e -> {
			setState(htmlCheckbox.checked);
			processItemEvent(
					new ItemEvent(this, 0, null, htmlCheckbox.checked ? ItemEvent.SELECTED : ItemEvent.DESELECTED));
			return e;
		};
	}

	String constructComponentName() {
		synchronized (Checkbox.class) {
			return base + nameCounter++;
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		synchronized (this) {
			if (label != this.label && (this.label == null || !this.label.equals(label))) {
				this.label = label;
			}
		}

	}

	public boolean getState() {
		return this.state;
	}

	void setStateInternal(boolean state) {
		this.state = state;
		if (htmlCheckbox != null) {
			htmlCheckbox.checked = state;
		}
	}

	public void setState(boolean state) {
		setStateInternal(state);
	}

	public Object[] getSelectedObjects() {
		if (state) {
			Object[] items = new Object[1];
			items[0] = label;
			return items;
		}
		return null;
	}

	public synchronized void addItemListener(ItemListener l) {
		if (l == null) {
			return;
		}
		itemListeners.push(l);
	}

	public synchronized void removeItemListener(ItemListener l) {
		if (l == null) {
			return;
		}
		int index = (int) itemListeners.indexOf(l);
		if (index > -1) {
			itemListeners.splice(index, 1);
		}
	}

	public synchronized ItemListener[] getItemListeners() {
		return itemListeners.asArray(new ItemListener[itemListeners.length]);
	}

	@SuppressWarnings({"unchecked", "DataFlowIssue"})
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
		JsArray<T> result = new JsArray<>();
		for (int i = 0; i < itemListeners.length; i++) {
			if (itemListeners.getAt(i).getClass() == listenerType) {
				result.push((T) itemListeners.getAt(i));
			}
		}
		return result.asArray((T[]) new Object[result.length]);
	}

	protected void processItemEvent(ItemEvent e) {
		for (ItemListener listener : iter(itemListeners)) {
			listener.itemStateChanged(e);
		}
	}

	protected String paramString() {
		String str = super.paramString();
		String label = this.label;
		if (label != null) {
			str += ",label=" + label;
		}
		return str + ",state=" + state;
	}

}
