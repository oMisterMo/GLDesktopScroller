package com.ds.mo.common;

import com.badlogic.gdx.math.Vector2;
import com.ds.mo.engine.logic.Mo;

/**
 * Created by Mo on 30/09/2017.
 */

public class DynamicGameObject extends GameObject {
    public final Vector2 velocity;
    public final Vector2 accel;
//    public final Vector2D velocity;
//    public final Vector2D accel;
    public boolean grounded;
    public boolean inAir;

    //Sensors
    public Ray leftFoot;
    public Ray rightFoot;
    public Ray rightSen;
    public Ray leftSen;
    public Ray leftHead;
    public Ray rightHead;
    private Vector2 temp = new Vector2();

    public DynamicGameObject(float x, float y, float width, float height) {
        super(x, y, width, height);
        velocity = new Vector2();
        accel = new Vector2();
//        velocity = new Vector2D();
//        accel = new Vector2D();

        grounded = false;
        inAir = true;

        initRay();
    }

    private void initRay(){
        temp.set(position);
        temp.x += 2;
        temp.y += bounds.width / 2;
        leftFoot = new Ray(temp, new Vector2(0, -1));
        leftHead = new Ray(temp, new Vector2(0, 1));
        temp.x = position.x + bounds.width;
        temp.x -= 2;
        rightFoot = new Ray(temp, new Vector2(0, -1));
        rightHead = new Ray(temp, new Vector2(0, 1));
        //Set temp to center of player (for left/right sensor)
        temp.set(position);
        temp.x += bounds.width / 2;
        temp.y += bounds.width / 2;
        leftSen = new Ray(temp, new Vector2(-1, 0));
        rightSen = new Ray(temp, new Vector2(1, 0));
    }

    public void updateRay(){
        //Set temp to mo.position (bottom left)
        temp.set(position);
        //Left sensor (shift 2 pixels into player)
        temp.x += 2;
        temp.y += bounds.width/ 2;
        leftFoot.start.set(temp);
        leftHead.start.set(temp);
        //Right sensor (shift 2 pixels into player)
        temp.x = position.x + bounds.width;
        temp.x -= 2;
        rightFoot.start.set(temp);
        rightHead.start.set(temp);
        //Set temp to center of player (for left/right sensor)
        temp.set(position);
        temp.x += bounds.width / 2;
        temp.y += bounds.width / 2;
        leftSen.start.set(temp);
        rightSen.start.set(temp);
    }
}
