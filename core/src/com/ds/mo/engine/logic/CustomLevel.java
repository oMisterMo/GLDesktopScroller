package com.ds.mo.engine.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.awt.Point;

public class CustomLevel {
    public final float WORLD_WIDTH;
    public final float WORLD_HEIGHT;
    public final int NO_OF_TILES_X;
    public final int NO_OF_TILES_Y;

    //---------------------------------------------------------------------------------------------
    //Game fields
    public final Tile[][] tiles;
    public Vector2 center = new Vector2();

    //id of solid tiles (manually)
    private int[] solidTiles = {3, 4, 5, 6, 16, 17, 18, 19, 20, 21, 32, 34, 35, 36, 37, 38, 39, 40,
            49, 50, 51, 52, 53, 54, 64, 65, 66, 67, 80, 81, 82, 83, 84, 85, 86, 87, 88, 96, 97, 98,
            99, 101, 102, 103, 104, 112, 113, 114, 115, 140, 141, 142, 156, 157, 158, 172, 173, 174,
            220, 221, 222, 236, 237, 238, 252, 253, 254, 14};   //14 = spike

    public CustomLevel(int xTiles, int yTiles) {
        System.out.println("Loading CUSTOM level...");
        //Set size of the world based on number of tiles
        NO_OF_TILES_X = xTiles;
        NO_OF_TILES_Y = yTiles;
        WORLD_WIDTH = Tile.TILE_WIDTH * NO_OF_TILES_X;
        WORLD_HEIGHT = Tile.TILE_HEIGHT * NO_OF_TILES_Y;
        this.tiles = new Tile[NO_OF_TILES_Y][NO_OF_TILES_X];
        init();
        center.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
        System.out.println("No x tiles: " + NO_OF_TILES_X);
        System.out.println("No y tiles: " + NO_OF_TILES_Y);
    }

    private void init() {
        //Initialise each Tile to empty
        nullTiles();   //sets to null
        initTiles();    //create empty tiles
        setIdAll(Tile.EMPTY);   //sets to empty

//        loadLevel("simpleScroller.json");
//        loadLevel("testRoom.json");
//        setSolidTiles();  //set solid tiles after loading levels
//        setRoom1();

//        setRandomBlocks();
    }

    /**
     * Sets all spikeBlocks to null
     */
    public void nullTiles() {
        System.out.println("Setting all tiles to null...");
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                tiles[y][x] = null;
            }
        }
    }

    /**
     * Called from the constructor, sets the position of all tiles
     */
    private void initTiles() {
        System.out.println("Initialising tiles...");
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                tiles[y][x] = new Tile(
                        x * Tile.TILE_WIDTH,
                        y * Tile.TILE_HEIGHT,
                        new Point(x, y));
            }
        }
    }

    /**
     * Sets all spikeBlocks to empty
     */
    public void setIdAll(int id) {
        System.out.println("Setting all tiles to " + id + "...");
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                tiles[y][x].id = id;
            }
        }
    }

    private boolean isSolidTile(int value) {
        int len = solidTiles.length;
        for (int i = 0; i < len; i++) {
            if (value == solidTiles[i]) return true;
        }
        return false;
    }

    /**
     * Set all solid tiles in one go
     */
    public void setSolidTiles() {
        System.out.println("Setting solid tiles...");
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                Tile t = tiles[y][x];
                // TODO: 30/09/2018 set solid tiles through TILED and load
//                if (t.id == 140 || t.id == 141 || t.id == 142 || t.id == 156 || t.id == 158
////                        || t.id == 172 || t.id == 173 || t.id == 174
////                        || t.id == 3 || t.id == 19 || t.id == 104 || t.id == 50) {
////                    t.solid = true;
////                }
                if (isSolidTile(t.id)) t.solid = true;
            }
        }
    }

    /**
     * Set the tiles individually
     *
     * @param t
     */
    private void setSolidTile(Tile t) {
//        /*ATTEMPT 1*/
//        if (t.id == 140 || t.id == 141 || t.id == 142 || t.id == 156 || t.id == 158
//                || t.id == 172 || t.id == 173 || t.id == 174
//                || t.id == 3 || t.id == 19 || t.id == 104) {
//            t.solid = true;
//        }

        /*ATTEMPT 2*/
        if (isSolidTile(t.id)) t.solid = true;
    }

    /**
     * Set the tiles individually
     *
     * @param t
     */
    private void setDeathTile(Tile t) {
//        /*ATTEMPT 1*/
//        if (t.id == 140 || t.id == 141 || t.id == 142 || t.id == 156 || t.id == 158
//                || t.id == 172 || t.id == 173 || t.id == 174
//                || t.id == 3 || t.id == 19 || t.id == 104) {
//            t.solid = true;
//        }

        /*ATTEMPT 2*/
        if (t.id == 14) t.death = true;
    }

    public void loadLevel(String levelName) {
        System.out.println("Loading json map....");
        JsonReader jsonReader = new JsonReader();
        JsonValue jsonValue = jsonReader.parse(Gdx.files.internal(levelName));
        int[] id = jsonValue.get("layers").get(0).get("data").asIntArray();
        int len = id.length;
        for (int i = 0; i < len; i++) {
            int x = i % NO_OF_TILES_X;
            int y = NO_OF_TILES_Y - (i / NO_OF_TILES_X) - 1;
//            System.out.println("x,y: " + "[" + x + "," + y + "]");
//            System.out.print("id: " + id[i] + " \n");
            Tile t = tiles[y][x];
            t.id = id[i] - 1;
            setSolidTile(t);
            setDeathTile(t);
        }
    }

    public void printTiles() {
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                Tile t = tiles[y][x];
                System.out.println(t.toString());
            }
        }
    }

    private void reset() {
        System.out.println("reset...");
//        state = WORLD_STATE_READY;
    }

    public Tile getTile(int x, int y) {
        /*CLAMP ver 2 (clamp between 0 - Tiles.length*/
        if (x < 0) {
            x = 0;
            System.out.println("x < 0, clamping to 0");
        }
        if (y < 0) {
            y = 0;
            System.out.println("y < 0, clamping to 0");
        }
        if (x >= NO_OF_TILES_X) {
            x = NO_OF_TILES_X - 1;
            System.out.println("x clamped to: " + (NO_OF_TILES_X - 1));
        }
        if (y >= NO_OF_TILES_Y) {
            y = NO_OF_TILES_Y - 1;
            System.out.println("y clamped to: " + (NO_OF_TILES_Y - 1));
        }
        return tiles[y][x];
    }

    public Tile getTile(Point point) {
        return getTile(point.x, point.y);
    }

    public void update(float deltaTime) {

    }
}
