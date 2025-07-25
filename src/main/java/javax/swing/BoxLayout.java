/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLTableCellElement;
import elemental2.dom.HTMLTableElement;
import elemental2.dom.HTMLTableRowElement;

import java.awt.*;
import java.io.Serializable;

import static elemental2.dom.DomGlobal.document;

@SuppressWarnings("serial")
public class BoxLayout implements LayoutManager2, Serializable {

	public static final int X_AXIS = 0;

	public static final int Y_AXIS = 1;

	public static final int LINE_AXIS = 2;

	public static final int PAGE_AXIS = 3;

	private HTMLTableElement table;

	public BoxLayout(Container target, int axis) {
		if (axis != X_AXIS && axis != Y_AXIS && axis != LINE_AXIS && axis != PAGE_AXIS) {
			throw new Error("Invalid axis");
		}
		this.axis = axis;
		this.target = target;
	}

	public final Container getTarget() {
		return this.target;
	}

	public final int getAxis() {
		return this.axis;
	}

	public synchronized void invalidateLayout(Container target) {
	}

	public void addLayoutComponent(String name, Component comp) {
	}

	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		if (axis == X_AXIS || axis == LINE_AXIS) {
			HTMLTableRowElement tr = (HTMLTableRowElement) table.firstChild;
			if (tr == null) {
				tr = (HTMLTableRowElement) document.createElement("tr");
				table.appendChild(tr);
			}
			HTMLTableCellElement td = (HTMLTableCellElement) document.createElement("td");
			td.appendChild(comp.getHTMLElement());
			tr.appendChild(td);
		} else {
			HTMLTableRowElement tr = (HTMLTableRowElement) document.createElement("tr");
			HTMLTableCellElement td = (HTMLTableCellElement) document.createElement("td");
			tr.appendChild(td);
			td.appendChild(comp.getHTMLElement());
			table.appendChild(tr);
		}
	}

	public synchronized float getLayoutAlignmentX(Container target) {
		return 0;
	}

	public synchronized float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	public void layoutContainer(Container target) {
		if (table == null) {
			table = (HTMLTableElement) document.createElement("table");
			HTMLDivElement div = (HTMLDivElement) target.getHTMLElement();
			div.appendChild(table);
		}
	}

	private int axis;
	private Container target;

}
