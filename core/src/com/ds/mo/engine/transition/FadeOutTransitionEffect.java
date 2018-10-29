package com.ds.mo.engine.transition;

import com.badlogic.gdx.Screen;
import com.ds.mo.common.Helper;
import com.ds.mo.common.Tween;

/**
 * TRANSPARENT to OPAQUE (0 -> 1)
 * Created by Mo on 09/11/2017.
 */

public class FadeOutTransitionEffect extends TransitionEffect {
    protected boolean finished = false;
    private float currentTime = 0;

    public FadeOutTransitionEffect(float duration){
        super(duration);
        alpha = 0f;
    }

    @Override
    void update(float deltaTime) {
        currentTime += deltaTime;
        if(alpha >= 1f){
            finished = true;
            System.out.println("FINISHED");
            return;
        }
//        old update
//        alpha = Math.min(1, alpha + Math.max(((1 - alpha) / 10), 0.005f));

        //Tween update (if tween end -> finished = true)
        alpha = (float) Tween.linearTween(currentTime, 0, 1, duration);
    }

    @Override
    void draw(Screen current, Screen next, float deltaTime) {
        current.render(deltaTime);
        //Draw transition effect above current screen todo transition screen

    }

    @Override
    float getAlpha() {
        return Helper.Clamp(alpha, 0, 1);
    }

    @Override
    boolean isFinished() {
        return finished;
    }
}
