package com.ds.mo.common;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Mo on 01/10/2017.
 */

public final class OverlapTester {

    public static Vector2 v2 = new Vector2();

    private OverlapTester() {
        //Can not instantiate class
    }

    public static boolean overlapCircles(Circle c1, Circle c2) {
        float distance = c1.center.dst2(c2.center);
//        float distance = c1.center.distanceSqr(c2.center);
        float radiusSum = c1.radius + c2.radius;
        return distance <= radiusSum * radiusSum;
    }

    public static boolean overlapRectangles(Rectangle r1, Rectangle r2) {
        return r1.lowerLeft.x < r2.lowerLeft.x + r2.width &&
                r1.lowerLeft.x + r1.width > r2.lowerLeft.x &&
                r1.lowerLeft.y < r2.lowerLeft.y + r2.height &&
                r1.lowerLeft.y + r1.height > r2.lowerLeft.y;
    }

    public static boolean overlapCircleRectangle(Circle c, Rectangle r) {
        float nearestX = c.center.x;
        float nearestY = c.center.y;

        //circle is on the left
        if (c.center.x < r.lowerLeft.x) {
            nearestX = r.lowerLeft.x;
        } else if (c.center.x > r.lowerLeft.x + r.width) {
            //circle is on the right
            nearestX = r.lowerLeft.x + r.width;
        }
        //circle is above
        if (c.center.y < r.lowerLeft.y) {
            nearestY = r.lowerLeft.y;
        } else if (c.center.y > r.lowerLeft.y + r.height) {
            //circle in below
            nearestY = r.lowerLeft.y + r.height;
        }

        return c.center.dst2(nearestX, nearestY) < c.radius * c.radius;
    }

    public static boolean pointInCircle(Circle c, Vector2 p) {
        return c.center.dst2(p) < c.radius * c.radius;
    }

    public static boolean pointInCircle(Circle c, float x, float y) {
        return c.center.dst2(x, y) < c.radius * c.radius;
    }

    public static boolean pointInRectangle(Rectangle r, Vector2D p) {
        return r.lowerLeft.x <= p.x && r.lowerLeft.x + r.width >= p.x &&
                r.lowerLeft.y <= p.y && r.lowerLeft.y + r.height >= p.y;
    }

    public static boolean pointInRectangle(Rectangle r, float x, float y) {
        return r.lowerLeft.x <= x && r.lowerLeft.x + r.width >= x &&
                r.lowerLeft.y <= y && r.lowerLeft.y + r.height >= y;
    }

    public static boolean intersectRayBoundsFast(Ray ray, Vector2 bounds) {
//        final float scaleX = 1.0 / delta.x;
//        final float scaleY = 1.0 / delta.y;
//        final float signX = Helper.Sign(scaleX);
//        final float signY = Helper.Sign(scaleY);
//        final float nearTimeX = (this.pos.x - signX * (this.half.x + paddingX) - pos.x) * scaleX;
//        final float nearTimeY = (this.pos.y - signY * (this.half.y + paddingY) - pos.y) * scaleY;
//        final float farTimeX = (this.pos.x + signX * (this.half.x + paddingX) - pos.x) * scaleX;
//        final float farTimeY = (this.pos.y + signY * (this.half.y + paddingY) - pos.y) * scaleY;
//
//        if (nearTimeX > farTimeY || nearTimeY > farTimeX) {
//            return false;
//        }
//        final float nearTime = nearTimeX > nearTimeY ? nearTimeX : nearTimeY;
//        final float farTime = farTimeX < farTimeY ? farTimeX : farTimeY;


        return true;
    }

    public static boolean intersect(Ray ray, Rectangle bounds) {
//        Vector2 inv = ray.direction.set(1/ray.direction.x, 1/ray.direction.y);
//        final float minX = bounds.lowerLeft.x;
//        final float maxX = bounds.lowerLeft.x + bounds.width;
//
//        float tmin = (minX - ray.start.x) / ray.direction.x;
//        float tmax= (maxX - ray.start.x) / ray.direction.x;
//
//        /*
//        if (tmin > tmx) swa[(tmin, tmax);
//         */
//        if(inv.x >= 0){
//            tmin = (minX - ray.start.x) * inv.x;
//            tmax = (maxX - ray.start.x) * inv.x;
//        }else{
//            tmin = (maxX - ray.start.x) * inv.x;
//            tmax = (minX - ray.start.x) * inv.x;
//        }
//
//        //---------------------------------------------------------------
//        float tymin = (min.y - r.orig.y) / r.dir.y;
//        float tymax = (max.y - r.orig.y) / r.dir.y;
//
//        if (tymin > tymax) swap(tymin, tymax);
//
//        if ((tmin > tymax) || (tymin > tmax))
//            return false;
//
//        if (tymin > tmin)
//            tmin = tymin;
//
//        if (tymax < tmax)
//            tmax = tymax;
//
//        float tzmin = (min.z - r.orig.z) / r.dir.z;
//        float tzmax = (max.z - r.orig.z) / r.dir.z;
//
//        if (tzmin > tzmax) swap(tzmin, tzmax);
//
//        if ((tmin > tzmax) || (tzmin > tmax))
//            return false;
//
//        if (tzmin > tmin)
//            tmin = tzmin;
//
//        if (tzmax < tmax)
//            tmax = tzmax;

        return true;
    }

    public static boolean intersectRayBounds(Ray ray, Rectangle bounds, Vector2 intersection) {
        if (bounds.contains(ray.start)) {
            if (intersection != null) intersection.set(ray.start);
            return true;
        }
        float lowest = 0, t;
        boolean hit = false;

        // min x
        if (ray.start.x <= bounds.min.x && ray.direction.x > 0) {
            t = (bounds.min.x - ray.start.x) / ray.direction.x;
            if (t >= 0) {
                v2.set(ray.direction).scl(t).add(ray.start);
                if (v2.y >= bounds.min.y && v2.y <= bounds.max.y && (!hit || t < lowest)) {
                    hit = true;
                    lowest = t;
                }
            }
        }
        // max x
        if (ray.start.x >= bounds.max.x && ray.direction.x < 0) {
            t = (bounds.max.x - ray.start.x) / ray.direction.x;
            if (t >= 0) {
                v2.set(ray.direction).scl(t).add(ray.start);
                if (v2.y >= bounds.min.y && v2.y <= bounds.max.y && (!hit || t < lowest)) {
                    hit = true;
                    lowest = t;
                }
            }
        }
        // min y
        if (ray.start.y <= bounds.min.y && ray.direction.y > 0) {
            t = (bounds.min.y - ray.start.y) / ray.direction.y;
            if (t >= 0) {
                v2.set(ray.direction).scl(t).add(ray.start);
                if (v2.x >= bounds.min.x && v2.x <= bounds.max.x && (!hit || t < lowest)) {
                    hit = true;
                    lowest = t;
                }
            }
        }
        // max y
        if (ray.start.y >= bounds.max.y && ray.direction.y < 0) {
            t = (bounds.max.y - ray.start.y) / ray.direction.y;
            if (t >= 0) {
                v2.set(ray.direction).scl(t).add(ray.start);
                if (v2.x >= bounds.min.x && v2.x <= bounds.max.x && (!hit || t < lowest)) {
                    hit = true;
                    lowest = t;
                }
            }
        }
//        // min z
//        if (ray.start.z <= bounds.min.z && ray.direction.z > 0) {
//            t = (bounds.min.z - ray.start.z) / ray.direction.z;
//            if (t >= 0) {
//                v2.set(ray.direction).scl(t).add(ray.start);
//                if (v2.x >= bounds.min.x && v2.x <= bounds.max.x && v2.y >= bounds.min.y && v2.y <= bounds.max.y && (!hit || t < lowest)) {
//                    hit = true;
//                    lowest = t;
//                }
//            }
//        }
//        // max z
//        if (ray.start.z >= bounds.max.z && ray.direction.z < 0) {
//            t = (bounds.max.z - ray.start.z) / ray.direction.z;
//            if (t >= 0) {
//                v2.set(ray.direction).scl(t).add(ray.start);
//                if (v2.x >= bounds.min.x && v2.x <= bounds.max.x && v2.y >= bounds.min.y && v2.y <= bounds.max.y && (!hit || t < lowest)) {
//                    hit = true;
//                    lowest = t;
//                }
//            }
//        }
        if (hit && intersection != null) {
            intersection.set(ray.direction).scl(lowest).add(ray.start);
            if (intersection.x < bounds.min.x) {
                intersection.x = bounds.min.x;
            } else if (intersection.x > bounds.max.x) {
                intersection.x = bounds.max.x;
            }
            if (intersection.y < bounds.min.y) {
                intersection.y = bounds.min.y;
            } else if (intersection.y > bounds.max.y) {
                intersection.y = bounds.max.y;
            }
//            if (intersection.z < bounds.min.z) {
//                intersection.z = bounds.min.z;
//            } else if (intersection.z > bounds.max.z) {
//                intersection.z = bounds.max.z;
//            }
        }
        return hit;
    }

//    static public boolean intersectRayBoundsFast (Ray ray, Rectangle box) {
//        return intersectRayBoundsFast(ray, box.getCenter(tmp1), box.getDimensions(tmp2));
//    }
}
