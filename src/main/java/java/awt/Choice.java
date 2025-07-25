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
import elemental2.dom.*;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.EventListener;
import java.util.Vector;

import static elemental2.dom.DomGlobal.document;

public class Choice extends Component implements ItemSelectable {
	Vector<String> pItems;

	int selectedIndex = -1;

	JsArray<ItemListener> itemListeners;

	private static final String base = "choice";
	private static int nameCounter = 0;

	private static final long serialVersionUID = -4075310674757313071L;

	public Choice() throws HeadlessException {
		pItems = new Vector<>();
		itemListeners = new JsArray<ItemListener>();
	}

	public void createHTML() {
		htmlElement = (HTMLElement) document.createElement("select");
	}

	public HTMLSelectElement getHTMLElement() {
		return (HTMLSelectElement) htmlElement;
	}

	public void initHTML() {
		super.initHTML();
		getHTMLElement().onchange = e -> {
			int i = (int) getHTMLElement().selectedIndex;
			HTMLOptionElement option = (HTMLOptionElement) getHTMLElement().childNodes.item(i);
			processItemEvent(new ItemEvent(this, 0, option.innerHTML, ItemEvent.SELECTED));
			return e;
		};
		NodeList<Node> childNodes = getHTMLElement().childNodes;
		for (int i = 0; i < childNodes.length; ++i) {
			getHTMLElement().removeChild(childNodes.getAt(i));
		}
		for (int i = 0; i < getItemCount(); i++) {
			HTMLOptionElement option = (HTMLOptionElement) document.createElement("option");
			option.innerHTML = getItem(i);
			option.value = getItem(i);
			if (getSelectedIndex() == i) {
				option.selected = true;
			}
			getHTMLElement().appendChild(option);
		}
	}

	String constructComponentName() {
		synchronized (Choice.class) {
			return base + nameCounter++;
		}
	}

	public int getItemCount() {
		return countItems();
	}

	@Deprecated
	public int countItems() {
		return pItems.size();
	}

	public String getItem(int index) {
		return getItemImpl(index);
	}

	final String getItemImpl(int index) {
		return pItems.elementAt(index);
	}

	public void add(String item) {
		addItem(item);
	}

	public void addItem(String item) {
		synchronized (this) {
			insertNoInvalidate(item, pItems.size());
		}
	}

	private void insertNoInvalidate(String item, int index) {
		if (item == null) {
			throw new NullPointerException("cannot add null item to Choice");
		}
		pItems.insertElementAt(item, index);
		// no selection or selection shifted up
		if (selectedIndex < 0 || selectedIndex >= index) {
			select(0);
		}
		if (htmlElement != null) {
			initHTML();
		}
	}

	public void insert(String item, int index) {
		synchronized (this) {
			if (index < 0) {
				throw new IllegalArgumentException("index less than zero.");
			}
			/* if the index greater than item count, add item to the end */
			index = Math.min(index, pItems.size());

			insertNoInvalidate(item, index);
		}
	}

	public void remove(String item) {
		synchronized (this) {
			int index = pItems.indexOf(item);
			if (index < 0) {
				throw new IllegalArgumentException("item " + item + " not found in choice");
			} else {
				removeNoInvalidate(index);
			}
		}

	}

	public void remove(int position) {
		synchronized (this) {
			removeNoInvalidate(position);
		}
	}

	private void removeNoInvalidate(int position) {
		pItems.removeElementAt(position);
		if (pItems.size() == 0) {
			selectedIndex = -1;
		} else if (selectedIndex == position) {
			select(0);
		} else if (selectedIndex > position) {
			select(selectedIndex - 1);
		}
		if (htmlElement != null) {
			initHTML();
		}
	}

	public void removeAll() {
		synchronized (this) {
			pItems.removeAllElements();
			selectedIndex = -1;
			if (htmlElement != null) {
				initHTML();
			}
		}
	}

	public synchronized String getSelectedItem() {
		return (selectedIndex >= 0) ? getItem(selectedIndex) : null;
	}

	public synchronized Object[] getSelectedObjects() {
		if (selectedIndex >= 0) {
			Object[] items = new Object[1];
			items[0] = getItem(selectedIndex);
			return items;
		}
		return null;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public synchronized void select(int pos) {
		if ((pos >= pItems.size()) || (pos < 0)) {
			throw new IllegalArgumentException("illegal Choice item position: " + pos);
		}
		if (pItems.size() > 0) {
			selectedIndex = pos;
			if (htmlElement != null) {
				initHTML();
			}
		}
	}

	public synchronized void select(String str) {
		int index = pItems.indexOf(str);
		if (index >= 0) {
			select(index);
		}
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
		JsArray<T> result = new JsArray<T>();
		for (int i = 0; i < itemListeners.length; i++) {
			if (itemListeners.getAt(i).getClass() == listenerType) {
				result.push((T) itemListeners.getAt(i));
			}
		}
		return result.asArray((T[]) new Object[0]);
	}

	protected void processItemEvent(ItemEvent e) {
		for (int i = 0; i < itemListeners.length; i++) {
			itemListeners.getAt(i).itemStateChanged(e);
		}
	}

	protected String paramString() {
		return super.paramString() + ",current=" + getSelectedItem();
	}

}
