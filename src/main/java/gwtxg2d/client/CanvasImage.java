package gwtxg2d.client;

import elemental2.dom.BaseRenderingContext2D;
import elemental2.dom.BaseRenderingContext2D.DrawImageImageUnionType;
import elemental2.dom.HTMLImageElement;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

public class CanvasImage extends DrawableImage {
    private final HTMLImageElement image;

    public CanvasImage(HTMLImageElement image) {
        this.image = image;
    }

    @Override
    public int getWidth(ImageObserver observer) {
        return image.width;
    }

    @Override
    public int getHeight(ImageObserver observer) {
        return image.height;
    }

    @Override
    public ImageProducer getSource() {
        return null;
    }

    @Override
    public Graphics getGraphics() {
        return null;
    }

    @Override
    public Object getProperty(String name, ImageObserver observer) {
        return null;
    }

    @Override
    public DrawImageImageUnionType getDrawable() {
        return DrawImageImageUnionType.of(image);
    }
}
