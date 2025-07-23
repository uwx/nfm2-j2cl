package j2ts_awt_swing.impl;

import elemental2.dom.BaseRenderingContext2D;
import elemental2.dom.HTMLImageElement;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

import static elemental2.dom.DomGlobal.document;

public class ImageImpl extends Image implements Drawable {

    // @Interface
    // class ImageSource {
    // public double width;
    // public double height;
    // }

    // public Image(ImageSource source) {
    // this.source = source;
    // }

    public ImageImpl(String src) {
        source = (HTMLImageElement) document.createElement("img");
        source.src = src;
        setScale(SCALE_DEFAULT);
    }

    private void setScale(int scale) {
        switch (scale) {
            case SCALE_DEFAULT:
            case SCALE_FAST:
            case SCALE_REPLICATE:
//				source.style.$set("imageRendering", "pixelated");
                break;
            case SCALE_SMOOTH:
            case SCALE_AREA_AVERAGING:
//				source.style.$set("imageRendering", "");

        }
    }

    public int getWidth(ImageObserver observer) {
        return (int) source.width;
    }

    public int getHeight(ImageObserver observer) {
        return (int) source.height;
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

    public final HTMLImageElement source;

    // public abstract ImageProducer getSource();

    // public Graphics getGraphics();

    public Image getScaledInstance(int width, int height, int scaleType) {
        ImageImpl image = new ImageImpl(source.src);
        image.source.width = width;
        image.source.height = height;
        image.setScale(scaleType);
        return image;
    }
    /**
     * 
     * Use the default image-scaling algorithm.
     *
     * @since JDK1.1
     */
    public static final int SCALE_DEFAULT = 1;

    /**
     * Choose an image-scaling algorithm that gives higher priority to scaling
     * speed than smoothness of the scaled image.
     *
     * @since JDK1.1
     */
    public static final int SCALE_FAST = 2;

    public static final int SCALE_SMOOTH = 4;

    public static final int SCALE_REPLICATE = 8;

    public static final int SCALE_AREA_AVERAGING = 16;

    public void flush() {
        // do nothing
    }

    @Override
    public BaseRenderingContext2D.DrawImageImageUnionType getDrawableImage() {
        return BaseRenderingContext2D.DrawImageImageUnionType.of(source);
    }
}
