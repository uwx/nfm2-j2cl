package io.github.uwx;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;

public class PanelPolyfill implements ImageObserver {
    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        return false;
    }

    protected Image createImage(MemoryImageSource memoryImageSource) {
        throw new RuntimeException("Not implemented");
    }

    protected Image createImage(int i, int i1) {
        return null;
    }
}
