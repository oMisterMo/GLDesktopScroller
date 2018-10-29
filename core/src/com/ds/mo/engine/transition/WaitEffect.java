package com.ds.mo.engine.transition;

import com.badlogic.gdx.Screen;

/**
 * Created by Mo on 14/11/2017.
 */

public class WaitEffect extends TransitionEffect {
    protected boolean finished = false;
    private float currentTime = 0;

    public WaitEffect(float duration){
        super(duration);
        alpha = 1f;
    }
    @Override
    void update(float deltaTime) {
        currentTime += deltaTime;
        if(currentTime >= duration){
            finished = true;
            System.out.println("FINISHED");
            return;
        }
    }

    @Override
    void draw(Screen current, Screen next, float deltaTime) {

    }

    @Override
    public float getAlpha(){
        return alpha;
    }

    @Override
    boolean isFinished() {
        return finished;
    }
}
