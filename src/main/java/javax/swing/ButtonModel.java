/*
 * Copyright (c) 1997, 2006, Oracle and/or its affiliates. All rights reserved.
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


import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

public interface ButtonModel extends ItemSelectable {

    boolean isArmed();

    boolean isSelected();

    boolean isEnabled();

    boolean isPressed();

    boolean isRollover();

    public void setArmed(boolean b);

    public void setSelected(boolean b);

    public void setEnabled(boolean b);

    public void setPressed(boolean b);

    public void setRollover(boolean b);

    public void setMnemonic(int key);

    public int  getMnemonic();

    public void setActionCommand(String s);

    public String getActionCommand();

    public void setGroup(ButtonGroup group);

    void addActionListener(ActionListener l);

    void removeActionListener(ActionListener l);

    void addItemListener(ItemListener l);

    void removeItemListener(ItemListener l);

    void addChangeListener(ChangeListener l);

    void removeChangeListener(ChangeListener l);

}
