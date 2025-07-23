package io.github.uwx;

import com.radicalplay.nfm2.GameSparker;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLDivElement;
import jsinterop.annotations.JsType;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@JsType
public class NFMM {

    public static final String HELLO_WORLD = "Hello J2CL world!";
    public static HTMLCanvasElement canvas;

    public void onModuleLoad() {
        canvas = (HTMLCanvasElement) DomGlobal.document.createElement("canvas");

        DomGlobal.document.body.appendChild(canvas);

        Frame frame = new Frame("UNFM2");// Change this to the name of your preference
        frame.setBackground(new Color(0, 0, 0));
        frame.setIgnoreRepaint(true);
//        frame.setIconImages(getIcons());

        GameSparker applet = new GameSparker();

//        applet.setStub(new DesktopStub());
//        frame.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent windowevent) {
//                exitSequence();
//            }
//        });
        applet.setPreferredSize(new Dimension(670, 400));// The resolution of your game goes here
        frame.add("Center", applet);
        frame.setResizable(false);// If you plan to make you game support changes in resolution, you can comment out this line.
        frame.pack();
        frame.setMinimumSize(frame.getSize());
//        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        applet.init();
        applet.start();
    }

    public String helloWorldString() {
        return HELLO_WORLD;
    }
}
