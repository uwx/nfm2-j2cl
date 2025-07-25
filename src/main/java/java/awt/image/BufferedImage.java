package java.awt.image;

import j2ts_awt_swing.impl.ImageImpl;

public class BufferedImage extends ImageImpl implements RenderedImage {

	public BufferedImage(String src) {
		super(src);
	}


	@Override
	public int getWidth() {
		return super.getWidth(null);
	}

	@Override
	public int getHeight() {
		return super.getHeight(null);
	}

	@Override
	public int getWidth(ImageObserver obs) {
		return super.getWidth(null);
	}

	@Override
	public int getHeight(ImageObserver obs) {
		return super.getHeight(null);
	}

}
