package com.ds.mo.engine.transition;

import com.badlogic.gdx.Screen;

/**
 * Created by Mo on 09/11/2017.
 */

public abstract class TransitionEffect {
    protected float duration;     //How long the transition should take
    protected float alpha;

    TransitionEffect(float duration) {
        this.duration = duration;
    }

    /**
     * Between 0 (Invisible) - 1 (Full color)
     *
     * @return transparency value
     */

    abstract void update(float deltaTime);

    abstract void draw(Screen current, Screen next, float deltaTime);

    abstract boolean isFinished();

    float getAlpha(){
        return alpha;
    }

}
