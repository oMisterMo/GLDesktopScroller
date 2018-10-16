package com.ds.mo.common;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.ds.mo.engine.android.TextureRegion;

/**
 * Animation of an sprite
 * <p/>
 * n.b. animation is frame dependant, update
 * n.b. add random frame animation
 * <p/>
 * Created by Mo on 14/03/2016.
 */
public class Animation {
    public static final int ANIMATION_LOOPING = 0;
    public static final int ANIMATION_NON_LOOPING = 1;

    final TextureRegion[] keyFrames;
    final float frameDuration;

    public Animation(float frameDuration, TextureRegion... keyFrames) {
        this.frameDuration = frameDuration;
        this.keyFrames = keyFrames;
    }

    public TextureRegion getKeyFrame(float startTime, int mode) {
        int frameNumber = (int) (startTime / frameDuration);
        if (mode == ANIMATION_NON_LOOPING) {
            //Happens before screen is ready
//            System.out.println("value 1: " + (keyFrames.length - 1));
//            System.out.println("Frame num: " + frameNumber);
            //returns the smaller value
            frameNumber = Math.min(keyFrames.length - 1, frameNumber);
        } else {
            frameNumber = frameNumber % keyFrames.length;
        }
        return keyFrames[frameNumber];
    }
}
