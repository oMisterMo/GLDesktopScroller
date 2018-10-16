package com.ds.mo.common;

/**
 * A single instance of a particles
 * Created by Mo on 04/01/2017.
 */

import com.badlogic.gdx.math.Vector2;

/**
 * 2-JAN-2016, 01:12:17.
 *
 * @author Mo
 */
public class Particle extends DynamicGameObject {
    public Vector2 center;
    public float width, height;
    //Particles lifespan in seconds
    public float age;

    //Range between 0.0 - 1.0 (resistance on x axis)
    public float dampening;
    //Rotation
    public float rotation;
    public float rotationDamp;
    public float rotationVel;
    //Scale
    public float scale;  //or do I only scale one axis 0.0 -> scaleMax
    public float scaleVel;
    public float scaleAcc;
    public float scaleMax;
    //Color
    public Color color;// = new Color(Color.WHITE);
    public Color initColor;
    public Color finalColor;
    public float fadeAge;

    public float startTime;
    public boolean dead;
//    protected float staticAge;    //only for tweening +ADD MAYBE???

    public Particle(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.center = new Vector2(x - width / 2, y - width / 2);
        this.width = width;
        this.height = height;
        startTime = System.currentTimeMillis();
        dead = false;

        color = new Color();
        initColor = new Color();
        finalColor = new Color();
    }

    public Particle(float x, float y, float width, float height,
                    float age, float damp,
                    float initRot, float initRotVel, float initRotDamp,
                    float initScale, float initScaleVel, float initScaleAcc,
                    float initScaleMax) {
        super(x, y, width, height);

        this.width = width;
        this.height = height;
        center = new Vector2(x - width / 2, y - width / 2);
        //Set main stuff
        this.age = age;
        this.dampening = damp;
        this.rotation = initRot;
        this.rotationVel = initRotVel;
        this.rotationDamp = initRotDamp;

        this.scale = initScale;
        this.scaleVel = initScaleVel;
        this.scaleAcc = initScaleAcc;
        this.scaleMax = initScaleMax;

        startTime = System.currentTimeMillis();
        dead = false;
    }

    public Particle(float x, float y, float width, float height,
                    float age, float damp,
                    float initRot, float initRotVel, float initRotDamp,
                    float initScale, float initScaleVel, float initScaleAcc,
                    float initScaleMax,
                    Color initColor, Color finalColor, float fadeAge) {
        this(x, y, width, height,
                age, damp,
                initRot, initRotVel, initRotDamp,
                initScale, initScaleVel, initScaleAcc,
                initScaleMax);
        this.color = new Color(initColor);
        this.initColor = initColor;
        this.finalColor = finalColor;
        this.fadeAge = fadeAge;
    }

    private void printInfo() {
        // TODO: 08/03/2018 Change to Log.d();
        System.out.println(position);
        System.out.println(velocity);
        System.out.println(accel);
        System.out.println("width: " + width);
        System.out.println("height: " + height);
        System.out.println(color);
    }

    public void setDead(boolean b) {
        dead = b;
    }

    public boolean isDead() {
        return dead;
    }

    public void setRandomColor() {
        color.set(Helper.Random(0f, 1f), Helper.Random(0f, 1f), Helper.Random(0f, 1f), 1f);
    }

    public void setColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
    }

    private void updatePos(float deltaTime) {
        /*
         vel (m/s) gravitiy (m/s), deltaTime in seconds(s), position(m)
         Every second we update the velocity
         */

        velocity.scl(dampening);
        //Update velocities position by applying gravity (change in vel = accel)
        velocity.add(accel.x * deltaTime, accel.y * deltaTime);
        //Update the particles position
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);


        //Update hitbox
//        bounds.lowerLeft.add(velocity.x * deltaTime, velocity.y * deltaTime);
        bounds.lowerLeft.set(position);
//        Log.d("Particle","bounds: "+bounds);
    }

    private void updateRot(float deltaTime) {
        rotation *= rotationDamp;
        rotation += rotationVel * deltaTime;
//        Log.d("Particle", "rotation: " + rotation);
    }

    private void updateScale(float deltaTime) {
        scaleVel += scaleAcc * deltaTime;
        scale += scaleVel * deltaTime;
        scale = Helper.Clamp(scale, 0.0f, scaleMax);
//        System.out.println("scale = " + scale);
    }

    private void updateCol(float deltaTime) {
        //if age is greater than start-fading age
        if ((age > fadeAge) && (fadeAge != 0)) {
            color = initColor;
//            System.out.println("first col");
//            Log.d("Particle", "First color (red)");
        } else {
            //interpolate color value
//            System.out.println("blending colors");
//            Log.d("Particle", "blending colors");
            float initC = (float) (age / fadeAge);
            float finalC = 1.0f - initC;

            float red = (initColor.r * initC) + (finalColor.r * finalC);
            float green = (initColor.g * initC) + (finalColor.g * finalC);
            float blue = (initColor.b * initC) + (finalColor.b * finalC);
            float alpha = (initColor.a * initC) + (finalColor.a * finalC);

//            System.out.println("red: " + red);
//            System.out.println("green = " + green);
//            System.out.println("blue = " + blue);
//            System.out.println("alpha = " + alpha + "\n");
            // TODO: 12/09/2018 Find a way to make "setColor" work
            color = new Color(red, green, blue, alpha);
//            color.set(red, green, blue, alpha);
        }
        //Set color of sprite here
    }

    public void update(float deltaTime) {
        //dt = 0.016 (60fps) dt = 0.032 (30fps)
        age -= deltaTime;         //in ms
        if (age < 0) {
            age = 0;
            dead = true;
            return;
        }
//        System.out.println("age: "+age);

        /* Test one by one */
        updatePos(deltaTime);
        updateRot(deltaTime);
        updateScale(deltaTime);
        updateCol(deltaTime);
    }
}

