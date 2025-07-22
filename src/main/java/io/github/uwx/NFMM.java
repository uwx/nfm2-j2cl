package io.github.uwx;

import com.radicalplay.nfmm.Madness;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLDivElement;
import jsinterop.annotations.JsType;

@JsType
public class NFMM {

    public static final String HELLO_WORLD = "Hello J2CL world!";
    public static HTMLCanvasElement canvas;

    public void onModuleLoad() {
        canvas = (HTMLCanvasElement) DomGlobal.document.createElement("canvas");

        DomGlobal.document.body.appendChild(canvas);

        Madness.main(new String[0]);
    }

    public String helloWorldString() {
        return HELLO_WORLD;
    }
}
