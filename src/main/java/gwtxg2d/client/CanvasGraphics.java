package gwtxg2d.client;

import elemental2.dom.BaseRenderingContext2D;
import elemental2.dom.BaseRenderingContext2D.FillStyleUnionType;
import elemental2.dom.BaseRenderingContext2D.StrokeStyleUnionType;
import elemental2.dom.CanvasRenderingContext2D;

import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * Graphics2D replacement for GWT
 * 
 * Works as a Wrapper around the HTML5 Context2d element
 */
public class CanvasGraphics extends Graphics2D {
	private final CanvasRenderingContext2D context;

	public CanvasGraphics(CanvasRenderingContext2D context) {
		this.context = context;
		trackTransforms(context);
	}

	public CanvasRenderingContext2D getContext2d() {
		return context;
	}

	@Override
	public void translate(int x, int y) {
		context.translate(x, y);
	}

	private String convert(Color color, float alpha) {
		return "rgba(" + color.getRed() + "," + color.getGreen() + ","
				+ color.getBlue() + ", " + alpha + ")";
	}

	private Color reverseConvert(String style) {
		int a = style.indexOf("rgba(");
		int b = style.indexOf(",", a + "rgba(".length());
		int c = style.indexOf(",", b + 1);
		int d = style.indexOf(",", c + 1);
		int e = style.indexOf(")", d + 1);
		int red = Integer.parseInt(style.substring(a + "rgba(".length(), b));
		int green = Integer.parseInt(style.substring(b + 1, c));
		int blue = Integer.parseInt(style.substring(c + 1, d));
		float alpha = Float.parseFloat(style.substring(d + 1, e));
		return new Color(red, green, blue, (int)(alpha*255));
	}

	@Override
	public void setColor(Color color) {
		if (color != null) {
			float alpha = color.getAlpha() / 255f;
			String colorString = convert(color, alpha);
			context.strokeStyle = StrokeStyleUnionType.of(colorString);
			context.fillStyle = FillStyleUnionType.of(colorString);
		}
	}

	@Override
	public void setStroke(Stroke s) {
		if (s instanceof BasicStroke) {
			BasicStroke stroke = (BasicStroke) s;
			// TODO support for stroke pattern as soon ie is support stroke patterns
			if (stroke.getDashArray() != null) {
				System.err.println("Ignoring stroke pattern. They aren't suppoted yet");
				// throw new
				// IllegalArgumentException("Stroke patterns aren't supported");
			}

			context.setMiterLimit(stroke.getMiterLimit());
			context.setLineWidth(stroke.getLineWidth());

			switch (stroke.getLineJoin()) {
			case BasicStroke.JOIN_BEVEL:
				context.setLineJoin("bevel");
				break;
			case BasicStroke.JOIN_MITER:
				context.setLineJoin("miter");
				break;
			case BasicStroke.JOIN_ROUND:
				context.setLineJoin("round");
				break;
			default:
				System.err.println("unknown line join type");
				throw new IllegalArgumentException("illegal line join value");
			}

			switch (stroke.getEndCap()) {
			case BasicStroke.CAP_BUTT:
				context.setLineCap("butt");
				break;
			case BasicStroke.CAP_ROUND:
				context.setLineCap("round");
				break;
			case BasicStroke.CAP_SQUARE:
				context.setLineCap("square");
				break;
			default:
				System.err.println("unknown line cap type");
				throw new IllegalArgumentException("illegal line cap value");
			}
		}
	}

	@Override
	public void draw(Shape shape) {
		if (shape != null) {
			path(shape);
			context.stroke();
		} else {
			System.err.println("dreckdreckdreck");
		}
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void fill(Shape shape) {
		if (shape != null) {
			path(shape);
			context.fill();
		}
	}

	private void path(Shape shape) {
		path(shape.getPathIterator(null));
	}

	private void path(PathIterator i) {
		float[] coords = new float[6];
		context.beginPath();
		while (!i.isDone()) {
			int segment = i.currentSegment(coords);
			if (segment == PathIterator.SEG_MOVETO) {
				context.moveTo(coords[0], coords[1]);
			} else if (segment == PathIterator.SEG_LINETO) {
				context.lineTo(coords[0], coords[1]);
			} else if (segment == PathIterator.SEG_QUADTO) {
				context.quadraticCurveTo(coords[0], coords[1], coords[2],
						coords[3]);
			} else if (segment == PathIterator.SEG_CUBICTO) {
				context.bezierCurveTo(coords[0], coords[1], coords[2],
						coords[3], coords[4], coords[5]);
			} else if (segment == PathIterator.SEG_CLOSE) {
				context.closePath();
			} else {
				throw new RuntimeException("Unknown Segment " + segment);
			}
			i.next();
		}
	}

	@Override
	public void setFont(Font font) {
		if (font != null && font.getFontName() != null)
			context.setFont(font.getFontName());
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		// TODO validate, intersec, null
		this.setClip(x, y, width, height);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		context.beginPath();
		context.moveTo(x, y);
		context.lineTo(x + width, y);
		context.lineTo(x + width, y + height);
		context.lineTo(x, y + height);
		context.lineTo(x, y);
		context.closePath();
		context.clip();
	}

	@Override
	public void setClip(Shape clip) {
		path(clip);
		context.clip();
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		context.clearRect(x, y, width, height);
	}

	@Override
	public void drawString(String str, int x, int y) {
		context.strokeText(str, x, y);
	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		drawString(new String(data, offset, length), x, y);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		if (img instanceof DrawableImage) {
			context.drawImage(((DrawableImage) img).getDrawable(), x, y);
			return true;
		}
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
		if (img instanceof DrawableImage) {
			context.drawImage(((DrawableImage) img).getDrawable(), x, y, width, height);
			return true;
		}
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
		if (img instanceof DrawableImage) {
			context.drawImage(((DrawableImage) img).getDrawable(), x, y);
			return true;
		}
		return false;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
		if (img instanceof DrawableImage) {
			context.drawImage(((DrawableImage) img).getDrawable(), x, y, width, height);
			return true;
		}
		return false;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void drawLine(int x, int y, int x2, int y2) {
		context.beginPath();
		context.moveTo(x, y);
		context.lineTo(x2, y2);
		context.stroke();
	}

	@Override
	public void drawString(String str, float x, float y) {
		context.strokeText(str, x, y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x, float y) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return null;
	}

	@Override
	public void setComposite(Composite comp) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setPaint(Paint paint) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {

	}

	@Override
	public void translate(double tx, double ty) {
		context.translate(tx, ty);
	}

	@Override
	public void rotate(double theta) {
		context.rotate(theta);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		translate(x, y);
        rotate(theta);
        translate(-x, -y);
	}

	@Override
	public void scale(double sx, double sy) {
		context.scale(sx, sy);
	}

	@Override
	public void shear(double shx, double shy) {
		// TODO Auto-generated method stub
	}

	@Override
	public void transform(AffineTransform at) {
		context.transform(at.getScaleX(), at.getShearX(), at.getShearY(), at.getScaleY(), at.getTranslateX(), at.getTranslateY());
	}

	@Override
	public void setTransform(AffineTransform at) {
		// TODO check if correct
		context.setTransform(at.getScaleX(), at.getShearX(), at.getShearY(), at.getScaleY(), at.getTranslateX(), at.getTranslateY());
	}

	@Override
	public AffineTransform getTransform() {
		// TODO improve
		return new AffineTransform();
	}

	@Override
	public Paint getPaint() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Composite getComposite() {
		return null;
	}

	@Override
	public void setBackground(Color color) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Color getBackground() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Stroke getStroke() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void clip(Shape shape) {
		if (shape != null) {
			path(shape);
			context.clip();
		}
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Graphics create() {
		context.save();
		return this;
	}

	@Override
	public Color getColor() {
		return reverseConvert(context.strokeStyle.asString());
	}

	@Override
	public void setPaintMode() {

	}

	@Override
	public void setXORMode(Color c1) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Font getFont() {
		return Font.decode(context.getFont());
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Rectangle getClipBounds() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Shape getClip() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		fill(new Rectangle(x, y, width, height));
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		draw(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		fill(new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight));
	}

	@Override
	public void drawOval(int x, int y, int w, int h) {
		draw(new Ellipse2D.Double(x, y, w, h));
	}

	@Override
	public void fillOval(int x, int y, int w, int h) {
		fill(new Ellipse2D.Double(x, y, w, h));
	}

	@Override
	public void drawArc(int x, int y, int w, int h, int startAngle,
			int arcAngle) {
		draw(new Arc2D.Double(x, y, w, h, startAngle, arcAngle, Arc2D.OPEN));
	}

	@Override
	public void fillArc(int x, int y, int w, int h, int startAngle,
			int arcAngle) {
		fill(new Arc2D.Double(x, y, w, h, startAngle, arcAngle, Arc2D.OPEN));
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
         context.beginPath();
         context.moveTo(xPoints[0], yPoints[0]);
         for (int i = 0; i < nPoints; i++) {
        	 context.lineTo(xPoints[i], yPoints[i]);
         }
         context.closePath();
         context.stroke();
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		draw(new Polygon(xPoints, yPoints, nPoints));
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		fill(new Polygon(xPoints, yPoints, nPoints));
	}

	@Override
	public void dispose() {
		context.restore();
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		return null;
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
	}

	@Override
	public RenderingHints getRenderingHints() {
		return new RenderingHints(null);
	}

	public Point transformedPoint(int x, int y) {
		return transformedPoint(context, x, y);
	}
	
	private native Point transformedPoint(CanvasRenderingContext2D ctx, int x, int y) /*-{
		var pt = ctx.transformedPoint(x,y);
		return @java.awt.Point::new(II)(pt.x,pt.y);
	}-*/;
	
	private native void trackTransforms(CanvasRenderingContext2D ctx) /*-{
		// Adds ctx.getTransform() - returns an SVGMatrix
		// Adds ctx.transformedPoint(x,y) - returns an SVGPoint

		var svg = $doc.createElementNS("http://www.w3.org/2000/svg",'svg');
		var xform = svg.createSVGMatrix();
		ctx.getTransform = function(){ return xform; };
		
		var savedTransforms = [];
		var save = ctx.save;
		ctx.save = function(){
			savedTransforms.push(xform.translate(0,0));
			return save.call(ctx);
		};
		var restore = ctx.restore;
		ctx.restore = function(){
			xform = savedTransforms.pop();
			return restore.call(ctx);
		};
	
		var scale = ctx.scale;
		ctx.scale = function(sx,sy){
			xform = xform.scaleNonUniform(sx,sy);
			return scale.call(ctx,sx,sy);
		};
		var rotate = ctx.rotate;
		ctx.rotate = function(radians){
			xform = xform.rotate(radians*180/Math.PI);
			return rotate.call(ctx,radians);
		};
		var translate = ctx.translate;
		ctx.translate = function(dx,dy){
			xform = xform.translate(dx,dy);
			return translate.call(ctx,dx,dy);
		};
		var transform = ctx.transform;
		ctx.transform = function(a,b,c,d,e,f){
			var m2 = svg.createSVGMatrix();
			m2.a=a; m2.b=b; m2.c=c; m2.d=d; m2.e=e; m2.f=f;
			xform = xform.multiply(m2);
			return transform.call(ctx,a,b,c,d,e,f);
		};
		var setTransform = ctx.setTransform;
		ctx.setTransform = function(a,b,c,d,e,f){
			xform.a = a;
			xform.b = b;
			xform.c = c;
			xform.d = d;
			xform.e = e;
			xform.f = f;
			return setTransform.call(ctx,a,b,c,d,e,f);
		};
		var pt  = svg.createSVGPoint();
		ctx.transformedPoint = function(x,y){
			pt.x=x; pt.y=y;
			return pt.matrixTransform(xform.inverse());
		}
	}-*/;
}
