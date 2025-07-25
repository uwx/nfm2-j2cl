/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.event.AdjustmentListener;
import java.lang.annotation.Native;

public interface Adjustable {

    @Native public static final int HORIZONTAL = 0;

    @Native public static final int VERTICAL = 1;

    @Native public static final int NO_ORIENTATION = 2;

    int getOrientation();

    void setMinimum(int min);

    int getMinimum();

    void setMaximum(int max);

    int getMaximum();

    void setUnitIncrement(int u);

    int getUnitIncrement();

    void setBlockIncrement(int b);

    int getBlockIncrement();

    void setVisibleAmount(int v);

    int getVisibleAmount();

    void setValue(int v);

    int getValue();

    void addAdjustmentListener(AdjustmentListener l);

    void removeAdjustmentListener(AdjustmentListener l);

}
