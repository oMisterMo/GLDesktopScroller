package com.ds.mo.engine.transition;

import com.badlogic.gdx.Screen;
import com.ds.mo.common.Helper;
import com.ds.mo.common.Tween;

/**
 * OPAQUE to TRANSPARENT (1 -> 0)
 * Created by Mo on 09/11/2017.
 */

public class FadeInTransitionEffect extends TransitionEffect {
    protected boolean finished = false;
    private float currentTime = 0;

    public FadeInTransitionEffect(float duration) {
        super(duration);
        alpha = 1f;
    }

    @Override
    void update(float deltaTime) {
        currentTime += deltaTime;
        if(alpha <= 0){
            alpha = 0;
            finished = true;
            System.out.println("FINISHED");
            return;
        }
//        old update
//        alpha = Math.max(0, alpha - Math.max((alpha / 10), 0.005f));

        //Tween update
        alpha = (float) Tween.linearTween(currentTime, 1, -1, duration);
    }

    @Override
    void draw(Screen current, Screen next, float deltaTime) {
        next.render(deltaTime);
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
