package com.ds.mo.engine.logic;

import com.badlogic.gdx.math.Vector2;
import com.ds.mo.common.DynamicGameObject;
import com.ds.mo.common.Helper;
import com.ds.mo.common.OverlapTester;

import java.util.ArrayList;

public class PlainWorld {

    public CustomLevel level;
    public Mo mo;

    public ArrayList<Goomba> goombas;

    //Temp variable to store the intersection point of a Ray object
    private Vector2 intersect = new Vector2();

    public PlainWorld() {
        level = new CustomLevel(20, 11);
        level.loadLevel("level2.json");
        //Or set solid tiles as you load the level
//        level.setSolidTiles();

        mo = new Mo(level.center.x, level.center.y+300);    //initially in air

        goombas = new ArrayList<Goomba>();
        for (int i = 0; i < 88; i++) {
            goombas.add(new Goomba(Helper.Random(0, level.WORLD_WIDTH - 150),
                    Helper.Random(16 * 4 + 1, level.WORLD_WIDTH - 150)));
        }
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

    private void floor(DynamicGameObject obj) {
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
                            return;
                        }
                    }
                }//end floor check loop
            }
        }
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
        playerWallCollisions();
        playerEnemyCollisions();
    }

    private void enemyCollisions() {
        goombaCollision();
    }

    private void goombaInBounds(Goomba goomba) {
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
        for (int i = goombas.size() - 1; i >= 0; i--) {
            Goomba goomba = goombas.get(i);
            if (OverlapTester.overlapRectangles(goomba.bounds, mo.bounds)) {
                if ((mo.velocity.y < 0)) {
                    mo.position.y = goomba.position.y + Goomba.GOOMBA_HEIGHT;   //push out of enemy
                    mo.jumpOnEnemy();
                    return;
                } else {
                    mo.hurt();
                    return;
                }
            }
        }

    }

    private void goombaCollision() {
        for (int i = goombas.size() - 1; i >= 0; i--) {
            Goomba goomba = goombas.get(i);
            if (goomba.grounded) {
                // TODO: 10/10/2018 collisions with /enemies/
                goombaInBounds(goomba);       //bounce off pos 0 - WORLD_WIDTH
                horizontalGoomba(goomba);
                //Check if grounded
//            checkFloorGoomba();
                checkFloorPoint(goomba);
            }
            if (goomba.inAir) {
                //Solid tile below check
                floor(goomba);
            }
        }
    }

    private void horizontalGoomba(Goomba goomba) {
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

    private void collisions() {
        playerCollisions();
        enemyCollisions();
    }

    public void update(float deltaTime) {
        //update goombas
        for (int i = goombas.size() - 1; i >= 0; i--) {
            Goomba goomba = goombas.get(i);
            goomba.update(deltaTime);
        }
        //update player
        mo.update(deltaTime);
        //Bound to world
        mo.position.x = Helper.Clamp(mo.position.x,
                2, level.WORLD_WIDTH - Mo.MO_WIDTH - 2);
        collisions();
        mo.updateRay();
    }
}
