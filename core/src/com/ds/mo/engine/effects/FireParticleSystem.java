package com.ds.mo.engine.effects;//package com.ds.mo.engine.common;

import com.badlogic.gdx.math.Vector2;
import com.ds.mo.common.Color;
import com.ds.mo.common.Helper;
import com.ds.mo.common.Particle;
import com.ds.mo.engine.logic.Tile;

import java.util.ArrayList;

/**
 * Particle system - holds and array of particles
 * Created by Mo on 04/01/2017.
 */
public class FireParticleSystem {
    //    public int MAX_SIZE = 1;
    private int MAX_SIZE = 100;   //30 also looks good
    private static final Color redFlame = new Color(153 / 255f, 51 / 255f, 25 / 255f, 255 / 255f);
    private static final Color blueFlame = new Color(25 / 255f, 51 / 255f, 153 / 255f, 255 / 255f);

    private static final Color redFlameEnd = new Color(153 / 255f, 51 / 255f, 25 / 255f, 0);
    private static final Color blueFlameEnd = new Color(25 / 255f, 51 / 255f, 153 / 255f, 0);

    public static final int RED = 0;
    public static final int BLUE = 1;
    private int state = RED;

    // TODO: 07/09/2018 OBJECT POOLING

    public Vector2 drawPos;
    public ArrayList<Particle> particles;

    public FireParticleSystem(Vector2 drawPos, int fireState) {
        this.drawPos = drawPos;
        this.particles = new ArrayList<Particle>();
        this.state = fireState;

        for (int i = 0; i < MAX_SIZE; i++) {
            newParticle();
        }
    }

    private void newParticle() {
        Particle p = new Particle(drawPos.x, drawPos.y, Tile.TILE_WIDTH, Tile.TILE_HEIGHT);
        Color startCol;
        Color endCol;
        switch (state) {
            case RED:
                startCol = redFlame;
                endCol = redFlameEnd;
                break;
            case BLUE:
                startCol = blueFlame;
                endCol = blueFlameEnd;
                break;
            default:
                startCol = redFlame;
                endCol = redFlameEnd;
        }
        p.color = new Color(startCol);
        p.initColor = new Color(startCol);
        p.finalColor = new Color(endCol);
        p.fadeAge = 1;
        resetFireParticle(p);
        particles.add(p);
    }


    private void resetFireParticle(Particle p) {
        p.age = Helper.Random();    //0-1
        p.dampening = 1;
        p.position.set(drawPos);
        p.center.set(p.position.x - p.width / 2, p.position.y - p.height / 2);
        p.bounds.width = Tile.TILE_WIDTH;
        p.bounds.height = Tile.TILE_HEIGHT;

        //Velocity
        p.velocity.x = Helper.Random(-6.4f, 6.4f);  //how wide it travels
        p.velocity.y = Helper.Random(-3.2f, 16f);    //how far up it travels
//        p.velocity.x = 0;
//        p.velocity.y = 0;
        //Acceleration
        p.accel.y = 80f;
        //Rotation
        p.rotation = Helper.Random(-190f, 190f);
        p.rotationVel = 30f;
        p.rotationDamp = 1f;
//        //Scale
        p.scale = 1;          //initial scale
        p.scaleVel = 0;
        p.scaleAcc = 0;
        p.scaleMax = 1;
        p.setDead(false);
    }

    /**
     * Loop through list backwards to avoid skipping elements!
     *
     * @param deltaTime
     */
    public void update(float deltaTime) {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update(deltaTime);
//            System.out.println(p.color);

            /* Memory efficient, resets position, doesn't create new particle */
            if (p.isDead()) {
//                Log.d("fps", "isDead, resetting");
                resetFireParticle(p);

//                particles.remove(p);
//                addParticle();
            }
        }
    }
}
