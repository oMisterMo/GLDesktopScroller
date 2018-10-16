package com.ds.mo.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.math.collision.Ray;
import com.ds.mo.common.Animation;
import com.ds.mo.common.Helper;
import com.ds.mo.common.OverlapTester;
import com.ds.mo.common.Particle;
import com.ds.mo.engine.effects.FireParticleSystem;
import com.ds.mo.engine.logic.Boo;
import com.ds.mo.engine.logic.Goomba;
import com.ds.mo.engine.logic.Level;
import com.ds.mo.engine.logic.Mo;
import com.ds.mo.engine.logic.Tile;
import com.ds.mo.common.Ray;
import com.ds.mo.engine.logic.World;

public class GameScreen implements Screen {
    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 180;
    final DesktopGame game;

    private SpriteBatch batcher;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private OrthographicCamera camera;
    private OrthographicCamera guiCam;
    private Assets assets;
//    private Level level;
//    private Mo mo;
//    private Boo boo;
//    private Goomba goomba;
    private World world;

    private static final float COL_DIST = 1;
    private Tile rock;
    private Vector2 intersect = new Vector2();
    private Vector2 out = new Vector2();

    private float elapsedTime;
    FPSLogger fps = new FPSLogger();
    private Vector2 center;

    //--------------------------------------
    private Vector2 endPoint = new Vector2();
    private boolean left_hit = false;
    private boolean right_hit = false;

    private boolean debugMode = true;
    private boolean drawMode = true;

    private FireParticleSystem fs;

    public GameScreen(DesktopGame game) {
        this.game = game;
        batcher = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();


        //Load world atlas
        assets = new Assets();

        //Set game variables
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        guiCam = new OrthographicCamera();
        guiCam.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

//        level = new Level();
//        level.tiles[Level.NO_OF_TILES_Y - 1][0].type = 3;
//        level.tiles[Level.NO_OF_TILES_Y - 1][0].solid = true;
//        mo = new Mo(16 * 3, 16 * 8, level);
//        boo = new Boo(100, 100, mo);
//        goomba = new Goomba(0, 100);
        world = new World();

        elapsedTime = 0;
        center = new Vector2(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
        initFireSystem();


        Vector2 v = new Vector2(200,200);
        System.out.println("vector: " + v.angle());

    }

    private void initFireSystem() {
        fs = new FireParticleSystem(new Vector2(200, 100), FireParticleSystem.RED);
//        fs = new FireParticleSystem(new Vector2(70 + (16 * 6), 54), FireParticleSystem.RED);
    }


    private void input() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            world.restart();    //or world = null; -> world = new World();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            debugMode = !debugMode;
            System.out.println("DebugMode: " + debugMode);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            drawMode = !drawMode;
            System.out.println("DrawMode: " + drawMode);
        }

//        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
//            System.out.println("click");
//            int x = Gdx.input.getX() / 3;
//            int y = (int) WORLD_HEIGHT - Gdx.input.getY() / 3;
//            System.out.println(x + ", " + y);
//            Tile door = level.tiles[3][14];
//            if (OverlapTester.pointInRectangle(door.bounds, x, y)) {
//                System.out.println("TOUCHED DOOR");
//            }
//            return;
//        }
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
        Level level = world.level;
        return level.getTile(pointToTileCoordsX(x), pointToTileCoordsY(y));
    }

    private void drawWorldBounds2() {
        for (int y = 0; y < Level.NO_OF_TILES_Y; y++) {
            for (int x = 0; x < Level.NO_OF_TILES_X; x++) {
                Tile tile = world.level.tiles[y][x];
                if (!tile.solid) continue;
                shapeRenderer.setColor(0, 0.4f, 0, 1);
                shapeRenderer.rect(tile.bounds.lowerLeft.x, tile.bounds.lowerLeft.y,
                        tile.bounds.width, tile.bounds.height);
            }
        }
    }

    private void drawWorldBounds() {
        for (int y = 0; y < Level.NO_OF_TILES_Y; y++) {
            for (int x = 0; x < Level.NO_OF_TILES_X; x++) {
                Tile tile = world.level.tiles[y][x];
                if (!tile.solid) {
                    continue;
                }
                switch (tile.type) {
                    case Tile.WALL:
                    case Tile.GROUND_TL:
                    case Tile.GROUND_TM:
                    case Tile.GROUND_TR:
                    case Tile.GROUND_ML:
                    case Tile.GROUND_MM:
                    case Tile.GROUND_MR:
                    case Tile.GROUND_LL:
                    case Tile.GROUND_LM:
                    case Tile.GROUND_LR:
                    case Tile.DOOR0:
                    case Tile.DOOR1:
                    case Tile.DOOR2:
                    case Tile.DOOR3:
                    case Tile.DOOR4:
                    case Tile.DOOR5:
                    case Tile.BIGDOOR0:
                    case Tile.BIGDOOR1:
                    case Tile.BIGDOOR2:
                    case Tile.BIGDOOR3:
                    case Tile.BIGDOOR4:
                    case Tile.BIGDOOR5:
                    case Tile.GRASS_T0:
                    case Tile.GRASS_T1:
                    case Tile.GRASS_T2:
                    case Tile.GRASS_T3:
                        shapeRenderer.setColor(0, 0.4f, 0, 1);
                        shapeRenderer.rect(tile.bounds.lowerLeft.x, tile.bounds.lowerLeft.y,
                                tile.bounds.width, tile.bounds.height);
                        break;
                    default:
                        //Draw nothing
//                        batcher.draw(Assets.wall, tile.position.x, tile.position.y);
                }
            }
        }
    }

    private void drawBooBounds() {
        Boo boo = world.boo;
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(boo.bounds.lowerLeft.x, boo.bounds.lowerLeft.y,
                boo.bounds.width, boo.bounds.height);
    }

    private void drawMoBounds() {
        Mo mo = world.mo;
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(mo.bounds.lowerLeft.x, mo.bounds.lowerLeft.y,
                mo.bounds.width, mo.bounds.height);
    }

    private void drawGoombaBounds() {
        Goomba goomba = world.goomba;
        shapeRenderer.setColor(Color.FOREST);
        shapeRenderer.rect(goomba.bounds.lowerLeft.x, goomba.bounds.lowerLeft.y,
                goomba.bounds.width, goomba.bounds.height);
    }

    private void drawWorld() {
        Level level = world.level;
        for (int y = 0; y < Level.NO_OF_TILES_Y; y++) {
            for (int x = 0; x < Level.NO_OF_TILES_X; x++) {
                Tile tile = level.tiles[y][x];
                batcher.setColor(1, 1, 1, 1);
                switch (tile.type) {
                    case Tile.WALL:
                        batcher.draw(Assets.wall, tile.position.x, tile.position.y);
                        break;
                    case Tile.GROUND_TL:
                        if (tile.hit) {
                            batcher.setColor(1, 0, 0, 1);
                        } else {
                            batcher.setColor(1, 1, 1, 1);
                        }
                        batcher.draw(Assets.ground_tl, tile.position.x, tile.position.y);
                        break;
                    case Tile.GROUND_TM:
                        if (tile.hit) {
                            batcher.setColor(1, 0, 0, 1);
                        } else {
                            batcher.setColor(1, 1, 1, 1);
                        }
                        batcher.draw(Assets.ground_tm, tile.position.x, tile.position.y);
                        break;
                    case Tile.GROUND_TR:
                        if (tile.hit) {
                            batcher.setColor(1, 0, 0, 1);
                        } else {
                            batcher.setColor(1, 1, 1, 1);
                        }
                        batcher.draw(Assets.ground_tr, tile.position.x, tile.position.y);
                        break;
                    case Tile.GROUND_ML:
                        batcher.draw(Assets.ground_ml, tile.position.x, tile.position.y);
                        break;
                    case Tile.GROUND_MM:
                        if (tile.hit) {
                            batcher.setColor(1, 0, 0, 1);
                        } else {
                            batcher.setColor(1, 1, 1, 1);
                        }
                        batcher.draw(Assets.ground_mm, tile.position.x, tile.position.y);
                        break;
                    case Tile.GROUND_MR:
                        batcher.draw(Assets.ground_mr, tile.position.x, tile.position.y);
                        break;
                    case Tile.GROUND_LL:
                        batcher.draw(Assets.ground_ll, tile.position.x, tile.position.y);
                        break;
                    case Tile.GROUND_LM:
                        batcher.draw(Assets.ground_lm, tile.position.x, tile.position.y);
                        break;
                    case Tile.GROUND_LR:
                        batcher.draw(Assets.ground_lr, tile.position.x, tile.position.y);
                        break;
                    case Tile.DOOR0:
                        batcher.draw(Assets.door0, tile.position.x, tile.position.y);
                        break;
                    case Tile.DOOR1:
                        batcher.draw(Assets.door1, tile.position.x, tile.position.y);
                        break;
                    case Tile.DOOR2:
                        batcher.draw(Assets.door2, tile.position.x, tile.position.y);
                        break;
                    case Tile.DOOR3:
                        batcher.draw(Assets.door3, tile.position.x, tile.position.y);
                        break;
                    case Tile.DOOR4:
                        batcher.draw(Assets.door4, tile.position.x, tile.position.y);
                        break;
                    case Tile.DOOR5:
                        batcher.draw(Assets.door5, tile.position.x, tile.position.y);
                        break;
                    case Tile.BIGDOOR0:
                        batcher.draw(Assets.bigDoor0, tile.position.x, tile.position.y);
                        break;
                    case Tile.BIGDOOR1:
                        batcher.draw(Assets.bigDoor1, tile.position.x, tile.position.y);
                        break;
                    case Tile.BIGDOOR2:
                        batcher.draw(Assets.bigDoor2, tile.position.x, tile.position.y);
                        break;
                    case Tile.BIGDOOR3:
                        batcher.draw(Assets.bigDoor3, tile.position.x, tile.position.y);
                        break;
                    case Tile.BIGDOOR4:
                        batcher.draw(Assets.bigDoor4, tile.position.x, tile.position.y);
                        break;
                    case Tile.BIGDOOR5:
                        batcher.draw(Assets.bigDoor5, tile.position.x, tile.position.y);
                        break;
                    case Tile.GRASS_T0:
                        batcher.draw(Assets.grass_t0, tile.position.x, tile.position.y);
                        break;
                    case Tile.GRASS_T1:
                        batcher.draw(Assets.grass_t1, tile.position.x, tile.position.y);
                        break;
                    case Tile.GRASS_T2:
                        batcher.draw(Assets.grass_t2, tile.position.x, tile.position.y);
                        break;
                    case Tile.GRASS_T3:
                        batcher.draw(Assets.grass_t3, tile.position.x, tile.position.y);
                        break;

                    default:
                        //Draw nothing
//                        batcher.draw(Assets.wall, tile.position.x, tile.position.y);
                }
            }
        }
    }

    private void drawWorld2() {
        Level level = world.level;
        batcher.setColor(Color.WHITE);
        for (int y = 0; y < Level.NO_OF_TILES_Y; y++) {
            for (int x = 0; x < Level.NO_OF_TILES_X; x++) {
                Tile tile = level.tiles[y][x];
                if (tile.type == -1) continue;
//                System.out.println("x,y: [" + x + ", " + y + "]");
                batcher.draw(Assets.T[tile.type], tile.position.x, tile.position.y);
            }
        }
    }

    private void drawBoo() {
        Boo boo = world.boo;
        batcher.setColor(Color.WHITE);
        switch (boo.state) {
            case Boo.STATE_IDLE:
                //Mo.Left/Right == -1/1 respectively
                if (boo.facing.x == Mo.RIGHT) {
                    batcher.draw(Assets.T[1], boo.position.x, boo.position.y,
                            Boo.BOO_WIDTH, Boo.BOO_HEIGHT);
                } else if (boo.facing.x == Mo.LEFT) {
                    batcher.draw(Assets.T[1], boo.position.x + Boo.BOO_WIDTH, boo.position.y,
                            -Boo.BOO_WIDTH, Boo.BOO_HEIGHT);
                }
                break;
            case Boo.STATE_CHASE:
                if (boo.facing.x == Mo.RIGHT) {
                    batcher.draw(Assets.T[2], boo.position.x, boo.position.y,
                            Boo.BOO_WIDTH, Boo.BOO_HEIGHT);
                } else if (boo.facing.x == Mo.LEFT) {
                    batcher.draw(Assets.T[2], boo.position.x + Boo.BOO_WIDTH, boo.position.y,
                            -Boo.BOO_WIDTH, Boo.BOO_HEIGHT);
                }
                break;

        }
    }

    private void drawGoomba() {
        Goomba goomba = world.goomba;
        batcher.setColor(Color.WHITE);
        TextureRegion region = Assets.goombaAnim.getKeyFrame(goomba.stateTime,
                Animation.ANIMATION_LOOPING);
        // TODO: 10/10/2018 guaranteed facing LEFT or RIGHT (don't need second condition)
        if (goomba.facing.x == Goomba.RIGHT) {
            batcher.draw(region, goomba.position.x, goomba.position.y,
                    Goomba.GOOMBA_WIDTH, Goomba.GOOMBA_HEIGHT);
        } else if (goomba.facing.x == Goomba.LEFT) {
            batcher.draw(region, goomba.position.x + Goomba.GOOMBA_WIDTH, goomba.position.y,
                    -Goomba.GOOMBA_WIDTH, Goomba.GOOMBA_HEIGHT);
        }
    }

    private void drawMo() {
        Mo mo = world.mo;
        batcher.setColor(Color.RED);
        batcher.draw(Assets.wall, mo.position.x, mo.position.y);
//        batcher.setColor(Color.BLACK);
//        batcher.draw(Assets.wall, mo.bounds.lowerLeft.x, mo.bounds.lowerLeft.y);
    }

    private void singleRay() {
        Mo mo = world.mo;
        //Floor senors (Left/Right)
        if (OverlapTester.intersectRayBounds(mo.leftFoot, rock.bounds, intersect)) {
            //left_hit solid WAY below
            out.set(mo.leftFoot.direction).scl(Mo.RAY_LEN).add(mo.leftFoot.start);   //from origin -> rays length
//            System.out.println("out.dst(intersect): " + out.dst(intersect));
//            if (out.dst(intersect) < COL_DIST) {
            //->register collision when ray is close to floor
            left_hit = true;
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.line(mo.leftFoot.start, intersect);
        }
        if (OverlapTester.intersectRayBounds(mo.rightFoot, rock.bounds, intersect)) {
            out.set(mo.rightFoot.direction).scl(Mo.RAY_LEN).add(mo.rightFoot.start);
//            if (out.dst(intersect) < COL_DIST) {
            right_hit = true;
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.line(mo.rightFoot.start, intersect);
        }

        //Wall sensors (Left/right)
        if (OverlapTester.intersectRayBounds(mo.leftSen, rock.bounds, intersect)) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.line(mo.leftSen.start, intersect);
        }
        if (OverlapTester.intersectRayBounds(mo.rightSen, rock.bounds, intersect)) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.line(mo.rightSen.start, intersect);
        }

        //Ceiling sensors (Left/Right)
        if (OverlapTester.intersectRayBounds(mo.leftHead, rock.bounds, intersect)) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.line(mo.leftHead.start, intersect);
        }
        if (OverlapTester.intersectRayBounds(mo.rightHead, rock.bounds, intersect)) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.line(mo.rightHead.start, intersect);
        }
    }

    private void completeRay() {
        Level level = world.level;
        Mo mo = world.mo;
//        if (mo.isMoving()) {
        boolean left_foot_col = false;
        boolean right_foot_col = false;
        boolean left_col = false;
        for (int y = Level.NO_OF_TILES_Y - 1; y >= 0; y--) {
            for (int x = Level.NO_OF_TILES_X - 1; x >= 0; x--) {
                Tile t = level.tiles[y][x];
                if (!t.solid) continue;
                //Floor senors (Left/Right)
                if (OverlapTester.intersectRayBounds(mo.leftFoot, t.bounds, intersect)) {
                    //left_hit solid WAY below
                    if (!left_foot_col) {
                        out.set(mo.rightFoot.direction).scl(Mo.RAY_LEN).add(mo.rightFoot.start);
                        left_hit = true;
                        shapeRenderer.setColor(Color.RED);
                        shapeRenderer.line(mo.leftFoot.start, intersect);
                        left_foot_col = true;
                    }
                }
                if (OverlapTester.intersectRayBounds(mo.rightFoot, t.bounds, intersect)) {
                    if (!right_foot_col) {
                        out.set(mo.rightFoot.direction).scl(Mo.RAY_LEN).add(mo.rightFoot.start);
                        right_hit = true;
                        shapeRenderer.setColor(Color.RED);
                        shapeRenderer.line(mo.rightFoot.start, intersect);
                        right_foot_col = true;
                    }
                }
                //Wall sensors (Left/right)
                if (OverlapTester.intersectRayBounds(mo.leftSen, t.bounds, intersect)) {
                    if (!left_col) {
                        shapeRenderer.setColor(Color.RED);
                        shapeRenderer.line(mo.leftSen.start, intersect);
                        left_col = true;
                    }
                }
            }
        }

        //Part 2 (right/up collision)
        boolean left_ceil_col = false;
        boolean right_ceil_col = false;
        boolean right_col = false;
        for (int y = 0; y < Level.NO_OF_TILES_Y; y++) {
            for (int x = 0; x < Level.NO_OF_TILES_X; x++) {
                Tile t = level.tiles[y][x];
                if (!t.solid) continue;
                if (OverlapTester.intersectRayBounds(mo.rightSen, t.bounds, intersect)) {
                    if (!right_col) {
                        shapeRenderer.setColor(Color.RED);
                        shapeRenderer.line(mo.rightSen.start, intersect);
                        right_col = true;
                    }
                }
                //Ceiling sensors (Left/Right)
                if (OverlapTester.intersectRayBounds(mo.leftHead, t.bounds, intersect)) {
                    if (!left_ceil_col) {
                        shapeRenderer.setColor(Color.RED);
                        shapeRenderer.line(mo.leftHead.start, intersect);
                        left_ceil_col = true;
                    }
                }
                if (OverlapTester.intersectRayBounds(mo.rightHead, t.bounds, intersect)) {
                    if (!right_ceil_col) {
                        shapeRenderer.setColor(Color.RED);
                        shapeRenderer.line(mo.rightHead.start, intersect);
                        right_ceil_col = true;
                    }
                }
            }
        }
//        }
    }

    private void drawRay() {
        Mo mo = world.mo;
        if (left_hit) {
            shapeRenderer.setColor(Color.RED);
        } else {
            shapeRenderer.setColor(Color.GOLD);
        }
        shapeRenderer.x(mo.leftFoot.start.x, mo.leftFoot.start.y, 1);
        shapeRenderer.line(mo.leftFoot.start, mo.leftFoot.getEndPoint(endPoint, Mo.RAY_LEN));
        shapeRenderer.line(mo.leftHead.start, mo.leftHead.getEndPoint(endPoint, Mo.RAY_LEN));
        shapeRenderer.x(mo.rightFoot.start.x, mo.rightFoot.start.y, 1);
        shapeRenderer.line(mo.rightFoot.start, mo.rightFoot.getEndPoint(endPoint, Mo.RAY_LEN));
        shapeRenderer.line(mo.rightHead.start, mo.rightHead.getEndPoint(endPoint, Mo.RAY_LEN));
        shapeRenderer.x(mo.leftSen.start.x, mo.leftSen.start.y, 1);
        shapeRenderer.line(mo.leftSen.start, mo.leftSen.getEndPoint(endPoint, Mo.RAY_LEN + 1));
        shapeRenderer.line(mo.rightSen.start, mo.rightSen.getEndPoint(endPoint, Mo.RAY_LEN + 1));
//        shapeRenderer.x(rightSen.start.x, rightSen.start.y, 1);

        /*Draw red RAY*/
        completeRay();
    }

    private void drawBlocks() {
        shapeRenderer.setColor(0, 1, 1, 0.1f);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                shapeRenderer.rect(128 * x, 128 * y, 128, 128);
            }
        }
    }

    private void drawRandom() {
        Mo mo = world.mo;
        //Get two floor tiles below player
        Tile bl = worldToTile(mo.position.x, mo.position.y - 1);
        Tile br = worldToTile(mo.position.x + Mo.MO_WIDTH, mo.position.y - 1);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.rect(bl.bounds.lowerLeft.x, bl.bounds.lowerLeft.y, bl.bounds.width, bl.bounds.height);
        shapeRenderer.rect(br.bounds.lowerLeft.x, br.bounds.lowerLeft.y, br.bounds.width, br.bounds.height);

    }

    private void drawParticles() {
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE); //This mode actually blends the alpha channel
        //Draw the fire particles
//        batcher.beginBatch(Assets.worldAtlas);

//        batcher.setColor(redFire.r, redFire.g, redFire.b, redFire.a);
        int len = fs.particles.size();
        for (int i = len - 1; i >= 0; i--) {
            Particle p = fs.particles.get(i);
            batcher.setColor(p.color.r, p.color.g, p.color.b, p.color.a);
            for (int j = 0; j < 2; j++) {
                batcher.draw(Assets.particle, p.position.x - Tile.TILE_WIDTH / 2 + (j * 66),
                        p.position.y - Tile.TILE_HEIGHT / 2,
                        8, 8,
                        16, 16, p.scale, p.scale,
                        p.rotation);
            }

        }

////        batcher.setColor(blueFire.r, blueFire.g, blueFire.b, blueFire.a);
//        len = fs.particles.size();
//        for (int i = len - 1; i >= 0; i--) {
//            // TODO: 11/09/2018 Use a single particle drawn at multiple places
//            Particle p2 = fs.particles.get(i);
//            Particle p3 = fs.particles.get(i);
//            batcher.setColor(p2.color.r, p2.color.g, p2.color.b, p2.color.a);
//            batcher.draw(Assets.particle, p2.position.x, p2.position.y,
//                    p2.bounds.width * p2.scale, p2.bounds.height * p2.scale,
//                    p2.rotation);
//            batcher.setColor(p3.color.r, p3.color.g, p3.color.b, p3.color.a);
//            batcher.draw(Assets.particle, p3.position.x, p3.position.y,
//                    p3.bounds.width * p3.scale, p3.bounds.height * p3.scale,
//                    p3.rotation);
//        }
//        gl.glDisable(GL10.GL_BLEND);
//        batcher.endBatch();
    }

    private void drawPlayerInfo() {
        Mo mo = world.mo;
        font.setColor(Color.WHITE);
        font.draw(batcher, "pos: " + mo.position, 100, Level.WORLD_HEIGHT * 3 - 10);
        font.draw(batcher, "xsp: " + mo.xsp, 100, Level.WORLD_HEIGHT * 3 - 10 - 20);
        font.draw(batcher, "ysp: " + mo.ysp, 100, Level.WORLD_HEIGHT * 3 - 10 - 40);

        font.draw(batcher, "grounded: ", 400, Level.WORLD_HEIGHT * 3 - 10);
        font.draw(batcher, "in air: ", 400, Level.WORLD_HEIGHT * 3 - 10 - 20);
        if (mo.grounded || mo.inAir) {
            font.setColor(Color.GREEN);
            int shift;
            if (mo.inAir) {
                shift = -20;
            } else {
                shift = 0;
            }
            font.draw(batcher, "YES", 480, Level.WORLD_HEIGHT * 3 - 10 + shift);
        }
    }

    private void drawGhostInfo() {
        Boo boo = world.boo;
        font.setColor(Color.WHITE);
        font.draw(batcher, "pos: " + boo.position, 100, Level.WORLD_HEIGHT * 3 - 10);
        font.draw(batcher, "vel: " + boo.velocity, 100, Level.WORLD_HEIGHT * 3 - 30);
        font.draw(batcher, "acc: " + boo.accel, 100, Level.WORLD_HEIGHT * 3 - 50);
        font.draw(batcher, "angle: " + boo.velocity.angle(), 100, Level.WORLD_HEIGHT * 3 - 80);
    }

    private void cameraFollowPlayer() {
        Mo mo = world.mo;
        camera.position.x += ((mo.position.x + 80) - camera.position.x) * .06;
//        if (mo.position.x > camera.position.y) {
//            //Tween to position
//            camera.position.x += ((mo.position.x + 80) - camera.position.x) * .1;
//        } else {
//            camera.position.x += ((mo.position.x) + 40 - camera.position.x) * .1;
//        }
        if (camera.position.x <= center.x) {
            camera.position.x = center.x;
        }
        if (camera.position.x >= center.x * 5) {
            camera.position.x = center.x * 5;
        }
    }

    private void updateParticles(float deltaTime) {
        fs.update(deltaTime);
    }

    private void update(float deltaTime) {
        input();
//        fps.log();
        camera.update();
        guiCam.update();
//        /*update monsters*/
//        boo.update(deltaTime);
//        goomba.update(deltaTime);
//
//        /*update mo*/
//        mo.update(deltaTime);
//
////        collisions();
//        collisionsAll();
//
//        //Bound to world
//        mo.position.x = Helper.Clamp(mo.position.x,
//                2, Level.WORLD_WIDTH * Level.WORLD_LENGTH - Mo.MO_WIDTH - 2);
//        mo.updateRay();
        world.update(deltaTime);
        cameraFollowPlayer();

        updateParticles(deltaTime);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (drawMode) {
            /*DRAW PARTICLES*/
            batcher.setProjectionMatrix(camera.combined);
            batcher.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            batcher.begin();
            drawParticles();
            batcher.end();

            /*DRAW WORLD*/
            batcher.setProjectionMatrix(camera.combined);
            batcher.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            batcher.begin();
//          drawWorld();
            drawWorld2();
            drawBoo();
            drawGoomba();
            drawMo();
            batcher.end();
        }
        if (debugMode) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            /*DRAW HITBOX*/
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//          drawWorldBounds();
            drawBooBounds();
            drawGoombaBounds();
            drawWorldBounds2();
            drawMoBounds();
            drawRay();
            drawBlocks();       //128 * 128
//          drawRandom();      //test
            shapeRenderer.end();

            /*Draw text*/
//            batcher.setProjectionMatrix(camera.combined.scl(0.333f));
            batcher.setProjectionMatrix(guiCam.combined.scl(0.33333f));
            batcher.begin();
//            drawPlayerInfo();
            drawGhostInfo();
            batcher.end();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public void dispose() {
        Assets.worldAtlas.dispose();
        batcher.dispose();
    }
}
