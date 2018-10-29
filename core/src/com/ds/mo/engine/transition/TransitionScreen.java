package com.ds.mo.engine.transition;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

/**
 * Created by Mo on 09/11/2017.
 */

public class TransitionScreen implements Screen {
    private Game game;
    //    private final SpriteBatch batcher;
    private final ShapeRenderer shapeRenderer;
    private ArrayList<TransitionEffect> effects;
    private int currentTransition;      //Current transition pointer within the effects array
    private Screen current;
    private Screen next;

    private final OrthographicCamera camera;

    public TransitionScreen(Game game, OrthographicCamera camera, Screen current, Screen next,
                            ArrayList<TransitionEffect> effects) {
//        super(game);
        this.game = game;
        this.effects = effects;
        this.current = current;     //Screen currently being rendered
        this.next = next;

//        this.shapeRenderer = shapeRenderer;
        this.shapeRenderer = new ShapeRenderer();
        this.camera = camera;
        System.out.println("-------------------TRANSITION SCREEN------------------------");
        System.out.println("CameraPos: "+camera.position);
        System.out.println();
    }

    @Override
    public void show() {

    }

    public void update(float deltaTime) {
//        game.getInput().getKeyEvents();
//        game.getInput().getTouchEvents();
//        camera.update();
    }

    @Override
    public void render(float delta) {
        update(delta);

        //If we have reached the end of the transition
        if (currentTransition >= effects.size()) {
            game.setScreen(next);
            return;
        }
        // TODO: 14/11/2017 Don't have to bing a texture when drawing a rect (udpdate for open gl 2.3+)


//        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        effects.get(currentTransition).update(delta);
        effects.get(currentTransition).draw(current, next, delta);

        Gdx.gl.glEnable(GL20.GL_BLEND);     //effects.draw() disables blend on call to batch.end()
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        float alpha = effects.get(currentTransition).getAlpha();
//        System.out.println(alpha);
        shapeRenderer.setColor(0, 0f, 0f, alpha);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        if (effects.get(currentTransition).isFinished()) {
            currentTransition++;
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
    public void dispose() {
        current.dispose();
    }
}
