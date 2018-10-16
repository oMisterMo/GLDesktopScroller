package com.ds.mo.engine;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.ds.mo.engine.logic.Boo;

public class PlainWorld implements Screen {
    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 180;

    private SpriteBatch batcher;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private OrthographicCamera camera;

    private Boo boo;

    private Vector2 intersect = new Vector2();
    private Vector2 out = new Vector2();
    private Vector2 temp = new Vector2();
    private Vector2 center = new Vector2();


    public PlainWorld(){

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

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
