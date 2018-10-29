package com.ds.mo.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ds.mo.common.Animation;
import com.ds.mo.common.OverlapTester;
import com.ds.mo.engine.logic.Boo;
import com.ds.mo.engine.logic.CustomLevel;
import com.ds.mo.engine.logic.Goomba;
import com.ds.mo.engine.logic.Level;
import com.ds.mo.engine.logic.Mo;
import com.ds.mo.engine.logic.Spider;
import com.ds.mo.engine.logic.Tile;
import com.ds.mo.engine.logic.FairWorld;
import com.ds.mo.engine.transition.FadeInTransitionEffect;
import com.ds.mo.engine.transition.FadeOutTransitionEffect;
import com.ds.mo.engine.transition.TransitionEffect;
import com.ds.mo.engine.transition.TransitionScreen;
import com.ds.mo.engine.transition.WaitEffect;

import java.util.ArrayList;

public class FairWorldScreen implements Screen {
    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 180;

    final DesktopGame game;
    private SpriteBatch batcher;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private OrthographicCamera camera;
    private OrthographicCamera guiCam;

    private Vector2 intersect = new Vector2();
    private Vector2 out = new Vector2();
    private Vector2 temp = new Vector2();
    private Vector2 endPoint = new Vector2();   //Used to draw ray cast
    private Vector2 center = new Vector2();


    private boolean debugMode = false;
    private boolean drawMode = true;

    private FairWorld world;

    public FairWorldScreen(DesktopGame game) {
        this.game = game;

        batcher = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();

        //Load world atlas
//        assets = new Assets();

        //Set game variables
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        //Center to small world
//        camera.position.x = (16 * 5) /2;
//        camera.position.y = (16 * 5) /2;
        guiCam = new OrthographicCamera();
        guiCam.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

        //Init World
        world = new FairWorld();
    }

    private void transition(Screen nextScreen) {
        //Transition
        Screen current = this;
        Screen next = nextScreen;
        ArrayList<TransitionEffect> effects = new ArrayList<TransitionEffect>();
        effects.add(new FadeOutTransitionEffect(0.5f));
        effects.add(new WaitEffect(0.5f));
        effects.add(new FadeInTransitionEffect(0.5f));
        Screen transitionScreen = new TransitionScreen(game, camera, current, next, effects);
        game.setScreen(transitionScreen);
    }

    private void input() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            world.restart();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            debugMode = !debugMode;
            System.out.println("DebugMode: " + debugMode);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            drawMode = !drawMode;
            System.out.println("DrawMode: " + drawMode);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            transition(new GameScreen(game));
            return;
        }
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

    //DEBUG MODE-------------------------------------------------------------------------
    private void drawWorldBounds() {
        CustomLevel level = world.level;
        for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
            for (int x = 0; x < level.NO_OF_TILES_X; x++) {
                Tile tile = level.tiles[y][x];
                if (!tile.solid) continue;
                shapeRenderer.setColor(0, 0.4f, 0, 1);
                shapeRenderer.rect(tile.bounds.lowerLeft.x, tile.bounds.lowerLeft.y,
                        tile.bounds.width, tile.bounds.height);
            }
        }
    }

    private void drawMoBounds() {
        Mo mo = world.mo;
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(mo.bounds.lowerLeft.x, mo.bounds.lowerLeft.y,
                mo.bounds.width, mo.bounds.height);
    }

    //
    private void drawBooBounds() {
        Boo boo = world.boo;
        shapeRenderer.setColor(Color.GOLD);
//        shapeRenderer.rect(boo.bounds.lowerLeft.x, boo.bounds.lowerLeft.y,
//                boo.bounds.width, boo.bounds.height);

        shapeRenderer.circle(boo.bounds.center.x, boo.bounds.center.y,
                boo.bounds.radius);
    }

    //
    private void drawGoombaBounds() {
        Goomba goomba = world.goomba;
        shapeRenderer.setColor(Color.FOREST);
        shapeRenderer.rect(goomba.bounds.lowerLeft.x, goomba.bounds.lowerLeft.y,
                goomba.bounds.width, goomba.bounds.height);
    }

    //
    private void drawSpiderBounds() {
        Spider spider = world.spider;
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(spider.bounds.lowerLeft.x, spider.bounds.lowerLeft.y,
                spider.bounds.width, spider.bounds.height);
    }

    private void drawRay() {
        Mo mo = world.mo;
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

    private void completeRay() {
        CustomLevel level = world.level;
        Mo mo = world.mo;
//        if (mo.isMoving()) {
        boolean left_foot_col = false;
        boolean right_foot_col = false;
        boolean left_col = false;
        for (int y = level.NO_OF_TILES_Y - 1; y >= 0; y--) {
            for (int x = level.NO_OF_TILES_X - 1; x >= 0; x--) {
                Tile t = level.tiles[y][x];
                if (!t.solid) continue;
                //Floor senors (Left/Right)
                if (OverlapTester.intersectRayBounds(mo.leftFoot, t.bounds, intersect)) {
                    //left_hit solid WAY below
                    if (!left_foot_col) {
                        out.set(mo.rightFoot.direction).scl(Mo.RAY_LEN).add(mo.rightFoot.start);
                        shapeRenderer.setColor(Color.RED);
                        shapeRenderer.line(mo.leftFoot.start, intersect);
                        left_foot_col = true;
                    }
                }
                if (OverlapTester.intersectRayBounds(mo.rightFoot, t.bounds, intersect)) {
                    if (!right_foot_col) {
                        out.set(mo.rightFoot.direction).scl(Mo.RAY_LEN).add(mo.rightFoot.start);
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
        for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
            for (int x = 0; x < level.NO_OF_TILES_X; x++) {
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

    private void drawBlocks() {
        shapeRenderer.setColor(0, 1, 1, 0.1f);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                shapeRenderer.rect(128 * x, 128 * y, 128, 128);
            }
        }
    }

    //
    private void drawGhostInfo() {
        Boo boo = world.boo;
        font.setColor(Color.WHITE);
        font.draw(batcher, "pos: " + boo.position, 100, Level.WORLD_HEIGHT * 3 - 10);
        font.draw(batcher, "vel: " + boo.velocity, 100, Level.WORLD_HEIGHT * 3 - 30);
        font.draw(batcher, "acc: " + boo.accel, 100, Level.WORLD_HEIGHT * 3 - 50);
        font.draw(batcher, "angle: " + boo.velocity.angle(), 100, Level.WORLD_HEIGHT * 3 - 80);
    }

    //
    private void drawPlayerInfo() {
        Mo mo = world.mo;
        font.setColor(Color.WHITE);
        font.draw(batcher, "pos: " + mo.position, 100, Level.WORLD_HEIGHT * 3 - 10);
        font.draw(batcher, "xsp: " + mo.velocity.x, 100, Level.WORLD_HEIGHT * 3 - 10 - 20);
        font.draw(batcher, "ysp: " + mo.velocity.y, 100, Level.WORLD_HEIGHT * 3 - 10 - 40);

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
        if (mo.hurt) {
            font.setColor(Color.RED);
            font.draw(batcher, "hurt", 400, Level.WORLD_HEIGHT * 3 - 10 - 40);
        }
    }

    //GRAPHICS MODE---------------------------------------------------------------------
    private void drawWorld() {
        CustomLevel level = world.level;
        batcher.setColor(Color.WHITE);
        for (int y = 0; y < level.NO_OF_TILES_Y; y++) {
            for (int x = 0; x < level.NO_OF_TILES_X; x++) {
                Tile tile = level.tiles[y][x];
                if (tile.id == -1) continue;
//                System.out.println("x,y: [" + x + ", " + y + "]");
                batcher.draw(Assets.T[tile.id], tile.position.x, tile.position.y);
            }
        }
    }

    //
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

    //
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

    //
    private void drawSpider() {
        Spider spider = world.spider;
        batcher.setColor(Color.WHITE);
        batcher.draw(Assets.T[14+(16*2)-1], spider.position.x, spider.position.y,
                Spider.SPIDER_WIDTH, Spider.SPIDER_HEIGHT);
    }

    private void drawMo() {
        Mo mo = world.mo;
        if (mo.hurt) {
            batcher.setColor(Color.RED);
        } else {
            batcher.setColor(Color.WHITE);
        }
//        batcher.setColor(Color.RED);
        batcher.draw(Assets.wall, mo.position.x, mo.position.y);
//        batcher.setColor(Color.BLACK);
//        batcher.draw(Assets.wall, mo.bounds.lowerLeft.x, mo.bounds.lowerLeft.y);
    }

    public void update(float deltaTime) {
        input();
        camera.update();
        guiCam.update();
        world.update(deltaTime);
//        cameraFollowPlayer();
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (drawMode) {
            batcher.setProjectionMatrix(camera.combined);
            batcher.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            batcher.begin();
            drawWorld();
            drawBoo();
            drawGoomba();
            drawSpider();
            drawMo();
            batcher.end();
        }

        if (debugMode) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            /*DRAW HITBOX*/
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            drawWorldBounds();
            drawBooBounds();
            drawGoombaBounds();
            drawSpiderBounds();
            drawMoBounds();
            drawRay();
//            drawBlocks();       //128 * 128
            shapeRenderer.end();

            /*Draw text*/
            batcher.setProjectionMatrix(guiCam.combined.scl(0.33333f));
            batcher.begin();
            drawPlayerInfo();
//            drawGhostInfo();
            batcher.end();
        }
    }

    @Override
    public void show() {

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
    public void dispose() {

    }
}
