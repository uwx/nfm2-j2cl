package com.radicalplay.nfm2;

import elemental2.core.JsMath;

/**
 * Contains many of the math functions for various purposes.
 *
 * @author Rafa, Kaffeinated, Omar Wally
 */
class RadicalMath {
    static public float cos(float i) {
        return (float) JsMath.cos(i * 0.01745329251994329576922);
    }

    static public float sin(float i) {
        return (float) JsMath.sin(i * 0.01745329251994329576922);
    }
}
