/*
 * Copyright (c) 2007, Oracle and/or its affiliates. All rights reserved.
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

package java.awt.geom;

public interface PathConsumer2D {
	/**
	 * @see Path2D.Float.moveTo
	 */
	public void moveTo(float x, float y);

	/**
	 * @see Path2D.Float.lineTo
	 */
	public void lineTo(float x, float y);

	/**
	 * @see Path2D.Float.quadTo
	 */
	public void quadTo(float x1, float y1, float x2, float y2);

	/**
	 * @see Path2D.Float.curveTo
	 */
	public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3);

	/**
	 * @see Path2D.Float.closePath
	 */
	public void closePath();

	/**
	 * Called after the last segment of the last subpath when the iteration of
	 * the path segments is completely done. This method serves to trigger the
	 * end of path processing in the consumer that would normally be triggered
	 * when a {@link PathIterator PathIterator} returns
	 * {@code true} from its {@code done} method.
	 */
	public void pathDone();

	/**
	 * If a given PathConsumer performs all or most of its work natively then it
	 * can return a (non-zero) pointer to a native function vector that defines
	 * C functions for all of the above methods. The specific pointer it returns
	 * is a pointer to a PathConsumerVec structure as defined in the include
	 * file src/share/native/sun/java2d/pipe/PathConsumer2D.h
	 * 
	 * @return a native pointer to a PathConsumerVec structure.
	 */
	public long getNativeConsumer();
}
