package javax.imageio;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageIO {

	public static BufferedImage read(File input) throws IOException {
		return new BufferedImage(input.getAbsolutePath()); // todo
	}
}
