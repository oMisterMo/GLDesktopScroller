package com.ds.mo.engine.logic;

import com.badlogic.gdx.math.Vector2;
import com.ds.mo.common.Helper;
import com.ds.mo.common.OverlapTester;

import java.awt.Point;

public class World {

    public Level level;
    public Mo mo;

    //Doors
    public Point door1, door2, door3;

    //Temp variable to store the intersection point of a Ray object
    private Vector2 intersect = new Vector2();

    public World() {
        level = new Level();
        mo = new Mo(16 * 3, 16 * 8);

        door1 = new Point(20, Level.NO_OF_TILES_Y - 1 - 7);
        door2 = new Point(31, Level.NO_OF_TILES_Y - 1 - 7);
        door3 = new Point(43, Level.NO_OF_TILES_Y - 1 - 7);
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
        System.out.println("R");
        this.mo = null;
        mo = new Mo(16 * 3, 16 * 8);
        mo.updateRay();
    }

    private void checkFloorPoint() {
        Tile bl = worldToTile(mo.position.x, mo.position.y - 1);
        Tile br = worldToTile(mo.position.x + mo.bounds.width, mo.position.y - 1);
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
        if (mo.velocity.x < 0) {
//            System.out.println("Double (checking for LEFT wall");
            for (int y = 0; y < Level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < Level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    if (OverlapTester.intersectRayBounds(mo.leftSen, t.bounds, intersect)) {
                        if (mo.position.x <= t.bounds.lowerLeft.x + t.bounds.width) {
                            mo.position.x = t.bounds.lowerLeft.x + t.bounds.width + gap;

                            mo.bounds.lowerLeft.set(mo.position);
                            mo.xsp = 0; //not needed
                            mo.velocity.x = 0;
                            return;
                        }
                    }
                }
            }

        }
        //if moving right
        if (mo.velocity.x > 0) {
//            System.out.println("Double (checking for RIGHT wall");
            for (int y = 0; y < Level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < Level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    if (OverlapTester.intersectRayBounds(mo.rightSen, t.bounds, intersect)) {
                        if (mo.position.x + Mo.MO_WIDTH >= t.bounds.lowerLeft.x) {
                            mo.position.x = t.bounds.lowerLeft.x - Mo.MO_WIDTH - gap;
                            mo.bounds.lowerLeft.set(mo.position);
                            mo.velocity.x = 0;
                            return;
                        }
                    }
                }
            }
        }
    }

    private void floor() {
//        if (mo.ysp < 0) {
        if (mo.velocity.y < 0) {
//            System.out.println("Double (checking for FLOOR)");
            //If player is moving down
            for (int y = 0; y < Level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < Level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    if (OverlapTester.intersectRayBounds(mo.leftFoot, t.bounds, intersect) ||
                            OverlapTester.intersectRayBounds(mo.rightFoot, t.bounds, intersect)) {
                        if (mo.position.y <= intersect.y) {
                            mo.grounded = true;
                            mo.inAir = false;
                            mo.position.y = t.bounds.lowerLeft.y + t.bounds.height;
                            mo.bounds.lowerLeft.set(mo.position);
//                            mo.ysp = 0;
                            mo.velocity.y = 0;
                            return;
                        }
                    }
                    //Debugging only
//                    if (!OverlapTester.intersectRayBounds(mo.leftFoot, t.bounds, intersect) &&
//                            !OverlapTester.intersectRayBounds(mo.rightFoot, t.bounds, intersect)) {
//                        left_hit = right_hit = false;
//                    }
                }
            }//end floor check loop
        }
    }

    private void ceiling() {
        if (mo.velocity.y > 0) {
//            System.out.println("Double (checking for FLOOR)");
            //If player is moving up
            for (int y = 0; y < Level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < Level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    int gap = 0;
                    if (OverlapTester.intersectRayBounds(mo.leftHead, t.bounds, intersect) ||
                            OverlapTester.intersectRayBounds(mo.rightHead, t.bounds, intersect)) {
                        if (mo.position.y + Mo.MO_HEIGHT > intersect.y) {
                            mo.position.y = intersect.y - Mo.MO_HEIGHT - gap;
                            mo.bounds.lowerLeft.set(mo.position);
//                            mo.ysp = 0;
                            mo.velocity.y = 0;
                        }
                    }
                }
            }
        }
    }

    private void playerCollisions() {
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

    private void collisions() {
        playerCollisions();
    }

    public void update(float deltaTime) {
        /*update mo*/
        mo.update(deltaTime);
        //Bound to world
        // TODO: 16/10/2018 depending on current level
        mo.position.x = Helper.Clamp(mo.position.x,
                2, Level.WORLD_WIDTH * Level.WORLD_LENGTH - Mo.MO_WIDTH - 2);
        collisions();
        mo.updateRay();
    }

}
