package com.ds.mo.engine.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.ds.mo.common.Helper;
import com.ds.mo.common.OverlapTester;
import com.ds.mo.engine.MyInputProcessor;

public class FairWorld {

    public CustomLevel level;
    public Mo mo;
    public Boo boo;
    public Goomba goomba;

    //Temp variable to store the intersection point of a Ray object
    private Vector2 intersect = new Vector2();
    private float scaleTime = 1;

    public FairWorld() {
        level = new CustomLevel(20, 11);
        level.loadLevel("level1.json");
        //Or set solid tiles as you load the level
//        level.setSolidTiles();

        mo = new Mo(16, level.center.y);    //initially in air
        boo = new Boo(100, 100, mo);
        goomba = new Goomba(level.WORLD_WIDTH - Tile.TILE_WIDTH * 2, 100);
        goomba.setFacing(Goomba.LEFT);

    }

    /**
     * Given an x coordinate -> returns the tile position of the array
     *
     * @param x position
     * @return tiles x coordinate
     */
    private int pointToTileCoordsX(float x) {
//        System.out.println((int) Math.floor(x / Tile.TILE_WIDTH));
        return (int) Math.floor(x / Tile.TILE_WIDTH);
    }

    /**
     * Given an y coordinate -> returns the tile position of the array
     *
     * @param y position
     * @return tiles y coordinate
     */
    private int pointToTileCoordsY(float y) {
//        System.out.println((int) Math.floor(y / Tile.TILE_WIDTH));
        return (int) Math.floor(y / Tile.TILE_HEIGHT);
    }

    /**
     * Given any point -> returns a tile at the position
     *
     * @param x position
     * @param y position
     * @return tile at [x,y]
     */
    private Tile worldToTile(float x, float y) {
        return level.getTile(pointToTileCoordsX(x), pointToTileCoordsY(y));
    }

    public void restart() {
        this.boo = null;
        boo = new Boo(100, 100, mo);
        this.goomba = null;
        goomba = new Goomba(0, 100);
    }

    private void checkFloorPoint() {
        Tile bl = worldToTile(mo.position.x, mo.position.y - 1);
        Tile br = worldToTile(mo.position.x + Mo.MO_WIDTH, mo.position.y - 1);
        if (!bl.solid && !br.solid) {
            System.out.println("left and right POINT: NOT HIT");
//            left_hit = right_hit = false;

            mo.grounded = false;
            mo.inAir = true;
        }
    }

    private void horizontal() {
        //if moving left
        int gap = 1;        //push away from tile 1 unit
        if (mo.xsp < 0) {
//            System.out.println("Double (checking for LEFT wall");
            for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    if (OverlapTester.intersectRayBounds(mo.leftSen, t.bounds, intersect)) {
                        if (mo.position.x <= t.bounds.lowerLeft.x + t.bounds.width) {
                            mo.position.x = t.bounds.lowerLeft.x + t.bounds.width + gap;

                            mo.bounds.lowerLeft.set(mo.position);
                            mo.xsp = 0;
                            return;
                        }
                    }
                }
            }

        }
        //if moving right
        if (mo.xsp > 0) {
//            System.out.println("Double (checking for RIGHT wall");
            for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    if (OverlapTester.intersectRayBounds(mo.rightSen, t.bounds, intersect)) {
                        if (mo.position.x + Mo.MO_WIDTH >= t.bounds.lowerLeft.x) {
                            mo.position.x = t.bounds.lowerLeft.x - Mo.MO_WIDTH - gap;
                            mo.bounds.lowerLeft.set(mo.position);
                            mo.xsp = 0;
                            return;
                        }
                    }
                }
            }
        }
    }

    private void floor() {
        if (mo.ysp < 0) {
            //If player is moving down
            for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    if (OverlapTester.intersectRayBounds(mo.leftFoot, t.bounds, intersect) ||
                            OverlapTester.intersectRayBounds(mo.rightFoot, t.bounds, intersect)) {
                        if (mo.position.y <= intersect.y) {
                            mo.grounded = true;
                            mo.inAir = false;
                            mo.position.y = t.bounds.lowerLeft.y + t.bounds.height;
                            mo.bounds.lowerLeft.set(mo.position);
                            mo.ysp = 0;
                            return;
                        }
                    }
                }//end floor check loop
            }
        }
    }

    private void ceiling() {
        if (mo.ysp > 0) {
            //If player is moving up
            for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    int gap = 0;
                    if (OverlapTester.intersectRayBounds(mo.leftHead, t.bounds, intersect) ||
                            OverlapTester.intersectRayBounds(mo.rightHead, t.bounds, intersect)) {
                        if (mo.position.y + Mo.MO_HEIGHT > intersect.y) {
                            mo.position.y = intersect.y - Mo.MO_HEIGHT - gap;
                            mo.bounds.lowerLeft.set(mo.position);
                            mo.ysp = 0;
                        }
                    }
                }
            }
        }
    }

    private void playerCollisions() {
        playerWallCollisions();
        playerEnemyCollisions();
    }

    private void playerWallCollisions() {
        /*Grounded*/
        if (mo.grounded) {
            checkFloorPoint();
            horizontal();
        }
        /*Airborne*/
        if (mo.inAir) {
            horizontal();
            floor();
            ceiling();
        }
    }

    private void playerEnemyCollisions() {
        if(mo.hurt){
//            System.out.println("ignoring collisions while hurt");
            return;
        }
        if (OverlapTester.overlapRectangles(mo.bounds, goomba.bounds)) {
            if ((mo.ysp < 0)) {
//            if ((mo.ysp < 0) && (mo.position.y > goomba.position.y + (Goomba.GOOMBA_HEIGHT / 2) - 3)) {
                mo.position.y = goomba.position.y + Goomba.GOOMBA_HEIGHT;   //push out of enemy
                mo.jumpOnEnemy();
            } else {
                System.out.println("ysp: "+mo.ysp);
                mo.hurt();
            }
        }
        if(OverlapTester.overlapCircleRectangle(boo.bounds, mo.bounds)){
//        if(OverlapTester.overlapRectangles(mo.bounds, boo.bounds)){
            mo.hurt();
        }
    }

    private void enemyCollisions() {
        goombaCollision();
    }

    private void checkFloorGoomba() {
        Tile bl = worldToTile(goomba.position.x, goomba.position.y - 1);
        Tile br = worldToTile(goomba.position.x + Goomba.GOOMBA_WIDTH, goomba.position.y - 1);
        if (!bl.solid && !br.solid) {
            System.out.println("left and right POINT: NOT HIT");
//            left_hit = right_hit = false;

            goomba.grounded = false;
            goomba.inAir = true;
        }
    }

    private void horizontalGoomba() {
        //if moving left
        int gap = 1;        //push away from tile 1 unit
        if (goomba.velocity.x < 0) {
//            System.out.println("Double (checking for LEFT wall");
            for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    if (OverlapTester.intersectRayBounds(goomba.leftSen, t.bounds, intersect)) {
                        if (goomba.position.x <= t.bounds.lowerLeft.x + t.bounds.width) {
                            goomba.position.x = t.bounds.lowerLeft.x + t.bounds.width + gap;
                            goomba.bounds.lowerLeft.set(goomba.position);
                            goomba.velocity.x = 0;
                            //Only difference between player/enemy hor collision is line below
                            goomba.reverseXdir();
                            return;
                        }
                    }
                }
            }
        }
        //if moving right
        if (goomba.velocity.x > 0) {
//            System.out.println("Double (checking for RIGHT wall");
            for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    if (OverlapTester.intersectRayBounds(goomba.rightSen, t.bounds, intersect)) {
                        if (goomba.position.x + Goomba.GOOMBA_WIDTH >= t.bounds.lowerLeft.x) {
                            goomba.position.x = t.bounds.lowerLeft.x - Goomba.GOOMBA_WIDTH - gap;
                            goomba.bounds.lowerLeft.set(goomba.position);
                            goomba.velocity.x = 0;
                            goomba.reverseXdir();
                            return;
                        }
                    }
                }
            }
        }
    }

    private void goombaCollision() {
        if (goomba.grounded) {
            // TODO: 10/10/2018 collisions with walls/enemies/ground
            //Walk left/right
            if (goomba.position.x > level.WORLD_WIDTH - Goomba.GOOMBA_WIDTH) {
                goomba.reverseXdir();
                return;
            }
            if (goomba.position.x <= 0) {
                goomba.reverseXdir();
                return;
            }
            horizontalGoomba();
            //Check if grounded
            checkFloorGoomba();
        }
        if (goomba.inAir) {
            //Solid tile below check
            if (goomba.velocity.y < 0) {
                //If player is moving down
                for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
                    for (int x = 0; x < level.NO_OF_TILES_X; x++) {
                        Tile t = level.tiles[y][x];
                        if (!t.solid) continue;
                        if (OverlapTester.intersectRayBounds(goomba.leftFoot, t.bounds, intersect) ||
                                OverlapTester.intersectRayBounds(goomba.rightFoot, t.bounds, intersect)) {
                            if (goomba.position.y <= intersect.y) {
                                goomba.grounded = true;
                                goomba.inAir = false;
                                goomba.position.y = t.bounds.lowerLeft.y + t.bounds.height;
                                goomba.bounds.lowerLeft.set(goomba.position);
                                goomba.velocity.y = 0;
                                return;
                            }
                        }
                    }
                }//end floor check loop
            }
        }
    }

    private void collisions() {
        playerCollisions(); //player -> wall / enemies
        enemyCollisions();
    }

    public void update(float deltaTime) {
        scaleTime = (Gdx.input.isKeyPressed(Input.Keys.P)) ?  0.2f : 1;
        deltaTime *= scaleTime;
        /*update monsters*/
        boo.update(deltaTime);
        goomba.update(deltaTime);
        /*update player*/
        mo.update(deltaTime);
        //Bound to world
        mo.position.x = Helper.Clamp(mo.position.x,
                2, level.WORLD_WIDTH - Mo.MO_WIDTH - 2);
        collisions();
        mo.updateRay();
    }
}
