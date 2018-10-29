package com.ds.mo.engine.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.ds.mo.common.DynamicGameObject;
import com.ds.mo.common.Helper;
import com.ds.mo.common.OverlapTester;
import com.ds.mo.engine.MyInputProcessor;

public class FairWorld {

    public CustomLevel level;
    public Mo mo;
    public Boo boo;
    public Goomba goomba;
    public Spider spider;

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
        spider = new Spider(goomba.position.x, goomba.position.y, mo);

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
        this.mo = null;
        mo = new Mo(16, level.center.y);
        this.boo = null;
        boo = new Boo(100, 100, mo);
        this.goomba = null;
        goomba = new Goomba(level.WORLD_WIDTH - Tile.TILE_WIDTH * 2, 100);
        goomba.setFacing(Goomba.LEFT);
        this.spider = null;
        spider = new Spider(goomba.position.x, goomba.position.y, mo);
    }

    private void checkFloorPoint(DynamicGameObject obj) {
        Tile bl = worldToTile(obj.position.x, obj.position.y - 1);
        Tile br = worldToTile(obj.position.x + obj.bounds.width, obj.position.y - 1);
        if (!bl.solid && !br.solid) {
//            System.out.println("left and right POINT: NOT HIT");
            obj.grounded = false;
            obj.inAir = true;
        }
    }

    private void horizontal(DynamicGameObject obj) {
        //if moving left
        int gap = 1;        //push away from tile 1 unit
        if (obj.velocity.x < 0) {
//            System.out.println("Double (checking for LEFT wall");
            for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    if (OverlapTester.intersectRayBounds(obj.leftSen, t.bounds, intersect)) {
                        if (obj.position.x <= t.bounds.lowerLeft.x + t.bounds.width) {
                            obj.position.x = t.bounds.lowerLeft.x + t.bounds.width + gap;
                            obj.bounds.lowerLeft.set(obj.position);
                            obj.velocity.x = 0;
                            return;
                        }
                    }
                }
            }

        }
        //if moving right
        if (obj.velocity.x > 0) {
//            System.out.println("Double (checking for RIGHT wall");
            for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    if (OverlapTester.intersectRayBounds(obj.rightSen, t.bounds, intersect)) {
                        if (obj.position.x + obj.bounds.width >= t.bounds.lowerLeft.x) {
                            obj.position.x = t.bounds.lowerLeft.x - obj.bounds.width - gap;
                            obj.bounds.lowerLeft.set(obj.position);
                            obj.velocity.x = 0;
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean floor(DynamicGameObject obj) {
        if (obj.velocity.y < 0) {
            //If object is moving down
            for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    if (OverlapTester.intersectRayBounds(obj.leftFoot, t.bounds, intersect) ||
                            OverlapTester.intersectRayBounds(obj.rightFoot, t.bounds, intersect)) {
                        if (obj.position.y <= intersect.y) {
                            obj.grounded = true;
                            obj.inAir = false;
                            obj.position.y = t.bounds.lowerLeft.y + t.bounds.height;
                            obj.bounds.lowerLeft.set(obj.position);
                            obj.velocity.y = 0;
                            return true;
                        }
                    }
                }//end floor check loop
            }
        }
        return false;
    }

    private void ceiling(DynamicGameObject obj) {
        if (obj.velocity.y > 0) {
            //If object is moving up
            for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    int gap = 0;
                    if (OverlapTester.intersectRayBounds(obj.leftHead, t.bounds, intersect) ||
                            OverlapTester.intersectRayBounds(obj.rightHead, t.bounds, intersect)) {
                        if (obj.position.y + obj.bounds.height > intersect.y) {
                            obj.position.y = intersect.y - obj.bounds.height - gap;
                            obj.bounds.lowerLeft.set(obj.position);
                            obj.velocity.y = 0;
                        }
                    }
                }
            }
        }
    }

    private void playerCollisions() {
        if (mo.dead) return;
        playerBadTiles();
        playerWallCollisions();
        playerEnemyCollisions();
    }

    private void playerBadTiles() {
        //If all bad tiles are stationary can check to see if mo.isMoving() first
        if (mo.isMoving()) {
            System.out.println("doing double");
            for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    if (OverlapTester.overlapRectangles(mo.bounds, t.bounds)) {
                        if (t.death) {
                            System.out.println("Mo hit bad tile");
                            mo.die();
                            return;
                        }
                    }
                }
            }
        }
    }

    private void playerWallCollisions() {
        /*Grounded*/
        if (mo.grounded) {
            checkFloorPoint(mo);
            horizontal(mo);
        }
        /*Airborne*/
        if (mo.inAir) {
            horizontal(mo);
            floor(mo);
            ceiling(mo);
        }
    }

    private void playerEnemyCollisions() {
        if (mo.hurt) {
//            System.out.println("ignoring collisions while hurt");
            return;
        }
        if (OverlapTester.overlapRectangles(goomba.bounds, mo.bounds)) {
            if ((mo.velocity.y < 0)) {
//            if ((mo.ysp < 0) && (mo.position.y > goomba.position.y + (Goomba.GOOMBA_HEIGHT / 2) - 3)) {
                mo.position.y = goomba.position.y + Goomba.GOOMBA_HEIGHT;   //push out of enemy
                mo.jumpOnEnemy();
            } else {
                mo.hurt();
            }
        }
        if (OverlapTester.overlapCircleRectangle(boo.bounds, mo.bounds)) {
//        if(OverlapTester.overlapRectangles(mo.bounds, boo.bounds)){
            mo.hurt();
        }
        if (OverlapTester.overlapRectangles(spider.bounds, mo.bounds)) {
            mo.die();
        }
    }

    private void enemyCollisions() {
        goombaCollision();
        spiderCollision();
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

    private void goombaInBounds() {
        //Walk left/right
        if (goomba.position.x > level.WORLD_WIDTH - Goomba.GOOMBA_WIDTH) {
            goomba.reverseXdir();
            return;
        }
        if (goomba.position.x <= 0) {
            goomba.reverseXdir();
            return;
        }
    }

    private void goombaCollision() {
        if (goomba.grounded) {
            // TODO: 10/10/2018 collisions with /enemies/
            goombaInBounds();       //bounce off pos 0 - WORLD_WIDTH
            horizontalGoomba();
            //Check if grounded
//            checkFloorGoomba();
            checkFloorPoint(goomba);
        }
        if (goomba.inAir) {
            //Solid tile below check
            floor(goomba);
        }
    }

    private void spiderCollision() {
        if (spider.grounded) {
            checkFloorPoint(spider);
            horizontal(spider);
        }
        if (spider.inAir) {
            horizontal(spider);
//            boolean b = floor(spider);
            if(floor(spider)){
                //true if just hit ground
                spider.velocity.x = 0;
                spider.switchState(Spider.STATE_IDLE);
            }
//            System.out.println(b);
        }
    }

    private void collisions() {
        playerCollisions(); //player -> wall / enemies
        enemyCollisions();
    }

    public void update(float deltaTime) {
        scaleTime = (Gdx.input.isKeyPressed(Input.Keys.P)) ? 0.2f : 1;
        deltaTime *= scaleTime;
        /*update monsters*/
        boo.update(deltaTime);
        goomba.update(deltaTime);
        spider.update(deltaTime);

        /*update player -> bound to world*/
        mo.update(deltaTime);
        mo.position.x = Helper.Clamp(mo.position.x,
                2, level.WORLD_WIDTH - Mo.MO_WIDTH - 2);
        collisions();
        mo.updateRay();
    }
}
