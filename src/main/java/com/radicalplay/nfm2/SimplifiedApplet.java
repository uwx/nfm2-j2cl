package com.radicalplay.nfm2;

import elemental2.dom.*;
import fallk.logmaster.HLogger;
import java_impl.CanvasImage;
import java_impl.WebGraphics2D;
import jsinterop.base.Js;

import java.awt.*;
import java.awt.Event;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static elemental2.dom.DomGlobal.document;
import static elemental2.dom.DomGlobal.window;

public abstract class SimplifiedApplet {
    private final HTMLCanvasElement element;

    public SimplifiedApplet() {
        element = (HTMLCanvasElement) document.createElement("canvas");
        document.body.append(element);

        window.addEventListener("keydown", event -> {
            KeyboardEvent keyboardEvent = (KeyboardEvent) event;
            keyDown(keyboardEvent.code);
        });
        window.addEventListener("keyup", event -> {
            KeyboardEvent keyboardEvent = (KeyboardEvent) event;
            keyUp(keyboardEvent.code);
        });
        element.addEventListener("mousedown", event -> {
            MouseEvent mouseEvent = (MouseEvent) event;
            mouseDown(null, (int) mouseEvent.offsetX, (int) mouseEvent.offsetY);
        });
        element.addEventListener("mousemove", event -> {
            MouseEvent mouseEvent = (MouseEvent) event;
            mouseMove(null, (int) mouseEvent.offsetX, (int) mouseEvent.offsetY);
        });
    }

    public boolean keyDown(String key) {
        return true;
    }

    public boolean keyUp(String key) {
        return true;
    }

    public void stop() {
    }

    public boolean lostFocus(Event event, Object obj) {
        return true;
    }

    public boolean gotFocus(Event event, Object obj) {
        return true;
    }

    public void update(Graphics g) {
    }

    public void start() {
    }

    public boolean mouseDown(Event event, int x, int y) {
        return true;
    }

    public boolean mouseMove(Event event, int x, int y) {
        return true;
    }

    public void init() {
    }

    public void paint(Graphics graphics) {
    }

    public void repaint() {
        HLogger.info("repaint");
        paint(getGraphics());
    }

    private Graphics getGraphics() {
        return new WebGraphics2D(element);
    }

    public Image createImage(int width, int height) {
        return new CanvasImage(width, height);
    }

    public URL getCodeBase() {
        try {
            return new URL(window.location.href);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCursor(Cursor cursor) {
    }

    public int getWidth() {
        return element.clientWidth;
    }

    public int getHeight() {
        return element.clientHeight;
    }

    public void setSize(int width, int height) {
        element.width = width;
        element.height = height;
    }
}
