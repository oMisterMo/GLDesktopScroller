package com.ds.mo.engine.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ds.mo.engine.DesktopGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "GLDesktopScroller";
//        config.fullscreen = true;
        int scale = 3;
        config.width = 320 * scale;
        config.height = 180 * scale;
		new LwjglApplication(new DesktopGame(), config);
	}
}
