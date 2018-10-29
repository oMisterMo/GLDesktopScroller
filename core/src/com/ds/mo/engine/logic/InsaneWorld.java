package com.ds.mo.engine.logic;

import com.badlogic.gdx.math.Vector2;
import com.ds.mo.common.DynamicGameObject;
import com.ds.mo.common.Helper;
import com.ds.mo.common.OverlapTester;

public class InsaneWorld {

    public CustomLevel level;
    public Mo mo;

    //Temp variable to store the intersection point of a Ray object
    private Vector2 intersect = new Vector2();

    public InsaneWorld() {
        level = new CustomLevel(20, 11);
        level.loadLevel("level3.json");
        //Or set solid tiles as you load the level
//        level.setSolidTiles();

        mo = new Mo(level.center.x, level.center.y);    //initially in air
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

    private void collisions() {
        playerCollisions();
    }

    public void update(float deltaTime) {
        mo.update(deltaTime);
        //Bound to world
        mo.position.x = Helper.Clamp(mo.position.x,
                2, level.WORLD_WIDTH - Mo.MO_WIDTH - 2);
        collisions();
        mo.updateRay();
    }
}
