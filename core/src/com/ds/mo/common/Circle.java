package com.ds.mo.common;

/**
 * Created by Mo on 01/10/2017.
 */

public class Circle {
    public final Vector2D center = new Vector2D();
    public float radius;

    public Circle(float x, float y, float radius) {
        this.center.set(x, y);
        this.radius = radius;
    }
}
