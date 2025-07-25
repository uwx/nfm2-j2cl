/*
 * Copyright (c) 2006, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.*;
import java.io.Serializable;
import java.util.Arrays;

/**
 * The {@code Path2D} class provides a simple, yet flexible shape which
 * represents an arbitrary geometric path. It can fully represent any path which
 * can be iterated by the {@link PathIterator} interface including all of its
 * segment types and winding rules and it implements all of the basic hit
 * testing methods of the {@link Shape} interface.
 * <p>
 * Use {@link Float} when dealing with data that can be represented and
 * used with floating point precision. Use {@link Double} for data that
 * requires the accuracy or range of double precision.
 * <p>
 * {@code Path2D} provides exactly those facilities required for basic
 * construction and management of a geometric path and implementation of the
 * above interfaces with little added interpretation. If it is useful to
 * manipulate the interiors of closed geometric shapes beyond simple hit testing
 * then the {@link Area} class provides additional capabilities specifically
 * targeted at closed figures. While both classes nominally implement the
 * {@code Shape} interface, they differ in purpose and together they provide two
 * useful views of a geometric shape where {@code Path2D} deals primarily with a
 * trajectory formed by path segments and {@code Area} deals more with
 * interpretation and manipulation of enclosed regions of 2D geometric space.
 * <p>
 * The {@link PathIterator} interface has more detailed descriptions of the
 * types of segments that make up a path and the winding rules that control how
 * to determine which regions are inside or outside the path.
 *
 * @author Jim Graham
 * @since 1.6
 */
public abstract class Path2D implements Shape, Cloneable {
	
	static abstract class Iterator implements PathIterator {
		int typeIdx;
		int pointIdx;
		Path2D path;

		static final int curvecoords[] = { 2, 2, 4, 6, 0 };

		Iterator(Path2D path) {
			this.path = path;
		}

		public int getWindingRule() {
			return path.getWindingRule();
		}

		public boolean isDone() {
			return (typeIdx >= path.numTypes);
		}

		public void next() {
			int type = path.pointTypes[typeIdx++];
			pointIdx += curvecoords[type];
		}
	}
	
	/**
	 * An even-odd winding rule for determining the interior of a path.
	 *
	 * @see PathIterator#WIND_EVEN_ODD
	 * @since 1.6
	 */
	public static final int WIND_EVEN_ODD = PathIterator.WIND_EVEN_ODD;

	/**
	 * A non-zero winding rule for determining the interior of a path.
	 *
	 * @see PathIterator#WIND_NON_ZERO
	 * @since 1.6
	 */
	public static final int WIND_NON_ZERO = PathIterator.WIND_NON_ZERO;

	// For code simplicity, copy these constants to our namespace
	// and cast them to byte constants for easy storage.
	private static final byte SEG_MOVETO = (byte) PathIterator.SEG_MOVETO;
	private static final byte SEG_LINETO = (byte) PathIterator.SEG_LINETO;
	private static final byte SEG_QUADTO = (byte) PathIterator.SEG_QUADTO;
	private static final byte SEG_CUBICTO = (byte) PathIterator.SEG_CUBICTO;
	private static final byte SEG_CLOSE = (byte) PathIterator.SEG_CLOSE;

	transient byte[] pointTypes;
	transient int numTypes;
	transient int numCoords;
	transient int windingRule;

	static final int INIT_SIZE = 20;
	static final int EXPAND_MAX = 500;

	/**
	 * Constructs a new empty {@code Path2D} object. It is assumed that the
	 * package sibling subclass that is defaulting to this constructor will fill
	 * in all values.
	 *
	 * @since 1.6
	 */
	/* private protected */
	Path2D() {
	}

	/**
	 * Constructs a new {@code Path2D} object from the given specified initial
	 * values. This method is only intended for internal use and should not be
	 * made public if the other constructors for this class are ever exposed.
	 *
	 * @param rule
	 *            the winding rule
	 * @param initialTypes
	 *            the size to make the initial array to store the path segment
	 *            types
	 * @since 1.6
	 */
	/* private protected */
	Path2D(int rule, int initialTypes) {
		setWindingRule(rule);
		this.pointTypes = new byte[initialTypes];
	}

	abstract float[] cloneCoordsFloat(AffineTransform at);

	abstract double[] cloneCoordsDouble(AffineTransform at);

	abstract void append(float x, float y);

	abstract void append(double x, double y);

	abstract Point2D getPoint(int coordindex);

	abstract void needRoom(boolean needMove, int newCoords);

	abstract int pointCrossings(double px, double py);

	abstract int rectCrossings(double rxmin, double rymin, double rxmax, double rymax);

	/**
	 * The {@code Float} class defines a geometric path with coordinates stored
	 * in single precision floating point.
	 *
	 * @since 1.6
	 */
	public static class Float extends Path2D implements Serializable {
		transient float floatCoords[];

		/**
		 * Constructs a new empty single precision {@code Path2D} object with a
		 * default winding rule of {@link #WIND_NON_ZERO}.
		 *
		 * @since 1.6
		 */
		public Float() {
			this(WIND_NON_ZERO, INIT_SIZE);
		}

		/**
		 * Constructs a new empty single precision {@code Path2D} object with
		 * the specified winding rule to control operations that require the
		 * interior of the path to be defined.
		 *
		 * @param rule
		 *            the winding rule
		 * @see #WIND_EVEN_ODD
		 * @see #WIND_NON_ZERO
		 * @since 1.6
		 */
		public Float(int rule) {
			this(rule, INIT_SIZE);
		}

		/**
		 * Constructs a new empty single precision {@code Path2D} object with
		 * the specified winding rule and the specified initial capacity to
		 * store path segments. This number is an initial guess as to how many
		 * path segments will be added to the path, but the storage is expanded
		 * as needed to store whatever path segments are added.
		 *
		 * @param rule
		 *            the winding rule
		 * @param initialCapacity
		 *            the estimate for the number of path segments in the path
		 * @see #WIND_EVEN_ODD
		 * @see #WIND_NON_ZERO
		 * @since 1.6
		 */
		public Float(int rule, int initialCapacity) {
			super(rule, initialCapacity);
			floatCoords = new float[initialCapacity * 2];
		}

		/**
		 * Constructs a new single precision {@code Path2D} object from an
		 * arbitrary {@link Shape} object. All of the initial geometry and the
		 * winding rule for this path are taken from the specified {@code Shape}
		 * object.
		 *
		 * @param s
		 *            the specified {@code Shape} object
		 * @since 1.6
		 */
		public Float(Shape s) {
			this(s, null);
		}

		/**
		 * Constructs a new single precision {@code Path2D} object from an
		 * arbitrary {@link Shape} object, transformed by an
		 * {@link AffineTransform} object. All of the initial geometry and the
		 * winding rule for this path are taken from the specified {@code Shape}
		 * object and transformed by the specified {@code AffineTransform}
		 * object.
		 *
		 * @param s
		 *            the specified {@code Shape} object
		 * @param at
		 *            the specified {@code AffineTransform} object
		 * @since 1.6
		 */
		public Float(Shape s, AffineTransform at) {
			if (s instanceof Path2D) {
				Path2D p2d = (Path2D) s;
				setWindingRule(p2d.windingRule);
				this.numTypes = p2d.numTypes;
				this.pointTypes = Arrays.copyOf(p2d.pointTypes, p2d.pointTypes.length);
				this.numCoords = p2d.numCoords;
				this.floatCoords = p2d.cloneCoordsFloat(at);
			} else {
				PathIterator pi = s.getPathIterator(at);
				setWindingRule(pi.getWindingRule());
				this.pointTypes = new byte[INIT_SIZE];
				this.floatCoords = new float[INIT_SIZE * 2];
				append(pi, false);
			}
		}

		float[] cloneCoordsFloat(AffineTransform at) {
			float ret[];
			if (at == null) {
				ret = Arrays.copyOf(this.floatCoords, this.floatCoords.length);
			} else {
				ret = new float[floatCoords.length];
				at.transform(floatCoords, 0, ret, 0, numCoords / 2);
			}
			return ret;
		}

		double[] cloneCoordsDouble(AffineTransform at) {
			double ret[] = new double[floatCoords.length];
			if (at == null) {
				for (int i = 0; i < numCoords; i++) {
					ret[i] = floatCoords[i];
				}
			} else {
				at.transform(floatCoords, 0, ret, 0, numCoords / 2);
			}
			return ret;
		}

		void append(float x, float y) {
			floatCoords[numCoords++] = x;
			floatCoords[numCoords++] = y;
		}

		void append(double x, double y) {
			floatCoords[numCoords++] = (float) x;
			floatCoords[numCoords++] = (float) y;
		}

		Point2D getPoint(int coordindex) {
			return new Point2D.Float(floatCoords[coordindex], floatCoords[coordindex + 1]);
		}

		void needRoom(boolean needMove, int newCoords) {
			if (needMove && numTypes == 0) {
				throw new IllegalPathStateException("missing initial moveto " + "in path definition");
			}
			int size = pointTypes.length;
			if (numTypes >= size) {
				int grow = size;
				if (grow > EXPAND_MAX) {
					grow = EXPAND_MAX;
				}
				pointTypes = Arrays.copyOf(pointTypes, size + grow);
			}
			size = floatCoords.length;
			if (numCoords + newCoords > size) {
				int grow = size;
				if (grow > EXPAND_MAX * 2) {
					grow = EXPAND_MAX * 2;
				}
				if (grow < newCoords) {
					grow = newCoords;
				}
				floatCoords = Arrays.copyOf(floatCoords, size + grow);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.6
		 */
		public final synchronized void moveTo(double x, double y) {
			if (numTypes > 0 && pointTypes[numTypes - 1] == SEG_MOVETO) {
				floatCoords[numCoords - 2] = (float) x;
				floatCoords[numCoords - 1] = (float) y;
			} else {
				needRoom(false, 2);
				pointTypes[numTypes++] = SEG_MOVETO;
				floatCoords[numCoords++] = (float) x;
				floatCoords[numCoords++] = (float) y;
			}
		}

		/**
		 * Adds a point to the path by moving to the specified coordinates
		 * specified in float precision.
		 * <p>
		 * This method provides a single precision variant of the double
		 * precision {@code moveTo()} method on the base {@code Path2D} class.
		 *
		 * @param x
		 *            the specified X coordinate
		 * @param y
		 *            the specified Y coordinate
		 * @see Path2D#moveTo
		 * @since 1.6
		 */
		public final synchronized void moveTo(float x, float y) {
			if (numTypes > 0 && pointTypes[numTypes - 1] == SEG_MOVETO) {
				floatCoords[numCoords - 2] = x;
				floatCoords[numCoords - 1] = y;
			} else {
				needRoom(false, 2);
				pointTypes[numTypes++] = SEG_MOVETO;
				floatCoords[numCoords++] = x;
				floatCoords[numCoords++] = y;
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.6
		 */
		public final synchronized void lineTo(double x, double y) {
			needRoom(true, 2);
			pointTypes[numTypes++] = SEG_LINETO;
			floatCoords[numCoords++] = (float) x;
			floatCoords[numCoords++] = (float) y;
		}

		/**
		 * Adds a point to the path by drawing a straight line from the current
		 * coordinates to the new specified coordinates specified in float
		 * precision.
		 * <p>
		 * This method provides a single precision variant of the double
		 * precision {@code lineTo()} method on the base {@code Path2D} class.
		 *
		 * @param x
		 *            the specified X coordinate
		 * @param y
		 *            the specified Y coordinate
		 * @see Path2D#lineTo
		 * @since 1.6
		 */
		public final synchronized void lineTo(float x, float y) {
			needRoom(true, 2);
			pointTypes[numTypes++] = SEG_LINETO;
			floatCoords[numCoords++] = x;
			floatCoords[numCoords++] = y;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.6
		 */
		public final synchronized void quadTo(double x1, double y1, double x2, double y2) {
			needRoom(true, 4);
			pointTypes[numTypes++] = SEG_QUADTO;
			floatCoords[numCoords++] = (float) x1;
			floatCoords[numCoords++] = (float) y1;
			floatCoords[numCoords++] = (float) x2;
			floatCoords[numCoords++] = (float) y2;
		}

		/**
		 * Adds a curved segment, defined by two new points, to the path by
		 * drawing a Quadratic curve that intersects both the current
		 * coordinates and the specified coordinates {@code (x2,y2)}, using the
		 * specified point {@code (x1,y1)} as a quadratic parametric control
		 * point. All coordinates are specified in float precision.
		 * <p>
		 * This method provides a single precision variant of the double
		 * precision {@code quadTo()} method on the base {@code Path2D} class.
		 *
		 * @param x1
		 *            the X coordinate of the quadratic control point
		 * @param y1
		 *            the Y coordinate of the quadratic control point
		 * @param x2
		 *            the X coordinate of the final end point
		 * @param y2
		 *            the Y coordinate of the final end point
		 * @see Path2D#quadTo
		 * @since 1.6
		 */
		public final synchronized void quadTo(float x1, float y1, float x2, float y2) {
			needRoom(true, 4);
			pointTypes[numTypes++] = SEG_QUADTO;
			floatCoords[numCoords++] = x1;
			floatCoords[numCoords++] = y1;
			floatCoords[numCoords++] = x2;
			floatCoords[numCoords++] = y2;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.6
		 */
		public final synchronized void curveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
			needRoom(true, 6);
			pointTypes[numTypes++] = SEG_CUBICTO;
			floatCoords[numCoords++] = (float) x1;
			floatCoords[numCoords++] = (float) y1;
			floatCoords[numCoords++] = (float) x2;
			floatCoords[numCoords++] = (float) y2;
			floatCoords[numCoords++] = (float) x3;
			floatCoords[numCoords++] = (float) y3;
		}

		/**
		 * Adds a curved segment, defined by three new points, to the path by
		 * drawing a B&eacute;zier curve that intersects both the current
		 * coordinates and the specified coordinates {@code (x3,y3)}, using the
		 * specified points {@code (x1,y1)} and {@code (x2,y2)} as B&eacute;zier
		 * control points. All coordinates are specified in float precision.
		 * <p>
		 * This method provides a single precision variant of the double
		 * precision {@code curveTo()} method on the base {@code Path2D} class.
		 *
		 * @param x1
		 *            the X coordinate of the first B&eacute;zier control point
		 * @param y1
		 *            the Y coordinate of the first B&eacute;zier control point
		 * @param x2
		 *            the X coordinate of the second B&eacute;zier control point
		 * @param y2
		 *            the Y coordinate of the second B&eacute;zier control point
		 * @param x3
		 *            the X coordinate of the final end point
		 * @param y3
		 *            the Y coordinate of the final end point
		 * @see Path2D#curveTo
		 * @since 1.6
		 */
		public final synchronized void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
			needRoom(true, 6);
			pointTypes[numTypes++] = SEG_CUBICTO;
			floatCoords[numCoords++] = x1;
			floatCoords[numCoords++] = y1;
			floatCoords[numCoords++] = x2;
			floatCoords[numCoords++] = y2;
			floatCoords[numCoords++] = x3;
			floatCoords[numCoords++] = y3;
		}

		int pointCrossings(double px, double py) {
			double movx, movy, curx, cury, endx, endy;
			float coords[] = floatCoords;
			curx = movx = coords[0];
			cury = movy = coords[1];
			int crossings = 0;
			int ci = 2;
			for (int i = 1; i < numTypes; i++) {
				switch (pointTypes[i]) {
				case PathIterator.SEG_MOVETO:
					if (cury != movy) {
						crossings += Curve.pointCrossingsForLine(px, py, curx, cury, movx, movy);
					}
					movx = curx = coords[ci++];
					movy = cury = coords[ci++];
					break;
				case PathIterator.SEG_LINETO:
					crossings += Curve.pointCrossingsForLine(px, py, curx, cury, endx = coords[ci++],
							endy = coords[ci++]);
					curx = endx;
					cury = endy;
					break;
				case PathIterator.SEG_QUADTO:
					crossings += Curve.pointCrossingsForQuad(px, py, curx, cury, coords[ci++], coords[ci++],
							endx = coords[ci++], endy = coords[ci++], 0);
					curx = endx;
					cury = endy;
					break;
				case PathIterator.SEG_CUBICTO:
					crossings += Curve.pointCrossingsForCubic(px, py, curx, cury, coords[ci++], coords[ci++],
							coords[ci++], coords[ci++], endx = coords[ci++], endy = coords[ci++], 0);
					curx = endx;
					cury = endy;
					break;
				case PathIterator.SEG_CLOSE:
					if (cury != movy) {
						crossings += Curve.pointCrossingsForLine(px, py, curx, cury, movx, movy);
					}
					curx = movx;
					cury = movy;
					break;
				}
			}
			if (cury != movy) {
				crossings += Curve.pointCrossingsForLine(px, py, curx, cury, movx, movy);
			}
			return crossings;
		}

		int rectCrossings(double rxmin, double rymin, double rxmax, double rymax) {
			float coords[] = floatCoords;
			double curx, cury, movx, movy, endx, endy;
			curx = movx = coords[0];
			cury = movy = coords[1];
			int crossings = 0;
			int ci = 2;
			for (int i = 1; crossings != Curve.RECT_INTERSECTS && i < numTypes; i++) {
				switch (pointTypes[i]) {
				case PathIterator.SEG_MOVETO:
					if (curx != movx || cury != movy) {
						crossings = Curve.rectCrossingsForLine(crossings, rxmin, rymin, rxmax, rymax, curx, cury, movx,
								movy);
					}
					// Count should always be a multiple of 2 here.
					// assert((crossings & 1) != 0);
					movx = curx = coords[ci++];
					movy = cury = coords[ci++];
					break;
				case PathIterator.SEG_LINETO:
					crossings = Curve.rectCrossingsForLine(crossings, rxmin, rymin, rxmax, rymax, curx, cury,
							endx = coords[ci++], endy = coords[ci++]);
					curx = endx;
					cury = endy;
					break;
				case PathIterator.SEG_QUADTO:
					crossings = Curve.rectCrossingsForQuad(crossings, rxmin, rymin, rxmax, rymax, curx, cury,
							coords[ci++], coords[ci++], endx = coords[ci++], endy = coords[ci++], 0);
					curx = endx;
					cury = endy;
					break;
				case PathIterator.SEG_CUBICTO:
					crossings = Curve.rectCrossingsForCubic(crossings, rxmin, rymin, rxmax, rymax, curx, cury,
							coords[ci++], coords[ci++], coords[ci++], coords[ci++], endx = coords[ci++],
							endy = coords[ci++], 0);
					curx = endx;
					cury = endy;
					break;
				case PathIterator.SEG_CLOSE:
					if (curx != movx || cury != movy) {
						crossings = Curve.rectCrossingsForLine(crossings, rxmin, rymin, rxmax, rymax, curx, cury, movx,
								movy);
					}
					curx = movx;
					cury = movy;
					// Count should always be a multiple of 2 here.
					// assert((crossings & 1) != 0);
					break;
				}
			}
			if (crossings != Curve.RECT_INTERSECTS && (curx != movx || cury != movy)) {
				crossings = Curve.rectCrossingsForLine(crossings, rxmin, rymin, rxmax, rymax, curx, cury, movx, movy);
			}
			// Count should always be a multiple of 2 here.
			// assert((crossings & 1) != 0);
			return crossings;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.6
		 */
		public final void append(PathIterator pi, boolean connect) {
			float coords[] = new float[6];
			while (!pi.isDone()) {
				switch (pi.currentSegment(coords)) {
				case SEG_MOVETO:
					if (!connect || numTypes < 1 || numCoords < 1) {
						moveTo(coords[0], coords[1]);
						break;
					}
					if (pointTypes[numTypes - 1] != SEG_CLOSE && floatCoords[numCoords - 2] == coords[0]
							&& floatCoords[numCoords - 1] == coords[1]) {
						// Collapse out initial moveto/lineto
						break;
					}
					lineTo(coords[0], coords[1]);
					break;
				case SEG_LINETO:
					lineTo(coords[0], coords[1]);
					break;
				case SEG_QUADTO:
					quadTo(coords[0], coords[1], coords[2], coords[3]);
					break;
				case SEG_CUBICTO:
					curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
					break;
				case SEG_CLOSE:
					closePath();
					break;
				}
				pi.next();
				connect = false;
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.6
		 */
		public final void transform(AffineTransform at) {
			at.transform(floatCoords, 0, floatCoords, 0, numCoords / 2);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.6
		 */
		public final synchronized Rectangle2D getBounds2D() {
			float x1, y1, x2, y2;
			int i = numCoords;
			if (i > 0) {
				y1 = y2 = floatCoords[--i];
				x1 = x2 = floatCoords[--i];
				while (i > 0) {
					float y = floatCoords[--i];
					float x = floatCoords[--i];
					if (x < x1)
						x1 = x;
					if (y < y1)
						y1 = y;
					if (x > x2)
						x2 = x;
					if (y > y2)
						y2 = y;
				}
			} else {
				x1 = y1 = x2 = y2 = 0.0f;
			}
			return new Rectangle2D.Float(x1, y1, x2 - x1, y2 - y1);
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * The iterator for this class is not multi-threaded safe, which means
		 * that the {@code Path2D} class does not guarantee that modifications
		 * to the geometry of this {@code Path2D} object do not affect any
		 * iterations of that geometry that are already in process.
		 *
		 * @since 1.6
		 */
		public final PathIterator getPathIterator(AffineTransform at) {
			if (at == null) {
				return new CopyIterator(this);
			} else {
				return new TxIterator(this, at);
			}
		}

		/**
		 * Creates a new object of the same class as this object.
		 *
		 * @return a clone of this instance.
		 * @exception OutOfMemoryError
		 *                if there is not enough memory.
		 * @see Cloneable
		 * @since 1.6
		 */
		public final Object clone() {
			// Note: It would be nice to have this return Path2D
			// but one of our subclasses (GeneralPath) needs to
			// offer "public Object clone()" for backwards
			// compatibility so we cannot restrict it further.
			// REMIND: Can we do both somehow?
			if (this instanceof GeneralPath) {
				return new GeneralPath(this);
			} else {
				return new Float(this);
			}
		}

		/*
		 * JDK 1.6 serialVersionUID
		 */
		private static final long serialVersionUID = 6990832515060788886L;

		static class CopyIterator extends Iterator {
			float floatCoords[];

			CopyIterator(Float p2df) {
				super(p2df);
				this.floatCoords = p2df.floatCoords;
			}

			public int currentSegment(float[] coords) {
				int type = path.pointTypes[typeIdx];
				int numCoords = curvecoords[type];
				if (numCoords > 0) {
					System.arraycopy(floatCoords, pointIdx, coords, 0, numCoords);
				}
				return type;
			}

			public int currentSegment(double[] coords) {
				int type = path.pointTypes[typeIdx];
				int numCoords = curvecoords[type];
				if (numCoords > 0) {
					for (int i = 0; i < numCoords; i++) {
						coords[i] = floatCoords[pointIdx + i];
					}
				}
				return type;
			}
		}

		static class TxIterator extends Iterator {
			float floatCoords[];
			AffineTransform affine;

			TxIterator(Float p2df, AffineTransform at) {
				super(p2df);
				this.floatCoords = p2df.floatCoords;
				this.affine = at;
			}

			public int currentSegment(float[] coords) {
				int type = path.pointTypes[typeIdx];
				int numCoords = curvecoords[type];
				if (numCoords > 0) {
					affine.transform(floatCoords, pointIdx, coords, 0, numCoords / 2);
				}
				return type;
			}

			public int currentSegment(double[] coords) {
				int type = path.pointTypes[typeIdx];
				int numCoords = curvecoords[type];
				if (numCoords > 0) {
					affine.transform(floatCoords, pointIdx, coords, 0, numCoords / 2);
				}
				return type;
			}
		}

	}

	/**
	 * The {@code Double} class defines a geometric path with coordinates stored
	 * in double precision floating point.
	 *
	 * @since 1.6
	 */
	public static class Double extends Path2D implements Serializable {
		transient double doubleCoords[];

		/**
		 * Constructs a new empty double precision {@code Path2D} object with a
		 * default winding rule of {@link #WIND_NON_ZERO}.
		 *
		 * @since 1.6
		 */
		public Double() {
			this(WIND_NON_ZERO, INIT_SIZE);
		}

		/**
		 * Constructs a new empty double precision {@code Path2D} object with
		 * the specified winding rule to control operations that require the
		 * interior of the path to be defined.
		 *
		 * @param rule
		 *            the winding rule
		 * @see #WIND_EVEN_ODD
		 * @see #WIND_NON_ZERO
		 * @since 1.6
		 */
		public Double(int rule) {
			this(rule, INIT_SIZE);
		}

		/**
		 * Constructs a new empty double precision {@code Path2D} object with
		 * the specified winding rule and the specified initial capacity to
		 * store path segments. This number is an initial guess as to how many
		 * path segments are in the path, but the storage is expanded as needed
		 * to store whatever path segments are added to this path.
		 *
		 * @param rule
		 *            the winding rule
		 * @param initialCapacity
		 *            the estimate for the number of path segments in the path
		 * @see #WIND_EVEN_ODD
		 * @see #WIND_NON_ZERO
		 * @since 1.6
		 */
		public Double(int rule, int initialCapacity) {
			super(rule, initialCapacity);
			doubleCoords = new double[initialCapacity * 2];
		}

		/**
		 * Constructs a new double precision {@code Path2D} object from an
		 * arbitrary {@link Shape} object. All of the initial geometry and the
		 * winding rule for this path are taken from the specified {@code Shape}
		 * object.
		 *
		 * @param s
		 *            the specified {@code Shape} object
		 * @since 1.6
		 */
		public Double(Shape s) {
			this(s, null);
		}

		/**
		 * Constructs a new double precision {@code Path2D} object from an
		 * arbitrary {@link Shape} object, transformed by an
		 * {@link AffineTransform} object. All of the initial geometry and the
		 * winding rule for this path are taken from the specified {@code Shape}
		 * object and transformed by the specified {@code AffineTransform}
		 * object.
		 *
		 * @param s
		 *            the specified {@code Shape} object
		 * @param at
		 *            the specified {@code AffineTransform} object
		 * @since 1.6
		 */
		public Double(Shape s, AffineTransform at) {
			if (s instanceof Path2D) {
				Path2D p2d = (Path2D) s;
				setWindingRule(p2d.windingRule);
				this.numTypes = p2d.numTypes;
				this.pointTypes = Arrays.copyOf(p2d.pointTypes, p2d.pointTypes.length);
				this.numCoords = p2d.numCoords;
				this.doubleCoords = p2d.cloneCoordsDouble(at);
			} else {
				PathIterator pi = s.getPathIterator(at);
				setWindingRule(pi.getWindingRule());
				this.pointTypes = new byte[INIT_SIZE];
				this.doubleCoords = new double[INIT_SIZE * 2];
				append(pi, false);
			}
		}

		float[] cloneCoordsFloat(AffineTransform at) {
			float ret[] = new float[doubleCoords.length];
			if (at == null) {
				for (int i = 0; i < numCoords; i++) {
					ret[i] = (float) doubleCoords[i];
				}
			} else {
				at.transform(doubleCoords, 0, ret, 0, numCoords / 2);
			}
			return ret;
		}

		double[] cloneCoordsDouble(AffineTransform at) {
			double ret[];
			if (at == null) {
				ret = Arrays.copyOf(this.doubleCoords, this.doubleCoords.length);
			} else {
				ret = new double[doubleCoords.length];
				at.transform(doubleCoords, 0, ret, 0, numCoords / 2);
			}
			return ret;
		}

		void append(float x, float y) {
			doubleCoords[numCoords++] = x;
			doubleCoords[numCoords++] = y;
		}

		void append(double x, double y) {
			doubleCoords[numCoords++] = x;
			doubleCoords[numCoords++] = y;
		}

		Point2D getPoint(int coordindex) {
			return new Point2D.Double(doubleCoords[coordindex], doubleCoords[coordindex + 1]);
		}

		void needRoom(boolean needMove, int newCoords) {
			if (needMove && numTypes == 0) {
				throw new IllegalPathStateException("missing initial moveto " + "in path definition");
			}
			int size = pointTypes.length;
			if (numTypes >= size) {
				int grow = size;
				if (grow > EXPAND_MAX) {
					grow = EXPAND_MAX;
				}
				pointTypes = Arrays.copyOf(pointTypes, size + grow);
			}
			size = doubleCoords.length;
			if (numCoords + newCoords > size) {
				int grow = size;
				if (grow > EXPAND_MAX * 2) {
					grow = EXPAND_MAX * 2;
				}
				if (grow < newCoords) {
					grow = newCoords;
				}
				doubleCoords = Arrays.copyOf(doubleCoords, size + grow);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.6
		 */
		public final synchronized void moveTo(double x, double y) {
			if (numTypes > 0 && pointTypes[numTypes - 1] == SEG_MOVETO) {
				doubleCoords[numCoords - 2] = x;
				doubleCoords[numCoords - 1] = y;
			} else {
				needRoom(false, 2);
				pointTypes[numTypes++] = SEG_MOVETO;
				doubleCoords[numCoords++] = x;
				doubleCoords[numCoords++] = y;
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.6
		 */
		public final synchronized void lineTo(double x, double y) {
			needRoom(true, 2);
			pointTypes[numTypes++] = SEG_LINETO;
			doubleCoords[numCoords++] = x;
			doubleCoords[numCoords++] = y;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.6
		 */
		public final synchronized void quadTo(double x1, double y1, double x2, double y2) {
			needRoom(true, 4);
			pointTypes[numTypes++] = SEG_QUADTO;
			doubleCoords[numCoords++] = x1;
			doubleCoords[numCoords++] = y1;
			doubleCoords[numCoords++] = x2;
			doubleCoords[numCoords++] = y2;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.6
		 */
		public final synchronized void curveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
			needRoom(true, 6);
			pointTypes[numTypes++] = SEG_CUBICTO;
			doubleCoords[numCoords++] = x1;
			doubleCoords[numCoords++] = y1;
			doubleCoords[numCoords++] = x2;
			doubleCoords[numCoords++] = y2;
			doubleCoords[numCoords++] = x3;
			doubleCoords[numCoords++] = y3;
		}

		int pointCrossings(double px, double py) {
			double movx, movy, curx, cury, endx, endy;
			double coords[] = doubleCoords;
			curx = movx = coords[0];
			cury = movy = coords[1];
			int crossings = 0;
			int ci = 2;
			for (int i = 1; i < numTypes; i++) {
				switch (pointTypes[i]) {
				case PathIterator.SEG_MOVETO:
					if (cury != movy) {
						crossings += Curve.pointCrossingsForLine(px, py, curx, cury, movx, movy);
					}
					movx = curx = coords[ci++];
					movy = cury = coords[ci++];
					break;
				case PathIterator.SEG_LINETO:
					crossings += Curve.pointCrossingsForLine(px, py, curx, cury, endx = coords[ci++],
							endy = coords[ci++]);
					curx = endx;
					cury = endy;
					break;
				case PathIterator.SEG_QUADTO:
					crossings += Curve.pointCrossingsForQuad(px, py, curx, cury, coords[ci++], coords[ci++],
							endx = coords[ci++], endy = coords[ci++], 0);
					curx = endx;
					cury = endy;
					break;
				case PathIterator.SEG_CUBICTO:
					crossings += Curve.pointCrossingsForCubic(px, py, curx, cury, coords[ci++], coords[ci++],
							coords[ci++], coords[ci++], endx = coords[ci++], endy = coords[ci++], 0);
					curx = endx;
					cury = endy;
					break;
				case PathIterator.SEG_CLOSE:
					if (cury != movy) {
						crossings += Curve.pointCrossingsForLine(px, py, curx, cury, movx, movy);
					}
					curx = movx;
					cury = movy;
					break;
				}
			}
			if (cury != movy) {
				crossings += Curve.pointCrossingsForLine(px, py, curx, cury, movx, movy);
			}
			return crossings;
		}

		int rectCrossings(double rxmin, double rymin, double rxmax, double rymax) {
			double coords[] = doubleCoords;
			double curx, cury, movx, movy, endx, endy;
			curx = movx = coords[0];
			cury = movy = coords[1];
			int crossings = 0;
			int ci = 2;
			for (int i = 1; crossings != Curve.RECT_INTERSECTS && i < numTypes; i++) {
				switch (pointTypes[i]) {
				case PathIterator.SEG_MOVETO:
					if (curx != movx || cury != movy) {
						crossings = Curve.rectCrossingsForLine(crossings, rxmin, rymin, rxmax, rymax, curx, cury, movx,
								movy);
					}
					// Count should always be a multiple of 2 here.
					// assert((crossings & 1) != 0);
					movx = curx = coords[ci++];
					movy = cury = coords[ci++];
					break;
				case PathIterator.SEG_LINETO:
					endx = coords[ci++];
					endy = coords[ci++];
					crossings = Curve.rectCrossingsForLine(crossings, rxmin, rymin, rxmax, rymax, curx, cury, endx,
							endy);
					curx = endx;
					cury = endy;
					break;
				case PathIterator.SEG_QUADTO:
					crossings = Curve.rectCrossingsForQuad(crossings, rxmin, rymin, rxmax, rymax, curx, cury,
							coords[ci++], coords[ci++], endx = coords[ci++], endy = coords[ci++], 0);
					curx = endx;
					cury = endy;
					break;
				case PathIterator.SEG_CUBICTO:
					crossings = Curve.rectCrossingsForCubic(crossings, rxmin, rymin, rxmax, rymax, curx, cury,
							coords[ci++], coords[ci++], coords[ci++], coords[ci++], endx = coords[ci++],
							endy = coords[ci++], 0);
					curx = endx;
					cury = endy;
					break;
				case PathIterator.SEG_CLOSE:
					if (curx != movx || cury != movy) {
						crossings = Curve.rectCrossingsForLine(crossings, rxmin, rymin, rxmax, rymax, curx, cury, movx,
								movy);
					}
					curx = movx;
					cury = movy;
					// Count should always be a multiple of 2 here.
					// assert((crossings & 1) != 0);
					break;
				}
			}
			if (crossings != Curve.RECT_INTERSECTS && (curx != movx || cury != movy)) {
				crossings = Curve.rectCrossingsForLine(crossings, rxmin, rymin, rxmax, rymax, curx, cury, movx, movy);
			}
			// Count should always be a multiple of 2 here.
			// assert((crossings & 1) != 0);
			return crossings;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.6
		 */
		public final void append(PathIterator pi, boolean connect) {
			double coords[] = new double[6];
			while (!pi.isDone()) {
				switch (pi.currentSegment(coords)) {
				case SEG_MOVETO:
					if (!connect || numTypes < 1 || numCoords < 1) {
						moveTo(coords[0], coords[1]);
						break;
					}
					if (pointTypes[numTypes - 1] != SEG_CLOSE && doubleCoords[numCoords - 2] == coords[0]
							&& doubleCoords[numCoords - 1] == coords[1]) {
						// Collapse out initial moveto/lineto
						break;
					}
					lineTo(coords[0], coords[1]);
					break;
				case SEG_LINETO:
					lineTo(coords[0], coords[1]);
					break;
				case SEG_QUADTO:
					quadTo(coords[0], coords[1], coords[2], coords[3]);
					break;
				case SEG_CUBICTO:
					curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
					break;
				case SEG_CLOSE:
					closePath();
					break;
				}
				pi.next();
				connect = false;
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.6
		 */
		public final void transform(AffineTransform at) {
			at.transform(doubleCoords, 0, doubleCoords, 0, numCoords / 2);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @since 1.6
		 */
		public final synchronized Rectangle2D getBounds2D() {
			double x1, y1, x2, y2;
			int i = numCoords;
			if (i > 0) {
				y1 = y2 = doubleCoords[--i];
				x1 = x2 = doubleCoords[--i];
				while (i > 0) {
					double y = doubleCoords[--i];
					double x = doubleCoords[--i];
					if (x < x1)
						x1 = x;
					if (y < y1)
						y1 = y;
					if (x > x2)
						x2 = x;
					if (y > y2)
						y2 = y;
				}
			} else {
				x1 = y1 = x2 = y2 = 0.0;
			}
			return new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);
		}

		/**
		 * {@inheritDoc}
		 * <p>
		 * The iterator for this class is not multi-threaded safe, which means
		 * that the {@code Path2D} class does not guarantee that modifications
		 * to the geometry of this {@code Path2D} object do not affect any
		 * iterations of that geometry that are already in process.
		 *
		 * @param at
		 *            an {@code AffineTransform}
		 * @return a new {@code PathIterator} that iterates along the boundary
		 *         of this {@code Shape} and provides access to the geometry of
		 *         this {@code Shape}'s outline
		 * @since 1.6
		 */
		public final PathIterator getPathIterator(AffineTransform at) {
			if (at == null) {
				return new CopyIterator(this);
			} else {
				return new TxIterator(this, at);
			}
		}

		/**
		 * Creates a new object of the same class as this object.
		 *
		 * @return a clone of this instance.
		 * @exception OutOfMemoryError
		 *                if there is not enough memory.
		 * @see Cloneable
		 * @since 1.6
		 */
		public final Object clone() {
			// Note: It would be nice to have this return Path2D
			// but one of our subclasses (GeneralPath) needs to
			// offer "public Object clone()" for backwards
			// compatibility so we cannot restrict it further.
			// REMIND: Can we do both somehow?
			return new Double(this);
		}

		/*
		 * JDK 1.6 serialVersionUID
		 */
		private static final long serialVersionUID = 1826762518450014216L;

		static class CopyIterator extends Iterator {
			double doubleCoords[];

			CopyIterator(Double p2dd) {
				super(p2dd);
				this.doubleCoords = p2dd.doubleCoords;
			}

			public int currentSegment(float[] coords) {
				int type = path.pointTypes[typeIdx];
				int numCoords = curvecoords[type];
				if (numCoords > 0) {
					for (int i = 0; i < numCoords; i++) {
						coords[i] = (float) doubleCoords[pointIdx + i];
					}
				}
				return type;
			}

			public int currentSegment(double[] coords) {
				int type = path.pointTypes[typeIdx];
				int numCoords = curvecoords[type];
				if (numCoords > 0) {
					System.arraycopy(doubleCoords, pointIdx, coords, 0, numCoords);
				}
				return type;
			}
		}

		static class TxIterator extends Iterator {
			double doubleCoords[];
			AffineTransform affine;

			TxIterator(Double p2dd, AffineTransform at) {
				super(p2dd);
				this.doubleCoords = p2dd.doubleCoords;
				this.affine = at;
			}

			public int currentSegment(float[] coords) {
				int type = path.pointTypes[typeIdx];
				int numCoords = curvecoords[type];
				if (numCoords > 0) {
					affine.transform(doubleCoords, pointIdx, coords, 0, numCoords / 2);
				}
				return type;
			}

			public int currentSegment(double[] coords) {
				int type = path.pointTypes[typeIdx];
				int numCoords = curvecoords[type];
				if (numCoords > 0) {
					affine.transform(doubleCoords, pointIdx, coords, 0, numCoords / 2);
				}
				return type;
			}
		}
	}

	/**
	 * Adds a point to the path by moving to the specified coordinates specified
	 * in double precision.
	 *
	 * @param x
	 *            the specified X coordinate
	 * @param y
	 *            the specified Y coordinate
	 * @since 1.6
	 */
	public abstract void moveTo(double x, double y);

	/**
	 * Adds a point to the path by drawing a straight line from the current
	 * coordinates to the new specified coordinates specified in double
	 * precision.
	 *
	 * @param x
	 *            the specified X coordinate
	 * @param y
	 *            the specified Y coordinate
	 * @since 1.6
	 */
	public abstract void lineTo(double x, double y);

	/**
	 * Adds a curved segment, defined by two new points, to the path by drawing
	 * a Quadratic curve that intersects both the current coordinates and the
	 * specified coordinates {@code (x2,y2)}, using the specified point
	 * {@code (x1,y1)} as a quadratic parametric control point. All coordinates
	 * are specified in double precision.
	 *
	 * @param x1
	 *            the X coordinate of the quadratic control point
	 * @param y1
	 *            the Y coordinate of the quadratic control point
	 * @param x2
	 *            the X coordinate of the final end point
	 * @param y2
	 *            the Y coordinate of the final end point
	 * @since 1.6
	 */
	public abstract void quadTo(double x1, double y1, double x2, double y2);

	/**
	 * Adds a curved segment, defined by three new points, to the path by
	 * drawing a B&eacute;zier curve that intersects both the current
	 * coordinates and the specified coordinates {@code (x3,y3)}, using the
	 * specified points {@code (x1,y1)} and {@code (x2,y2)} as B&eacute;zier
	 * control points. All coordinates are specified in double precision.
	 *
	 * @param x1
	 *            the X coordinate of the first B&eacute;zier control point
	 * @param y1
	 *            the Y coordinate of the first B&eacute;zier control point
	 * @param x2
	 *            the X coordinate of the second B&eacute;zier control point
	 * @param y2
	 *            the Y coordinate of the second B&eacute;zier control point
	 * @param x3
	 *            the X coordinate of the final end point
	 * @param y3
	 *            the Y coordinate of the final end point
	 * @since 1.6
	 */
	public abstract void curveTo(double x1, double y1, double x2, double y2, double x3, double y3);

	/**
	 * Closes the current subpath by drawing a straight line back to the
	 * coordinates of the last {@code moveTo}. If the path is already closed
	 * then this method has no effect.
	 *
	 * @since 1.6
	 */
	public final synchronized void closePath() {
		if (numTypes == 0 || pointTypes[numTypes - 1] != SEG_CLOSE) {
			needRoom(true, 0);
			pointTypes[numTypes++] = SEG_CLOSE;
		}
	}

	/**
	 * Appends the geometry of the specified {@code Shape} object to the path,
	 * possibly connecting the new geometry to the existing path segments with a
	 * line segment. If the {@code connect} parameter is {@code true} and the
	 * path is not empty then any initial {@code moveTo} in the geometry of the
	 * appended {@code Shape} is turned into a {@code lineTo} segment. If the
	 * destination coordinates of such a connecting {@code lineTo} segment match
	 * the ending coordinates of a currently open subpath then the segment is
	 * omitted as superfluous. The winding rule of the specified {@code Shape}
	 * is ignored and the appended geometry is governed by the winding rule
	 * specified for this path.
	 *
	 * @param s
	 *            the {@code Shape} whose geometry is appended to this path
	 * @param connect
	 *            a boolean to control whether or not to turn an initial
	 *            {@code moveTo} segment into a {@code lineTo} segment to
	 *            connect the new geometry to the existing path
	 * @since 1.6
	 */
	public final void append(Shape s, boolean connect) {
		append(s.getPathIterator(null), connect);
	}

	/**
	 * Appends the geometry of the specified {@link PathIterator} object to the
	 * path, possibly connecting the new geometry to the existing path segments
	 * with a line segment. If the {@code connect} parameter is {@code true} and
	 * the path is not empty then any initial {@code moveTo} in the geometry of
	 * the appended {@code Shape} is turned into a {@code lineTo} segment. If
	 * the destination coordinates of such a connecting {@code lineTo} segment
	 * match the ending coordinates of a currently open subpath then the segment
	 * is omitted as superfluous. The winding rule of the specified
	 * {@code Shape} is ignored and the appended geometry is governed by the
	 * winding rule specified for this path.
	 *
	 * @param pi
	 *            the {@code PathIterator} whose geometry is appended to this
	 *            path
	 * @param connect
	 *            a boolean to control whether or not to turn an initial
	 *            {@code moveTo} segment into a {@code lineTo} segment to
	 *            connect the new geometry to the existing path
	 * @since 1.6
	 */
	public abstract void append(PathIterator pi, boolean connect);

	/**
	 * Returns the fill style winding rule.
	 *
	 * @return an integer representing the current winding rule.
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 * @see #setWindingRule
	 * @since 1.6
	 */
	public final synchronized int getWindingRule() {
		return windingRule;
	}

	/**
	 * Sets the winding rule for this path to the specified value.
	 *
	 * @param rule
	 *            an integer representing the specified winding rule
	 * @exception IllegalArgumentException
	 *                if {@code rule} is not either {@link #WIND_EVEN_ODD} or
	 *                {@link #WIND_NON_ZERO}
	 * @see #getWindingRule
	 * @since 1.6
	 */
	public final void setWindingRule(int rule) {
		if (rule != WIND_EVEN_ODD && rule != WIND_NON_ZERO) {
			throw new IllegalArgumentException("winding rule must be " + "WIND_EVEN_ODD or " + "WIND_NON_ZERO");
		}
		windingRule = rule;
	}

	/**
	 * Returns the coordinates most recently added to the end of the path as a
	 * {@link Point2D} object.
	 *
	 * @return a {@code Point2D} object containing the ending coordinates of the
	 *         path or {@code null} if there are no points in the path.
	 * @since 1.6
	 */
	public final synchronized Point2D getCurrentPoint() {
		int index = numCoords;
		if (numTypes < 1 || index < 1) {
			return null;
		}
		if (pointTypes[numTypes - 1] == SEG_CLOSE) {
			loop: for (int i = numTypes - 2; i > 0; i--) {
				switch (pointTypes[i]) {
				case SEG_MOVETO:
					break loop;
				case SEG_LINETO:
					index -= 2;
					break;
				case SEG_QUADTO:
					index -= 4;
					break;
				case SEG_CUBICTO:
					index -= 6;
					break;
				case SEG_CLOSE:
					break;
				}
			}
		}
		return getPoint(index - 2);
	}

	/**
	 * Resets the path to empty. The append position is set back to the
	 * beginning of the path and all coordinates and point types are forgotten.
	 *
	 * @since 1.6
	 */
	public final synchronized void reset() {
		numTypes = numCoords = 0;
	}

	/**
	 * Transforms the geometry of this path using the specified
	 * {@link AffineTransform}. The geometry is transformed in place, which
	 * permanently changes the boundary defined by this object.
	 *
	 * @param at
	 *            the {@code AffineTransform} used to transform the area
	 * @since 1.6
	 */
	public abstract void transform(AffineTransform at);

	/**
	 * Returns a new {@code Shape} representing a transformed version of this
	 * {@code Path2D}. Note that the exact type and coordinate precision of the
	 * return value is not specified for this method. The method will return a
	 * Shape that contains no less precision for the transformed geometry than
	 * this {@code Path2D} currently maintains, but it may contain no more
	 * precision either. If the tradeoff of precision vs. storage size in the
	 * result is important then the convenience constructors in the
	 * {@link Float#Path2D.Float(Shape, AffineTransform) Path2D.Float}
	 * and {@link Double#Path2D.Double(Shape, AffineTransform)
	 * Path2D.Double} subclasses should be used to make the choice explicit.
	 *
	 * @param at
	 *            the {@code AffineTransform} used to transform a new
	 *            {@code Shape}.
	 * @return a new {@code Shape}, transformed with the specified
	 *         {@code AffineTransform}.
	 * @since 1.6
	 */
	public final synchronized Shape createTransformedShape(AffineTransform at) {
		Path2D p2d = (Path2D) clone();
		if (at != null) {
			p2d.transform(at);
		}
		return p2d;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.6
	 */
	public final Rectangle getBounds() {
		return getBounds2D().getBounds();
	}

	/**
	 * Tests if the specified coordinates are inside the closed boundary of the
	 * specified {@link PathIterator}.
	 * <p>
	 * This method provides a basic facility for implementors of the
	 * {@link Shape} interface to implement support for the
	 * {@link Shape#contains(double, double)} method.
	 *
	 * @param pi
	 *            the specified {@code PathIterator}
	 * @param x
	 *            the specified X coordinate
	 * @param y
	 *            the specified Y coordinate
	 * @return {@code true} if the specified coordinates are inside the
	 *         specified {@code PathIterator}; {@code false} otherwise
	 * @since 1.6
	 */
	public static boolean contains(PathIterator pi, double x, double y) {
		if (x * 0.0 + y * 0.0 == 0.0) {
			/*
			 * N * 0.0 is 0.0 only if N is finite. Here we know that both x and
			 * y are finite.
			 */
			int mask = (pi.getWindingRule() == WIND_NON_ZERO ? -1 : 1);
			int cross = Curve.pointCrossingsForPath(pi, x, y);
			return ((cross & mask) != 0);
		} else {
			/*
			 * Either x or y was infinite or NaN. A NaN always produces a
			 * negative response to any test and Infinity values cannot be
			 * "inside" any path so they should return false as well.
			 */
			return false;
		}
	}

	/**
	 * Tests if the specified {@link Point2D} is inside the closed boundary of
	 * the specified {@link PathIterator}.
	 * <p>
	 * This method provides a basic facility for implementors of the
	 * {@link Shape} interface to implement support for the
	 * {@link Shape#contains(Point2D)} method.
	 *
	 * @param pi
	 *            the specified {@code PathIterator}
	 * @param p
	 *            the specified {@code Point2D}
	 * @return {@code true} if the specified coordinates are inside the
	 *         specified {@code PathIterator}; {@code false} otherwise
	 * @since 1.6
	 */
	public static boolean contains(PathIterator pi, Point2D p) {
		return contains(pi, p.getX(), p.getY());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.6
	 */
	public final boolean contains(double x, double y) {
		if (x * 0.0 + y * 0.0 == 0.0) {
			/*
			 * N * 0.0 is 0.0 only if N is finite. Here we know that both x and
			 * y are finite.
			 */
			if (numTypes < 2) {
				return false;
			}
			int mask = (windingRule == WIND_NON_ZERO ? -1 : 1);
			return ((pointCrossings(x, y) & mask) != 0);
		} else {
			/*
			 * Either x or y was infinite or NaN. A NaN always produces a
			 * negative response to any test and Infinity values cannot be
			 * "inside" any path so they should return false as well.
			 */
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.6
	 */
	public final boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	/**
	 * Tests if the specified rectangular area is entirely inside the closed
	 * boundary of the specified {@link PathIterator}.
	 * <p>
	 * This method provides a basic facility for implementors of the
	 * {@link Shape} interface to implement support for the
	 * {@link Shape#contains(double, double, double, double)} method.
	 * <p>
	 * This method object may conservatively return false in cases where the
	 * specified rectangular area intersects a segment of the path, but that
	 * segment does not represent a boundary between the interior and exterior
	 * of the path. Such segments could lie entirely within the interior of the
	 * path if they are part of a path with a {@link #WIND_NON_ZERO} winding
	 * rule or if the segments are retraced in the reverse direction such that
	 * the two sets of segments cancel each other out without any exterior area
	 * falling between them. To determine whether segments represent true
	 * boundaries of the interior of the path would require extensive
	 * calculations involving all of the segments of the path and the winding
	 * rule and are thus beyond the scope of this implementation.
	 *
	 * @param pi
	 *            the specified {@code PathIterator}
	 * @param x
	 *            the specified X coordinate
	 * @param y
	 *            the specified Y coordinate
	 * @param w
	 *            the width of the specified rectangular area
	 * @param h
	 *            the height of the specified rectangular area
	 * @return {@code true} if the specified {@code PathIterator} contains the
	 *         specified rectangular area; {@code false} otherwise.
	 * @since 1.6
	 */
	public static boolean contains(PathIterator pi, double x, double y, double w, double h) {
		if (java.lang.Double.isNaN(x + w) || java.lang.Double.isNaN(y + h)) {
			/*
			 * [xy]+[wh] is NaN if any of those values are NaN, or if adding the
			 * two together would produce NaN by virtue of adding opposing
			 * Infinte values. Since we need to add them below, their sum must
			 * not be NaN. We return false because NaN always produces a
			 * negative response to tests
			 */
			return false;
		}
		if (w <= 0 || h <= 0) {
			return false;
		}
		int mask = (pi.getWindingRule() == WIND_NON_ZERO ? -1 : 2);
		int crossings = Curve.rectCrossingsForPath(pi, x, y, x + w, y + h);
		return (crossings != Curve.RECT_INTERSECTS && (crossings & mask) != 0);
	}

	/**
	 * Tests if the specified {@link Rectangle2D} is entirely inside the closed
	 * boundary of the specified {@link PathIterator}.
	 * <p>
	 * This method provides a basic facility for implementors of the
	 * {@link Shape} interface to implement support for the
	 * {@link Shape#contains(Rectangle2D)} method.
	 * <p>
	 * This method object may conservatively return false in cases where the
	 * specified rectangular area intersects a segment of the path, but that
	 * segment does not represent a boundary between the interior and exterior
	 * of the path. Such segments could lie entirely within the interior of the
	 * path if they are part of a path with a {@link #WIND_NON_ZERO} winding
	 * rule or if the segments are retraced in the reverse direction such that
	 * the two sets of segments cancel each other out without any exterior area
	 * falling between them. To determine whether segments represent true
	 * boundaries of the interior of the path would require extensive
	 * calculations involving all of the segments of the path and the winding
	 * rule and are thus beyond the scope of this implementation.
	 *
	 * @param pi
	 *            the specified {@code PathIterator}
	 * @param r
	 *            a specified {@code Rectangle2D}
	 * @return {@code true} if the specified {@code PathIterator} contains the
	 *         specified {@code Rectangle2D}; {@code false} otherwise.
	 * @since 1.6
	 */
	public static boolean contains(PathIterator pi, Rectangle2D r) {
		return contains(pi, r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method object may conservatively return false in cases where the
	 * specified rectangular area intersects a segment of the path, but that
	 * segment does not represent a boundary between the interior and exterior
	 * of the path. Such segments could lie entirely within the interior of the
	 * path if they are part of a path with a {@link #WIND_NON_ZERO} winding
	 * rule or if the segments are retraced in the reverse direction such that
	 * the two sets of segments cancel each other out without any exterior area
	 * falling between them. To determine whether segments represent true
	 * boundaries of the interior of the path would require extensive
	 * calculations involving all of the segments of the path and the winding
	 * rule and are thus beyond the scope of this implementation.
	 *
	 * @since 1.6
	 */
	public final boolean contains(double x, double y, double w, double h) {
		if (java.lang.Double.isNaN(x + w) || java.lang.Double.isNaN(y + h)) {
			/*
			 * [xy]+[wh] is NaN if any of those values are NaN, or if adding the
			 * two together would produce NaN by virtue of adding opposing
			 * Infinte values. Since we need to add them below, their sum must
			 * not be NaN. We return false because NaN always produces a
			 * negative response to tests
			 */
			return false;
		}
		if (w <= 0 || h <= 0) {
			return false;
		}
		int mask = (windingRule == WIND_NON_ZERO ? -1 : 2);
		int crossings = rectCrossings(x, y, x + w, y + h);
		return (crossings != Curve.RECT_INTERSECTS && (crossings & mask) != 0);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method object may conservatively return false in cases where the
	 * specified rectangular area intersects a segment of the path, but that
	 * segment does not represent a boundary between the interior and exterior
	 * of the path. Such segments could lie entirely within the interior of the
	 * path if they are part of a path with a {@link #WIND_NON_ZERO} winding
	 * rule or if the segments are retraced in the reverse direction such that
	 * the two sets of segments cancel each other out without any exterior area
	 * falling between them. To determine whether segments represent true
	 * boundaries of the interior of the path would require extensive
	 * calculations involving all of the segments of the path and the winding
	 * rule and are thus beyond the scope of this implementation.
	 *
	 * @since 1.6
	 */
	public final boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * Tests if the interior of the specified {@link PathIterator} intersects
	 * the interior of a specified set of rectangular coordinates.
	 * <p>
	 * This method provides a basic facility for implementors of the
	 * {@link Shape} interface to implement support for the
	 * {@link Shape#intersects(double, double, double, double)} method.
	 * <p>
	 * This method object may conservatively return true in cases where the
	 * specified rectangular area intersects a segment of the path, but that
	 * segment does not represent a boundary between the interior and exterior
	 * of the path. Such a case may occur if some set of segments of the path
	 * are retraced in the reverse direction such that the two sets of segments
	 * cancel each other out without any interior area between them. To
	 * determine whether segments represent true boundaries of the interior of
	 * the path would require extensive calculations involving all of the
	 * segments of the path and the winding rule and are thus beyond the scope
	 * of this implementation.
	 *
	 * @param pi
	 *            the specified {@code PathIterator}
	 * @param x
	 *            the specified X coordinate
	 * @param y
	 *            the specified Y coordinate
	 * @param w
	 *            the width of the specified rectangular coordinates
	 * @param h
	 *            the height of the specified rectangular coordinates
	 * @return {@code true} if the specified {@code PathIterator} and the
	 *         interior of the specified set of rectangular coordinates
	 *         intersect each other; {@code false} otherwise.
	 * @since 1.6
	 */
	public static boolean intersects(PathIterator pi, double x, double y, double w, double h) {
		if (java.lang.Double.isNaN(x + w) || java.lang.Double.isNaN(y + h)) {
			/*
			 * [xy]+[wh] is NaN if any of those values are NaN, or if adding the
			 * two together would produce NaN by virtue of adding opposing
			 * Infinte values. Since we need to add them below, their sum must
			 * not be NaN. We return false because NaN always produces a
			 * negative response to tests
			 */
			return false;
		}
		if (w <= 0 || h <= 0) {
			return false;
		}
		int mask = (pi.getWindingRule() == WIND_NON_ZERO ? -1 : 2);
		int crossings = Curve.rectCrossingsForPath(pi, x, y, x + w, y + h);
		return (crossings == Curve.RECT_INTERSECTS || (crossings & mask) != 0);
	}

	/**
	 * Tests if the interior of the specified {@link PathIterator} intersects
	 * the interior of a specified {@link Rectangle2D}.
	 * <p>
	 * This method provides a basic facility for implementors of the
	 * {@link Shape} interface to implement support for the
	 * {@link Shape#intersects(Rectangle2D)} method.
	 * <p>
	 * This method object may conservatively return true in cases where the
	 * specified rectangular area intersects a segment of the path, but that
	 * segment does not represent a boundary between the interior and exterior
	 * of the path. Such a case may occur if some set of segments of the path
	 * are retraced in the reverse direction such that the two sets of segments
	 * cancel each other out without any interior area between them. To
	 * determine whether segments represent true boundaries of the interior of
	 * the path would require extensive calculations involving all of the
	 * segments of the path and the winding rule and are thus beyond the scope
	 * of this implementation.
	 *
	 * @param pi
	 *            the specified {@code PathIterator}
	 * @param r
	 *            the specified {@code Rectangle2D}
	 * @return {@code true} if the specified {@code PathIterator} and the
	 *         interior of the specified {@code Rectangle2D} intersect each
	 *         other; {@code false} otherwise.
	 * @since 1.6
	 */
	public static boolean intersects(PathIterator pi, Rectangle2D r) {
		return intersects(pi, r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method object may conservatively return true in cases where the
	 * specified rectangular area intersects a segment of the path, but that
	 * segment does not represent a boundary between the interior and exterior
	 * of the path. Such a case may occur if some set of segments of the path
	 * are retraced in the reverse direction such that the two sets of segments
	 * cancel each other out without any interior area between them. To
	 * determine whether segments represent true boundaries of the interior of
	 * the path would require extensive calculations involving all of the
	 * segments of the path and the winding rule and are thus beyond the scope
	 * of this implementation.
	 *
	 * @since 1.6
	 */
	public final boolean intersects(double x, double y, double w, double h) {
		if (java.lang.Double.isNaN(x + w) || java.lang.Double.isNaN(y + h)) {
			/*
			 * [xy]+[wh] is NaN if any of those values are NaN, or if adding the
			 * two together would produce NaN by virtue of adding opposing
			 * Infinte values. Since we need to add them below, their sum must
			 * not be NaN. We return false because NaN always produces a
			 * negative response to tests
			 */
			return false;
		}
		if (w <= 0 || h <= 0) {
			return false;
		}
		int mask = (windingRule == WIND_NON_ZERO ? -1 : 2);
		int crossings = rectCrossings(x, y, x + w, y + h);
		return (crossings == Curve.RECT_INTERSECTS || (crossings & mask) != 0);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method object may conservatively return true in cases where the
	 * specified rectangular area intersects a segment of the path, but that
	 * segment does not represent a boundary between the interior and exterior
	 * of the path. Such a case may occur if some set of segments of the path
	 * are retraced in the reverse direction such that the two sets of segments
	 * cancel each other out without any interior area between them. To
	 * determine whether segments represent true boundaries of the interior of
	 * the path would require extensive calculations involving all of the
	 * segments of the path and the winding rule and are thus beyond the scope
	 * of this implementation.
	 *
	 * @since 1.6
	 */
	public final boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The iterator for this class is not multi-threaded safe, which means that
	 * this {@code Path2D} class does not guarantee that modifications to the
	 * geometry of this {@code Path2D} object do not affect any iterations of
	 * that geometry that are already in process.
	 *
	 * @since 1.6
	 */
	public final PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new FlatteningPathIterator(getPathIterator(at), flatness);
	}

	/**
	 * Creates a new object of the same class as this object.
	 *
	 * @return a clone of this instance.
	 * @exception OutOfMemoryError
	 *                if there is not enough memory.
	 * @see Cloneable
	 * @since 1.6
	 */
	public abstract Object clone();
	// Note: It would be nice to have this return Path2D
	// but one of our subclasses (GeneralPath) needs to
	// offer "public Object clone()" for backwards
	// compatibility so we cannot restrict it further.
	// REMIND: Can we do both somehow?

}
