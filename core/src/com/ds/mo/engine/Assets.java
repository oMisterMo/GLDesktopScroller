package com.ds.mo.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ds.mo.common.Animation;

public class Assets {
    public static final int TILE_COUNT = 256;
    public static Texture worldAtlas;

    public static Animation goombaAnim;
    //Tiles
    public static TextureRegion particle;
    public static TextureRegion solidWhite;
    public static TextureRegion drawRectFull;
    public static TextureRegion wall;

    public static TextureRegion ground_tl;  //(ground)
    public static TextureRegion ground_tm;
    public static TextureRegion ground_tr;
    public static TextureRegion ground_ml;
    public static TextureRegion ground_mm;
    public static TextureRegion ground_mr;
    public static TextureRegion ground_ll;
    public static TextureRegion ground_lm;
    public static TextureRegion ground_lr;

    public static TextureRegion door0;      //(door)
    public static TextureRegion door1;
    public static TextureRegion door2;
    public static TextureRegion door3;
    public static TextureRegion door4;
    public static TextureRegion door5;
    public static TextureRegion bigDoor0;      //(door)
    public static TextureRegion bigDoor1;
    public static TextureRegion bigDoor2;
    public static TextureRegion bigDoor3;
    public static TextureRegion bigDoor4;
    public static TextureRegion bigDoor5;

    public static TextureRegion grass_t0;   //(grass)
    public static TextureRegion grass_t1;
    public static TextureRegion grass_t2;
    public static TextureRegion grass_t3;

    //Mo dust particle
    public static TextureRegion moDust;

    public static TextureRegion[] T;
    public static TextureRegion[][] textureRegions;

    public Assets() {
        worldAtlas = new Texture(Gdx.files.internal("area4.png")); //jungle.png
//        T = new TextureRegion[TILE_COUNT];
//        for(int i=0; i<TILE_COUNT; i++){
//            int num = i%15;
//            System.out.println(num);
////            T[i] = new TextureRegion(worldAtlas, 16,16,16,16);
//        }
//        textureRegions = new TextureRegion[10][60];
        textureRegions = TextureRegion.split(worldAtlas, 16, 16);
        System.out.println("Length of textureRegions: " + textureRegions.length);
        System.out.println("Rows: " + textureRegions[0].length);
        T = new TextureRegion[textureRegions.length * textureRegions[0].length];
        int count = 0;
        for (int y = 0; y < textureRegions[0].length; y++) {
            for (int x = 0; x < textureRegions.length; x++) {
                T[count] = textureRegions[y][x];
                count += 1;
            }
        }
        goombaAnim = new Animation(0.2f, T[13], T[14]);
//        goombaAnim.setPlayMode(Animation.PlayMode.LOOP);
        particle = new TextureRegion(worldAtlas, 0,0,16,16);

        wall = new TextureRegion(worldAtlas, 16 * 3, 0, 16, 16);
        ground_tl = new TextureRegion(worldAtlas, 16 * 12, 16 * 13, 16, 16);
        ground_tm = new TextureRegion(worldAtlas, 16 * 13, 16 * 13, 16, 16);
        ground_tr = new TextureRegion(worldAtlas, 16 * 14, 16 * 13, 16, 16);
        ground_ml = new TextureRegion(worldAtlas, 16 * 12, 16 * 14, 16, 16);
        ground_mm = new TextureRegion(worldAtlas, 16 * 13, 16 * 14, 16, 16);
        ground_mr = new TextureRegion(worldAtlas, 16 * 14, 16 * 14, 16, 16);
        ground_ll = new TextureRegion(worldAtlas, 16 * 12, 16 * 15, 16, 16);
        ground_lm = new TextureRegion(worldAtlas, 16 * 13, 16 * 15, 16, 16);
        ground_lr = new TextureRegion(worldAtlas, 16 * 14, 16 * 15, 16, 16);
        door0 = new TextureRegion(worldAtlas, 16 * 7, 16 * 3, 16, 16);
        door1 = new TextureRegion(worldAtlas, 16 * 8, 16 * 3, 16, 16);
        door2 = new TextureRegion(worldAtlas, 16 * 9, 16 * 3, 16, 16);
        door3 = new TextureRegion(worldAtlas, 16 * 7, 16 * 4, 16, 16);
        door4 = new TextureRegion(worldAtlas, 16 * 8, 16 * 4, 16, 16);
        door5 = new TextureRegion(worldAtlas, 16 * 9, 16 * 4, 16, 16);
        bigDoor0 = new TextureRegion(worldAtlas, 16 * 7, 0, 16, 16);
        bigDoor1 = new TextureRegion(worldAtlas, 16 * 8, 0, 16, 16);
        bigDoor2 = new TextureRegion(worldAtlas, 16 * 9, 0, 16, 16);
        bigDoor3 = new TextureRegion(worldAtlas, 16 * 7, 16, 16, 16);
        bigDoor4 = new TextureRegion(worldAtlas, 16 * 8, 16, 16, 16);
        bigDoor5 = new TextureRegion(worldAtlas, 16 * 9, 16, 16, 16);
        grass_t0 = new TextureRegion(worldAtlas, 16 * 12, 16 * 12, 16, 16);
        grass_t1 = new TextureRegion(worldAtlas, 16 * 13, 16 * 12, 16, 16);
        grass_t2 = new TextureRegion(worldAtlas, 16 * 14, 16 * 12, 16, 16);
        grass_t3 = new TextureRegion(worldAtlas, 16 * 15, 16 * 12, 16, 16);

//        manager = new AssetManager();
//        manager.setLoader(TiledMap.class, new TmxMapLoader());
//        manager.load("testRoom.tmx", TiledMap.class);
//        manager.finishLoading();
//        map = manager.get("testRoom.tmx", TiledMap.class);
//        manager.dispose();
    }
}
