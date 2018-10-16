package com.ds.mo.engine;

import com.badlogic.gdx.Game;

public class DesktopGame extends Game {
//    public SpriteBatch batcher;

    @Override
    public void create() {
//        batcher = new SpriteBatch();
        this.setScreen(new GameScreen(this));
    }

    @Override
    public void render () {
        super.render();
    }

    @Override
    public void pause() {
        super.pause();
        System.out.println("STOPPED");

    }

    @Override
    public void resume() {
        super.resume();
        System.out.println("RESUMED!");
    }

    @Override
    public void dispose() {
//        font.dispose();
//        batcher.dispose();
    }
}
