package com.ds.mo.common;

import com.badlogic.gdx.math.Vector2;

public class Ray {

    public final Vector2 start = new Vector2();
    public final Vector2 direction = new Vector2();
//    public final Vector2D start = new Vector2D();
//    public final Vector2D direction = new Vector2D();

    public Ray(Vector2 start, Vector2 direction) {
        this.start.set(start);
        this.direction.set(direction).nor();
    }

    public Vector2 getEndPoint(final Vector2 out, final float distance) {
        return out.set(direction).scl(distance).add(start);
    }

    public Ray copy() {
        return new Ray(this.start, this.direction);
    }

    public Ray set(Vector2 start, Vector2 direction) {
        this.start.set(start);
        this.direction.set(direction);
        return this;
    }

    public Ray set(Ray ray) {
        this.start.set(ray.start);
        this.direction.set(ray.direction);
        return this;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Ray r = (Ray) o;
        return this.direction.equals(r.direction) && this.start.equals(r.start);
    }

    public String toString() {
        return "ray [" + start + ":" + direction + "]";
    }

}
