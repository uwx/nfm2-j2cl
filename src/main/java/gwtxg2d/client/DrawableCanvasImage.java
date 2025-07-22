package gwtxg2d.client;

import elemental2.dom.BaseRenderingContext2D;
import elemental2.dom.BaseRenderingContext2D.DrawImageImageUnionType;
import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.OffscreenCanvas;
import jsinterop.base.Js;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

public class DrawableCanvasImage extends DrawableImage {
    private final OffscreenCanvas canvas;

    public DrawableCanvasImage(int width, int height) {
        this.canvas = new OffscreenCanvas(width, height);
    }

    @Override
    public int getWidth(ImageObserver observer) {
        return this.canvas.width;
    }

    @Override
    public int getHeight(ImageObserver observer) {
        return this.canvas.height;
    }

    @Override
    public ImageProducer getSource() {
        return null;
    }

    @Override
    public Graphics getGraphics() {
        return new CanvasGraphics(Js.cast(this.canvas.getContext("2d")));
    }

    @Override
    public Object getProperty(String name, ImageObserver observer) {
        return null;
    }

    @Override
    public DrawImageImageUnionType getDrawable() {
        return DrawImageImageUnionType.of(canvas);
    }
}
