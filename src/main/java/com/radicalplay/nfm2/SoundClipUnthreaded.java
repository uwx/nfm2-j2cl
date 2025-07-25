package com.radicalplay.nfm2;

import fallk.logmaster.HLogger;

import java.io.ByteArrayInputStream;

public class SoundClipUnthreaded implements SoundClip {

    public boolean loaded;
    int lfrpo = -1;
    int cntcheck = 0;

    public SoundClipUnthreaded(byte[] byteInput) {
        loaded = true;
    }

    @Override
    public void play() {
        lfrpo = -1;
        cntcheck = 5;
    }

    @Override
    public void loop() {
        lfrpo = -2;
        cntcheck = 0;
    }

    @Override
    public void stop() {
        lfrpo = -1;
    }

}
