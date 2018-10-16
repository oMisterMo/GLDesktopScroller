package com.ds.mo.common;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Mo on 01/10/2017.
 */

public class Rectangle {
    public final Vector2 lowerLeft;
    //    public final Vector2D lowerLeft;
    public float width, height;
    public Vector2 center;

    public final Vector2 min, max;

    public Rectangle(float x, float y, float width, float height) {
        this.lowerLeft = new Vector2(x, y);
//        this.lowerLeft = new Vector2D(x, y);
        this.width = width;
        this.height = height;
        this.center = new Vector2(x + width / 2, y + height / 2);

        this.min = new Vector2(x, y);
        this.max = new Vector2(x + width, y + height);
    }

    public boolean contains(Vector2 v) {
        return min.x <= v.x && max.x >= v.x && min.y <= v.y && max.y >= v.y;
    }

    @Override
    public String toString() {
        return "[" + lowerLeft.x + ", " + lowerLeft.y + "]";
    }
}
