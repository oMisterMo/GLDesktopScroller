package com.ds.mo.engine.logic;

import com.badlogic.gdx.math.Vector2;
import com.ds.mo.common.DynamicGameObject;
import com.ds.mo.common.Helper;
import com.ds.mo.common.Ray;

public class Goomba extends DynamicGameObject {
    public static final float GOOMBA_WIDTH = 16 ;
    public static final float GOOMBA_HEIGHT = 16;

    public static final int RIGHT = 1;
    public static final int LEFT = -1;

    private float speed;

    private static final int TERMINAL_VELOCITY = 16;
    public static final float GRAV = -10 ;
    public static final Vector2 gravity = new Vector2(0, GRAV);
    public boolean grounded;
    public boolean inAir;

    public Ray leftFoot;
    public Ray rightFoot;
    public Ray horizontal;

    public Vector2 facing;

    public float stateTime;
    private Vector2 temp = new Vector2();

    public Goomba(float x, float y) {
        super(x, y, GOOMBA_WIDTH, GOOMBA_HEIGHT);

        grounded = false;
        inAir = true;
        speed = 60 / 4;  //temp speed

        facing = new Vector2(RIGHT, 0);
        initRay();

        stateTime = 0;
    }

    private void initRay() {
//        Vector2 temp = new Vector2();
        temp.set(position);
        temp.x += 2;
        temp.y += GOOMBA_HEIGHT / 2;
        leftFoot = new Ray(temp, new Vector2(0, -1));
        temp.x = position.x + GOOMBA_WIDTH - 2;
        rightFoot = new Ray(temp, new Vector2(0, -1));
    }

    public boolean isMoving() {
        return velocity.x < 0 || velocity.x > 0 || velocity.y < 0 || velocity.y > 0;
    }

    public void reverseXdir() {
        speed *= -1;
        if (speed > 0) facing.x = RIGHT;
        if (speed < 0) facing.x = LEFT;
    }

    private void updateRay(){
        temp.set(position);
        temp.x += 2;
        temp.y += GOOMBA_HEIGHT / 2;
        leftFoot.start.set(temp);
        temp.x = position.x + GOOMBA_WIDTH - 2;
        rightFoot.start.set(temp);
    }

    public void update(float deltaTime) {
        //Move enemy
        velocity.x = speed * deltaTime;

        //Apply gravity if in air
        if (inAir) {
            velocity.add(gravity.x * deltaTime, gravity.y * deltaTime);
            velocity.y = Helper.Clamp(velocity.y, -TERMINAL_VELOCITY * 3, TERMINAL_VELOCITY * 3);
        }

        stateTime += deltaTime;
        //Update position
        position.add(velocity);
        bounds.lowerLeft.set(position);

        updateRay();
    }
}
