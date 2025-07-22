package io.github.uwx;

import java.awt.*;

public abstract class AppletPolyfill extends PanelPolyfill {
    protected void repaint() {

    }

    protected void requestFocus() {

    }

    protected void setCursor(Cursor cursor) {

    }

    public abstract void paint(Graphics var1);

    protected int getWidth() {
        return 0;
    }

    protected int getHeight() {
        return 0;
    }

    public abstract void update(Graphics var1);

    public abstract void init();

    protected void setBackground(Color color) {

    }

    protected void setLayout(Object o) {
    }

    protected void add(Component cp) {

    }

    public abstract void start();

    public abstract void stop();

    public abstract boolean mouseUp(Event var1, int var2, int var3);

    public abstract boolean mouseDown(Event var1, int var2, int var3);

    public abstract boolean mouseMove(Event var1, int var2, int var3);

    public abstract boolean mouseDrag(Event var1, int var2, int var3);

    public abstract boolean lostFocus(Event var1, Object var2);

    public abstract boolean gotFocus(Event var1, Object var2);

    public abstract boolean keyDown(Event var1, int var2);

    public abstract boolean keyUp(Event var1, int var2);
}
