package java.awt;

import elemental2.core.JsArray;
import elemental2.core.Uint8Array;
import elemental2.dom.Blob;
import elemental2.dom.BlobPropertyBag;
import elemental2.dom.URL;
import j2ts_awt_swing.impl.ImageImpl;
import jsinterop.base.Js;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Toolkit {

	private static Toolkit toolkit;

	private Map<String, Image> images = new HashMap<>();

	public static String getProperty(String key, String defaultValue) {
		return defaultValue;
	}

	public static synchronized Toolkit getDefaultToolkit() {
		if (toolkit == null) {
			toolkit = new Toolkit();
		}
		return toolkit;
	}

	public Image createImage(String filename) {
		try {
			Image image = new ImageImpl(filename);
			images.put(filename, image);
			return image;
		} catch (Exception e) {
			return null;
		}
	}

	public Image getImage(String filename) {
		return createImage(filename);
	}

	@SuppressWarnings({"unchecked", "DataFlowIssue"})
    public Image createImage(byte[] bytes) {
		Uint8Array ui8 = new Uint8Array((JsArray<Double>) Js.uncheckedCast(bytes));
		BlobPropertyBag options = BlobPropertyBag.create();
		options.setType("image/png"); // TODO DETECT MIME
		Blob blob = new Blob(new Blob.ConstructorBlobPartsArrayUnionType[] {
				Blob.ConstructorBlobPartsArrayUnionType.of(ui8)
		}, options);

		String url = URL.createObjectURL(blob);

        return new ImageImpl(url);
	}
}
