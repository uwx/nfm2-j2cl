package gwtxg2d.client;

import elemental2.dom.BaseRenderingContext2D.DrawImageImageUnionType;

import java.awt.*;

public abstract class DrawableImage extends Image {
    public abstract DrawImageImageUnionType getDrawable();
}
