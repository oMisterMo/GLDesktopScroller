package com.ds.mo.engine.logic;

import com.ds.mo.common.GameObject;

import java.awt.Point;

/**
 * Represents a single Tile instance
 * <p>
 * A Tile is a static game object used to created the game world
 * Created by Mo on 06/10/2017.
 */

public class Tile extends GameObject {
    public static final float TILE_WIDTH = 16;
    public static final float TILE_HEIGHT = 16;

    //Tile Id
    public static final int EMPTY = -1;
    public static final int WALL = 0;
    public static final int GROUND_TL = 1;  //top left
    public static final int GROUND_TM = 2;
    public static final int GROUND_TR = 3;
    public static final int GROUND_ML = 4;
    public static final int GROUND_MM = 5;
    public static final int GROUND_MR = 6;
    public static final int GROUND_LL = 7;
    public static final int GROUND_LM = 8;
    public static final int GROUND_LR = 9;
    public static final int DOOR0 = 10;
    public static final int DOOR1 = 11;
    public static final int DOOR2 = 12;
    public static final int DOOR3 = 13;
    public static final int DOOR4 = 14;
    public static final int DOOR5 = 15;
    public static final int BIGDOOR0 = 16;
    public static final int BIGDOOR1 = 17;
    public static final int BIGDOOR2 = 18;
    public static final int BIGDOOR3 = 19;
    public static final int BIGDOOR4 = 20;
    public static final int BIGDOOR5 = 21;
    public static final int GRASS_T0 = 22;      ///grass on top of ground
    public static final int GRASS_T1 = 23;
    public static final int GRASS_T2 = 24;
    public static final int GRASS_T3 = 25;

    public int type = EMPTY;    //-1
    public boolean solid;
    public boolean hit;         // TODO: 05/11/2017 for debugging, can delete when not needed

    public Point grid;

    public Tile(float x, float y, Point grid) {
        super(x, y, TILE_WIDTH, TILE_HEIGHT);
        this.solid = false;
        this.hit = false;
        this.type = EMPTY;
        this.grid = grid;
    }

    @Override
    public String toString() {
        // TODO: 07/09/2018 or just print grid
        String s = "(" + (int) Math.floor(position.x / TILE_WIDTH) + ", "
                + (int) Math.floor(position.y / TILE_HEIGHT) + ")";
        return s + ": " + type;
    }
}
