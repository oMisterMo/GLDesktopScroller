package com.ds.mo.engine.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.ds.mo.common.Helper;

import java.awt.Point;

public class Level {
    public static float WORLD_WIDTH = 320;
    public static float WORLD_HEIGHT = 180;

    //Triple the screen width
    public static final int WORLD_LENGTH = 3;
    public static final int NO_OF_TILES_X = (int) ((WORLD_WIDTH / Tile.TILE_WIDTH)) * WORLD_LENGTH;       //60
    public static final int NO_OF_TILES_Y = (int) (WORLD_HEIGHT / Tile.TILE_HEIGHT);                      //11

    //---------------------------------------------------------------------------------------------
    //Game fields
    public final Tile[][] tiles;
    public static int FLOOR_LEVEL = 3;

    public Level() {
        System.out.println("Loading level...");
        this.tiles = new Tile[NO_OF_TILES_Y][NO_OF_TILES_X];
        init();


        System.out.println("No x tiles: " + NO_OF_TILES_X);
        System.out.println("No y tiles: " + NO_OF_TILES_Y);
    }

    private void init() {
        //Initialise each Tile to empty
        nullTiles();   //sets to null
        initTiles();    //create empty tiles
        setIdAll(Tile.EMPTY);   //sets to empty

        loadLevel("simpleScroller.json");
//        loadLevel("testRoom.json");
        setSolidTiles();  //set solid tiles after loading levels
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
//                        x * Tile.TILE_WIDTH + Tile.TILE_WIDTH / 2,
//                        y * Tile.TILE_HEIGHT + Tile.TILE_HEIGHT / 2,
                        new Point(x, y));
            }
        }
    }

    /**
     * Sets all spikeBlocks to empty
     */
    public void setIdAll(int type) {
        System.out.println("Setting all tiles to " + type + "...");
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                tiles[y][x].id = type;
            }
        }
    }

    public void setSolidTiles() {
        System.out.println("Setting solid tiles...");
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                Tile t = tiles[y][x];
                // TODO: 30/09/2018 set solid tiles through TILED and load
                if (t.id == 140 || t.id == 141 || t.id == 142 || t.id == 156 || t.id == 158
                        || t.id == 172 || t.id == 173 || t.id == 174
                        || t.id == 3 || t.id == 19 || t.id == 104) {
                    t.solid = true;
                }
            }
        }
    }

    private void setSolidTile(Tile t) {
        if (t.id == 140 || t.id == 141 || t.id == 142 || t.id == 156 || t.id == 158
                || t.id == 172 || t.id == 173 || t.id == 174
                || t.id == 3 || t.id == 19 || t.id == 104) {
            t.solid = true;
        }
    }

    private void setSolidTiles2OLD() {
        System.out.println("Setting solid tiles...");
        for (int y = 0; y < NO_OF_TILES_Y; y++) {
            for (int x = 0; x < NO_OF_TILES_X; x++) {
                Tile t = tiles[y][x];
                if (t.id == Tile.WALL || t.id == Tile.GROUND_TL || t.id == Tile.GROUND_TM
                        || t.id == Tile.GROUND_TR || t.id == Tile.GROUND_ML || t.id == Tile.GROUND_MR
                        || t.id == Tile.GROUND_LL || t.id == Tile.GROUND_LM || t.id == Tile.GROUND_LR) {
                    t.solid = true;
                }
            }
        }
    }

    private void loadLevel(String levelName) {
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
//            setSolidTile(t);
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

    /* ************************************************************
     *
     * HERE I DO DA SETTING THE TILES MANUALLY
     *
     * ************************************************************/

    public void setRoom1() {
        /* SET GROUND */
        //Draw bottom left tile
        tiles[FLOOR_LEVEL][0].id = Tile.GROUND_TL;
//        tiles[FLOOR_LEVEL][0].solid = true;
        //Set whole floor
        for (int i = 1; i < NO_OF_TILES_X - 1; i++) {
            tiles[FLOOR_LEVEL][i].id = Tile.GROUND_TM;
//            tiles[FLOOR_LEVEL][i].solid = true;
        }
        //SET bottom right
        tiles[FLOOR_LEVEL][NO_OF_TILES_X - 1].id = Tile.GROUND_TR;
//        tiles[FLOOR_LEVEL][NO_OF_TILES_X - 1].solid = true;

        /* SET RANDOM GRASS  */
        for (int i = 1; i < NO_OF_TILES_X - 1; i++) {
            if (i == NO_OF_TILES_X - 4 || i == NO_OF_TILES_X - 3 || i == NO_OF_TILES_X - 2) {
                continue;
            }
            tiles[FLOOR_LEVEL + 1][i].id = Helper.Random(Tile.GRASS_T0, Tile.GRASS_T3);
        }
        //Set fire blocks
        tiles[FLOOR_LEVEL + 1][3].id = Tile.GRASS_T3;
        tiles[FLOOR_LEVEL + 1][54].id = Tile.GRASS_T3;
        tiles[FLOOR_LEVEL + 1][59].id = Tile.GRASS_T3;
        setFloorTiles();
        setDoor(10, FLOOR_LEVEL);
        setDoor(20, FLOOR_LEVEL);
        setDoor(30, FLOOR_LEVEL);
        setBigDoor(55, FLOOR_LEVEL);

        //Draw random blocks
//        setRandomBlocks();

        tiles[FLOOR_LEVEL + 3][1].id = Tile.GROUND_MM;
        tiles[FLOOR_LEVEL + 3][1].solid = true;

        tiles[FLOOR_LEVEL + 3][3].id = Tile.GROUND_MM;
        tiles[FLOOR_LEVEL + 3][3].solid = true;

        tiles[FLOOR_LEVEL + 5][1].id = Tile.GROUND_MM;
        tiles[FLOOR_LEVEL + 5][1].solid = true;

//        tiles[++y][x].id = Tile.GROUND_MM;
//        tiles[y][x].solid = true;
//        Log.d("World", "lowerLeft: " + tiles[0][0].bounds.lowerLeft);
//        Log.d("World", "pos.x: " + tiles[0][0].position.x);
//        Log.d("World", "pos.y: " + tiles[0][0].position.y);

    }

    public void setRoom2() {
        /* SET GROUND */
        //Draw bottom left tile
        tiles[0][0].id = Tile.GROUND_TL;
//        tiles[0][0].solid = true;
        //Set whole floor
        for (int i = 1; i < NO_OF_TILES_X - 1; i++) {
            tiles[0][i].id = Tile.GROUND_TM;
//            tiles[0][i].solid = true;
        }
        //SET bottom right
        tiles[0][NO_OF_TILES_X - 1].id = Tile.GROUND_TR;
//        tiles[0][NO_OF_TILES_X - 1].solid = true;

        /* SET RANDOM GRASS  */
        for (int i = 1; i < NO_OF_TILES_X - 1; i++) {
            if (i == NO_OF_TILES_X - 4 || i == NO_OF_TILES_X - 3 || i == NO_OF_TILES_X - 2) {
                continue;
            }
            tiles[1][i].id = Helper.Random(Tile.GRASS_T0, Tile.GRASS_T3);
        }
        setDoor(10);
        setDoor(20);
        setDoor(30);
        setBigDoor(55);
    }

    public void setTrainingRoom() {
        /* SET GROUND */
        int worldLen = NO_OF_TILES_X / WORLD_LENGTH;
        //Set whole floor
        for (int i = 0; i < worldLen - 1; i++) {
            tiles[0][i].id = Tile.WALL;
//            tiles[0][i].solid = true;
        }

        //Draw left wall
        for (int i = 0; i < NO_OF_TILES_Y; i++) {
            Tile t = tiles[i][0];
            t.id = Tile.WALL;
//            t.solid = true;
        }
        //Draw right wall
        for (int i = 0; i < NO_OF_TILES_Y; i++) {
            Tile t = tiles[i][worldLen - 1];
            t.id = Tile.WALL;
//            t.solid = true;
        }
    }

    public void setTrainingRoom2() {
        /* SET GROUND */
        int worldLen = NO_OF_TILES_X / WORLD_LENGTH;
        //Set whole floor
        for (int i = 0; i < worldLen - 1; i++) {
            tiles[0][i].id = Tile.WALL;
//            tiles[0][i].solid = true;
        }

        for (int i = 3; i < worldLen - 1; i++) {
            tiles[3][i].id = Tile.WALL;
//            tiles[0][i].solid = true;
        }
        for (int i = 3; i < worldLen - 1; i++) {
            tiles[5][i].id = Tile.WALL;
//            tiles[0][i].solid = true;
        }

    }

    private void setFloorTiles() {
        if (FLOOR_LEVEL > 0) {
            for (int y = 0; y < FLOOR_LEVEL; y++) {
                for (int x = 1; x < NO_OF_TILES_X - 1; x++) {
                    Tile t = tiles[y][x];
                    t.id = Tile.GROUND_MM;
                    t.solid = true;
                }
            }
            for (int i = 0; i < FLOOR_LEVEL; i++) {
                //left wall tiles
                Tile left = tiles[i][0];
                left.id = Tile.GROUND_ML;
                left.solid = true;
                Tile right = tiles[i][NO_OF_TILES_X - 1];
                right.id = Tile.GROUND_MR;
                right.solid = true;

            }
        }
    }

    private void setDoor(int x) {
        setDoor(x, 0);
    }

    private void setDoor(int x, int floor) {
        if (x < 0 || x + 3 > NO_OF_TILES_X) {
            //Don't go out of bounds
            x = 0;
        }
        /* SET DOOR */
        tiles[floor + 2][x + 0].id = Tile.DOOR0;
        tiles[floor + 2][x + 1].id = Tile.DOOR1;
        tiles[floor + 2][x + 2].id = Tile.DOOR2;
        tiles[floor + 1][x + 0].id = Tile.DOOR3;
        tiles[floor + 1][x + 1].id = Tile.DOOR4;
        tiles[floor + 1][x + 2].id = Tile.DOOR5;
    }

    private void setBigDoor(int x) {
        setBigDoor(x, 0);
    }

    private void setBigDoor(int x, int floor) {
        if (x < 0 || x + 4 > NO_OF_TILES_X) {
            //Don't go out of bounds
            x = 0;
        }
        /* SET DOOR */
        tiles[floor + 3][x + 0].id = Tile.BIGDOOR0;
        tiles[floor + 3][x + 1].id = Tile.BIGDOOR1;
        tiles[floor + 3][x + 2].id = Tile.BIGDOOR1;
        tiles[floor + 3][x + 3].id = Tile.BIGDOOR2;
        tiles[floor + 2][x + 0].id = Tile.BIGDOOR3;
        tiles[floor + 2][x + 1].id = Tile.BIGDOOR4;
        tiles[floor + 2][x + 2].id = Tile.BIGDOOR4;
        tiles[floor + 2][x + 3].id = Tile.BIGDOOR5;
        tiles[floor + 1][x + 0].id = Tile.BIGDOOR3;
        tiles[floor + 1][x + 1].id = Tile.BIGDOOR4;
        tiles[floor + 1][x + 2].id = Tile.BIGDOOR4;
        tiles[floor + 1][x + 3].id = Tile.BIGDOOR5;
    }

    private void setRandomBlocks() {
        int x = 14;
        int y = NO_OF_TILES_Y - 1;

        for (int i = 0; i < NO_OF_TILES_X / 2 - 2; i += 2) {
            tiles[y][i].id = 3;
            tiles[y][i].solid = true;
        }
    }

    public void update(float deltaTime) {

    }
}
