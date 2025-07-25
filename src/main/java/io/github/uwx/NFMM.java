package io.github.uwx;

import com.radicalplay.nfm2.GameSparker;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import jsinterop.annotations.JsType;

import java.awt.*;

import static elemental2.dom.DomGlobal.setTimeout;

@JsType
public class NFMM {
    public void start() {
        GameSparker applet = new GameSparker();
        applet.setSize(670, 400);
        applet.init();
        new GameTicker(applet).run();
    }

    private static class GameTicker {
        private final GameSparker applet;

        public GameTicker(GameSparker applet) {
            this.applet = applet;
        }

        public void run() {
            applet.run();
            update();
        }

        private void update(Object... p0) {
            long msToWait = applet.gameTick();

            setTimeout(this::update, msToWait);
        }
    }
}
