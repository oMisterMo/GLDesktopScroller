package com.ds.mo.engine.logic;

import com.ds.mo.common.DynamicGameObject;

public class Spider extends DynamicGameObject {
    public static final int SPIDER_WIDTH = 16;
    public static final int SPIDER_HEIGHT = 16;

    public static final int STATE_IDLE = 0;
    public static final int STATE_JUMP = 1;
    public float stateTime;
    public int state;

    //-------------------------------------
    private static final float IDLE_TIME = 3; //second

    private static final int JUMP_HEIGHT = 60;
    private static final int HOR_SPEED = 40;
    //    public static final int JUMP_HEIGHT = 20;
    private final Mo mo;

    private float gravity = -90f;

    public Spider(float x, float y, Mo mo) {
        super(x, y, SPIDER_WIDTH, SPIDER_HEIGHT);
        bounds.height -= 4;
        stateTime = 0;
        state = STATE_IDLE;
        System.out.println("IDLE");
        this.mo = mo;
    }

    public void switchState(int state) {
        if (state < 0 || state > STATE_JUMP) {
            System.out.println("Wrong state");
            return;
        }
        if (state == STATE_IDLE) System.out.println("IDLE");
        if (state == STATE_JUMP) System.out.println("JUMP");
        this.state = state;
        stateTime = 0;
    }

    public void jump() {
//        velocity.x =
        velocity.y = JUMP_HEIGHT;
//        switchState(STATE_IDLE);
    }

    public void update(float deltaTime) {
        if (grounded) {
            switch (state) {
                case STATE_IDLE:
                    stateTime += deltaTime;
                    velocity.x = 0;
                    if (stateTime >= IDLE_TIME) {
                        //Change state to jump
                        switchState(STATE_JUMP);
                    }
                    break;
                case STATE_JUMP:
                    stateTime += deltaTime;
                    //Get direction towards player
                    float x = mo.position.x - position.x;
                    //float y = mo.position.y - position.y;
                    velocity.set(x, 0).nor().scl(HOR_SPEED);
//                    System.out.println("Spider vel.x :"+x);
//                    System.out.println(velocity.x);
                    jump();
                    break;
            }
        }
        if (inAir) {
            velocity.y += gravity * deltaTime;
            //maybe clamp vel.y
        }
//        System.out.println(velocity);
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        bounds.lowerLeft.set(position);
        updateRay();
    }
}
