package com.radicalplay.nfm2;

import fallk.logmaster.HLogger;

public class SoundClipThreaded implements Runnable, SoundClip {

    private byte[] clipBytes;
    boolean isPlaying = false;
    boolean loaded;

    public SoundClipThreaded(byte[] byteInput) {
        try {
            this.clipBytes = byteInput;
            loaded = true;
        } catch (Exception e) {
            HLogger.error(e);
            loaded = false;
        }
    }

    @Override
    public void play() {
        isPlaying = false;
    }

    @Override
    public void loop() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void run() {
        play();
    }

}
