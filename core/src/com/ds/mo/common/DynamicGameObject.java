package com.ds.mo.common;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Mo on 30/09/2017.
 */

public class DynamicGameObject extends GameObject {
    public final Vector2 velocity;
    public final Vector2 accel;
//    public final Vector2D velocity;
//    public final Vector2D accel;

    public DynamicGameObject(float x, float y, float width, float height) {
        super(x, y, width, height);
        velocity = new Vector2();
        accel = new Vector2();
//        velocity = new Vector2D();
//        accel = new Vector2D();
    }
}
