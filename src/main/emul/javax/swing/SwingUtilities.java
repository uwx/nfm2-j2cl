package javax.swing;

import elemental2.dom.DomGlobal;

import static elemental2.dom.DomGlobal.requestAnimationFrame;

public class SwingUtilities implements SwingConstants {
    private SwingUtilities() {
        // do nothing
    }

    public static void invokeLater(Runnable doRun) {
        requestAnimationFrame(v -> doRun.run());
    }

    public static void invokeAndWait(Runnable doRun) {
        doRun.run();
    }
}
