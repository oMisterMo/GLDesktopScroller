package com.ds.mo.engine.logic;

import com.badlogic.gdx.math.Vector2;
import com.ds.mo.common.Circle;
import com.ds.mo.common.DynamicGameObject;

public class Boo extends DynamicGameObject {

    public static final float BOO_WIDTH = 16;
    public static final float BOO_HEIGHT = 16;

    public static final int STATE_IDLE = 0;
    public static final int STATE_CHASE = 1;
    private int stateTime;
    public int state;

    public Vector2 facing;

    private static final float SPEED = 10;

    private final Mo player;
    private Vector2 booToPlayer = new Vector2();

    public Circle bounds;

    public Boo(float x, float y, Mo player) {
        super(x, y, BOO_WIDTH, BOO_HEIGHT);
        stateTime = 0;
        state = STATE_IDLE;
//        state = STATE_CHASE;

        this.player = player;
        this.facing = new Vector2(1, 0);
        setInitialFacing();
//        followTarget();
        bounds = new Circle(x + BOO_WIDTH / 2, y + BOO_HEIGHT / 2, BOO_WIDTH / 2);
    }

    private void setInitialFacing() {
        if (position.x < player.position.x) {
            facing.x = 1; //to the left of player
        }
        if (position.x > player.position.x) {
            facing.x = -1;
        }
    }

    public void switchState() {
        if (state == STATE_IDLE) {
//            System.out.println("CHASE NOW");
            state = STATE_CHASE;
        } else {
            state = STATE_IDLE;
//            System.out.println("IDLE NOW");
            accel.set(0, 0);
            velocity.set(0, 0);
        }
        stateTime = 0;
    }

    public void accelerate(Vector2 accel) {
        velocity.add(accel);
    }

    private void followTarget() {
        float x = player.position.x;
        float y = player.position.y + Mo.MO_HEIGHT + 5;


        velocity.set(x - position.x, y - position.y);
        velocity.nor();
        velocity.scl(SPEED);
    }

    private void setFacing() {
        //-1 = LEFT
        //1  = RIGHT
        if (velocity.x < 0) {
            facing.x = -1;
        }
        if (velocity.x > 0) {
            facing.x = 1;
        }
    }

    public void update(float deltaTime) {
        //Points from Monster to Player
        float x = player.position.x - position.x;
        float y = player.position.y - position.y;
        booToPlayer.set(x, y);
        float angle = player.facing.angle(booToPlayer);
//        System.out.println(angle);
        switch (state) {
            case STATE_IDLE:
                int lim = 70;
                if (angle < lim && angle > -lim) {
                    switchState();
                    return;
                }
                stateTime++;
                break;
            case STATE_CHASE:
                if (angle < -120 || angle > 120) {
                    switchState();
                    return;
                }
                followTarget();
                setFacing();
                stateTime++;
                break;
        }
//        velocity.add(accel);
        //or
//        accelerate(accel); //velocity.add(accel)
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
//        bounds.lowerLeft.set(position);
        bounds.center.set(position).add(BOO_WIDTH / 2, BOO_HEIGHT / 2);
    }
}
