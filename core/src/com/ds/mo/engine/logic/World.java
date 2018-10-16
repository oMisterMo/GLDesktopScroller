package com.ds.mo.engine.logic;

import com.badlogic.gdx.math.Vector2;
import com.ds.mo.common.Helper;
import com.ds.mo.common.OverlapTester;

public class World {

    public Level level;
    public Mo mo;
    public Boo boo;
    public Goomba goomba;

    private Vector2 endPoint = new Vector2();
    private Vector2 intersect = new Vector2();
    private Vector2 out = new Vector2();
    private Vector2 center = new Vector2();

    public World(){
        level = new Level();
        mo = new Mo(16 * 3, 16 * 8);
        boo = new Boo(100, 100, mo);
        goomba = new Goomba(0, 100);


        //Set random top tiles (test)
        level.tiles[Level.NO_OF_TILES_Y - 1][0].type = 3;
        level.tiles[Level.NO_OF_TILES_Y - 1][0].solid = true;
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
        this.boo = null;
        boo = new Boo(100, 100, mo);
        this.goomba = null;
        goomba = new Goomba(0, 100);
        mo.updateRay();
    }

//    private void checkFloorSen() {
//        if (!OverlapTester.intersectRayBounds(mo.leftFoot, rock.bounds, intersect) &&
//                !OverlapTester.intersectRayBounds(mo.rightFoot, rock.bounds, intersect)) {
//            System.out.println("left and right sensor: NOT HIT");
//            left_hit = right_hit = false;
//
//            mo.grounded = false;
//            mo.inAir = true;
//        }
//    }



//    private void horizontalCol() {
//        //if moving left
//        int gap = 1;    //should be 1
//        if (mo.xsp < 0) {
//            if (OverlapTester.intersectRayBounds(mo.leftSen, rock.bounds, intersect)) {
//                if (mo.position.x <= rock.bounds.lowerLeft.x + rock.bounds.width) {
//                    mo.position.x = rock.bounds.lowerLeft.x + rock.bounds.width + gap;
//                    mo.bounds.lowerLeft.set(mo.position);
//                    mo.xsp = 0;
//                }
//            }
//        }
//        //if moving right
//        if (mo.xsp > 0) {
//            if (OverlapTester.intersectRayBounds(mo.rightSen, rock.bounds, intersect)) {
//                if (mo.position.x + Mo.MO_WIDTH >= rock.bounds.lowerLeft.x) {
//                    mo.position.x = rock.bounds.lowerLeft.x - Mo.MO_WIDTH - gap;
//                    mo.bounds.lowerLeft.set(mo.position);
//                    mo.xsp = 0;
//                }
//            }
//        }
//    }

//    private void floorCol() {
//        //Left Leg
//        if (OverlapTester.intersectRayBounds(mo.leftFoot, rock.bounds, intersect)) {
//            out.set(mo.leftFoot.direction).scl(Mo.RAY_LEN).add(mo.leftFoot.start);   //Points to where ray ends
////                    if (out.dst(intersect) < COL_DIST) {
//            if (mo.position.y <= intersect.y) {
//                System.out.println("Left sensor detect -> Setting grounded");
//                left_hit = true;
//                mo.grounded = true;
//                mo.inAir = false;
//                mo.position.y = rock.bounds.lowerLeft.y + rock.bounds.height;
//                mo.bounds.lowerLeft.set(mo.position);
//                mo.ysp = 0;
//                return;
//            }
//        }
//        //Right Leg
//        if (OverlapTester.intersectRayBounds(mo.rightFoot, rock.bounds, intersect)) {
//            out.set(mo.rightFoot.direction).scl(Mo.RAY_LEN).add(mo.rightFoot.start);
////                    if (out.dst(intersect) < COL_DIST) {
//            if (mo.position.y <= intersect.y) {
//                System.out.println("Right sensor detect -> Setting grounded");
//                right_hit = true;
//                mo.grounded = true;
//                mo.inAir = false;
//                mo.position.y = rock.bounds.lowerLeft.y + rock.bounds.height;
//                mo.bounds.lowerLeft.set(mo.position);
//                mo.ysp = 0;
//                return;
//            }
//        }
//    }
//
//    private void ceilingCol() {
//        int gap = 1;
//        if (OverlapTester.intersectRayBounds(mo.leftHead, rock.bounds, intersect)) {
//            if (mo.position.y + Mo.MO_HEIGHT >= intersect.y) {
//                mo.position.y = intersect.y - Mo.MO_HEIGHT - gap;
//                mo.bounds.lowerLeft.set(mo.position);
//                mo.ysp = 0;
//            }
//        }
//        if (OverlapTester.intersectRayBounds(mo.rightHead, rock.bounds, intersect)) {
//            if (mo.position.y + Mo.MO_HEIGHT >= intersect.y) {
//                mo.position.y = intersect.y - Mo.MO_HEIGHT - gap;
//                mo.bounds.lowerLeft.set(mo.position);
//                mo.ysp = 0;
//            }
//        }
//    }
//
//    private void collisions() {
//        //Grounded
//        if (mo.grounded) {
//            /*Handle flat ground -> lower y horizontal sensors*/
//
//            /*Handle vertical collision*/
////            checkFloorSen();
////            checkFloorPoint();
//            /*Handle balancing on edges*/
//
//            /*Handle horizontal collision*/
//            horizontalCol();
//        }
//
//        //Airborne
//        if (mo.inAir) {
//            /*If player is moving DOWN*/
//            if (mo.ysp < 0) {
//                /*Handle horizontal collisions*/
//                horizontalCol();
//                /*Floor collision*/
//                floorCol();
//                //Debugging only
////                if (!OverlapTester.intersectRayBounds(mo.leftFoot, rock.bounds, intersect) &&
////                        !OverlapTester.intersectRayBounds(mo.rightFoot, rock.bounds, intersect)) {
////                    left_hit = right_hit = false;
////                }
//            }
//
//            /*If player is moving UP*/
//            if (mo.ysp > 0) {
//                //Horizontal collision
//                horizontalCol();
//                //Ceiling collision
//                ceilingCol();
//            }
//        }
//    }

    private void checkFloorPoint() {
        Tile bl = worldToTile(mo.position.x, mo.position.y + 1);
        Tile br = worldToTile(mo.position.x + Mo.MO_WIDTH, mo.position.y - 1);
        if (!bl.solid && !br.solid) {
            System.out.println("left and right POINT: NOT HIT");
//            left_hit = right_hit = false;

            mo.grounded = false;
            mo.inAir = true;
        }
    }

    private void horizontalALL() {
        //if moving left
        int gap = 1;        //push away from tile 1 unit
        if (mo.xsp < 0) {
//            System.out.println("Double (checking for LEFT wall");
            for (int y = 0; y < Level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < Level.NO_OF_TILES_X; x++) {
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
            for (int y = 0; y < Level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < Level.NO_OF_TILES_X; x++) {
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

    private void floorALL() {
        if (mo.ysp < 0) {
//            System.out.println("Double (checking for FLOOR)");
            //If player is moving down
            for (int y = 0; y < Level.NO_OF_TILES_Y; y++) {
                for (int x = 0; x < Level.NO_OF_TILES_X; x++) {
                    Tile t = level.tiles[y][x];
                    if (!t.solid) continue;
                    if (OverlapTester.intersectRayBounds(mo.leftFoot, t.bounds, intersect) ||
                            OverlapTester.intersectRayBounds(mo.rightFoot, t.bounds, intersect)) {
                        if (mo.position.y <= intersect.y) {
//                            left_hit = true;
//                            right_hit = true;
                            mo.grounded = true;
                            mo.inAir = false;
                            mo.position.y = t.bounds.lowerLeft.y + t.bounds.height;
                            mo.bounds.lowerLeft.set(mo.position);
                            mo.ysp = 0;
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

    private void ceilingALL() {
        if (mo.ysp > 0) {
//            System.out.println("Double (checking for FLOOR)");
            //If player is moving down
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
                            mo.ysp = 0;
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
            horizontalALL();
        }
        /*Airborne*/
        if (mo.inAir) {
            horizontalALL();
            floorALL();
            ceilingALL();
        }
    }

    private void enemyCollisions() {
        if (goomba.grounded) {
            // TODO: 10/10/2018 collisions with walls/enemies/ground
            //Walk left/right
            if (goomba.position.x > 200) {
                goomba.reverseXdir();
                return;
            }
            if (goomba.position.x <= 0) {
                goomba.reverseXdir();
                return;
            }
            //Check if grounded
        }
        if (goomba.inAir) {
            if (goomba.velocity.y < 0) {
                //If player is moving down
                for (int y = 0; y < Level.NO_OF_TILES_Y; y++) {
                    for (int x = 0; x < Level.NO_OF_TILES_X; x++) {
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

    private void collisionsAll() {
        playerCollisions();
        enemyCollisions();
    }

    public void update(float deltaTime){
        /*update monsters*/
        boo.update(deltaTime);
        goomba.update(deltaTime);

        /*update mo*/
        mo.update(deltaTime);

//        collisions();
        collisionsAll();

        //Bound to world
        mo.position.x = Helper.Clamp(mo.position.x,
                2, Level.WORLD_WIDTH * Level.WORLD_LENGTH - Mo.MO_WIDTH - 2);
//        cameraFollowPlayer();
        mo.updateRay();
    }

}
