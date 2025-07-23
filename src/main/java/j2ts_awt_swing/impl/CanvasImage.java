package j2ts_awt_swing.impl;

import elemental2.dom.BaseRenderingContext2D;
import elemental2.dom.HTMLCanvasElement;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

import static elemental2.dom.DomGlobal.document;

public class CanvasImage extends Image implements Drawable {
    private final HTMLCanvasElement canvas;

    public CanvasImage(int width, int height) {
        this.canvas = (HTMLCanvasElement) document.createElement("canvas");
        this.canvas.width = width;
        this.canvas.height = height;
    }

    @Override
    public BaseRenderingContext2D.DrawImageImageUnionType getDrawableImage() {
        return null;
    }

    @Override
    public int getWidth(ImageObserver observer) {
        return canvas.width;
    }

    @Override
    public int getHeight(ImageObserver observer) {
        return canvas.height;
    }

    @Override
    public ImageProducer getSource() {
        return null;
    }

    @Override
    public Graphics getGraphics() {
        return new WebGraphics2D(canvas);
    }

    @Override
    public Object getProperty(String name, ImageObserver observer) {
        return null;
    }
}
