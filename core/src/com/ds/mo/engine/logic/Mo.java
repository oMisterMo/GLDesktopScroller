package com.ds.mo.engine.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.ds.mo.common.DynamicGameObject;
import com.ds.mo.common.Helper;
import com.ds.mo.common.Ray;

public class Mo extends DynamicGameObject {
    static float scale = 1.23076931f;
    public static final float MO_WIDTH = 16 / scale;
    public static final float MO_HEIGHT = 16 / scale;
//    public static final float JUMP_HEIGHT = Tile.TILE_HEIGHT / 4;   //Tile.TILE_HEIGHT / 4 = 4


    private static final int TERMINAL_VELOCITY = 16;
//    private static final int GRAVITY = 1000;
//    private static final int JUMP = 480;
//    public static final float JUMP_HEIGHT = 6.5f;
//    public static final float MO_WIDTH = 20;
//    public static final float MO_HEIGHT = 40;

    private static final int RECOIL_X = 2;
    private static final int RECOIL_Y = 2;
    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    public Vector2 facing;
//    private final Level level;

    public float xsp;
    public float ysp;
    public float gsp;
    public float acc = 2;       //acceleration
    public float dec = 8;       //deceleration
    public float frc = 0.2f;       //friction
    public int top_speed = 6;

    private int key_left = 0;
    private int key_right = 0;

    private int key_up = 0;
    private int key_down = 0;
    private int jump_key = 0;
    private int move = 0;
    private int move2 = 0;

    public boolean grounded = false;
    public boolean inAir = true;

    private float justJumped = 0; //Time since player pressed jump
    public float jump_height = 4;
    public float gravity = -5 * 2f;
    //    public int walk_speed = 80;
    public float walk_speed = 80 * 0.01666f;

    /*Ray colliders*/
    public static final int RAY_LEN = 8;
    public Ray leftFoot;
    public Ray rightFoot;
    public Ray rightSen;
    public Ray leftSen;
    public Ray leftHead;
    public Ray rightHead;
    private Vector2 temp = new Vector2();

    public boolean hurt = false;
    public int lastHurt = 0;

    private int elapsedTime = 0;

    public Mo(float x, float y) {
        super(x, y, MO_WIDTH, MO_HEIGHT);
        System.out.println("Loading player...");
//        this.level = level;
        this.facing = new Vector2(RIGHT, 0);    //either -1 or 1 (left or right)

        initRay();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
////                System.out.println("key down");
//                //Movement
//                if (keycode == Input.Keys.A) {
//                    System.out.println("A DOWN");
//                    moveLeft();
//                }
//                if (keycode == Input.Keys.D) {
//                    System.out.println("D DOWN");
//                    moveRight();
//                }
                if (!hurt) {
                    //Jumping
                    if (keycode == Input.Keys.SPACE) {
//                        System.out.println("Space pressed");
                        if (grounded) {
                            jump();
                            justJumped = 0.5f;    //Time in seconds since jump
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
////                System.out.println("key up");
//                //Movement
//                if (keycode == Input.Keys.A) {
//                    xsp = 0;
//                }
//                if (keycode == Input.Keys.D) {
//                    xsp = 0;
//                }

                //Jumping
                if (keycode == Input.Keys.SPACE) {
//                    System.out.println("Space released");
                    if (justJumped > 0) lowJump();
                }
                return false;
            }
        });
    }

    private void initRay() {
        temp.set(position);
        temp.x += 2;
        temp.y += Mo.MO_HEIGHT / 2;
        leftFoot = new Ray(temp, new Vector2(0, -1));
        leftHead = new Ray(temp, new Vector2(0, 1));
        temp.x = position.x + Mo.MO_WIDTH;
        temp.x -= 2;
        rightFoot = new Ray(temp, new Vector2(0, -1));
        rightHead = new Ray(temp, new Vector2(0, 1));
        //Set temp to center of player (for left/right sensor)
        temp.set(position);
        temp.x += Mo.MO_WIDTH / 2;
        temp.y += Mo.MO_HEIGHT / 2;
        leftSen = new Ray(temp, new Vector2(-1, 0));
        rightSen = new Ray(temp, new Vector2(1, 0));
    }

    public void updateRay() {
        //Set temp to mo.position (bottom left)
        temp.set(position);
        //Left sensor (shift 2 pixels into player)
        temp.x += 2;
        temp.y += Mo.MO_HEIGHT / 2;
        leftFoot.start.set(temp);
        leftHead.start.set(temp);
        //Right sensor (shift 2 pixels into player)
        temp.x = position.x + Mo.MO_WIDTH;
        temp.x -= 2;
        rightFoot.start.set(temp);
        rightHead.start.set(temp);
        //Set temp to center of player (for left/right sensor)
        temp.set(position);
        temp.x += Mo.MO_WIDTH / 2;
        temp.y += Mo.MO_HEIGHT / 2;
        leftSen.start.set(temp);
        rightSen.start.set(temp);
    }

    private void getInput() {
//        //UP/DOWN
//        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
//            key_up = 1;
//        } else {
//            key_up = 0;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
//            key_down = 1;
//        } else {
//            key_down = 0;
//        }

        //A/D keys
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            // TODO: 26/09/2018 Check if tile 2 pixels to the left of player is a solid block
//            System.out.println(tileToLeft());
            key_left = 1;
            facing.x = LEFT;
            if (xsp > 0) xsp = 0;
        } else {
            key_left = 0;
//            xsp = 0;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            key_right = 1;
            facing.x = RIGHT;
            if (xsp < 0) xsp = 0;
        } else {
            key_right = 0;
//            xsp = 0;
        }
        move = key_right - key_left;    //move: -1 (left), 0, or 1 (right)
        move2 = key_up - key_down;    //move: -1 (left), 0, or 1 (right)
//        System.out.println("move: " + move);

//        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
//            if (grounded) {
//                jump();
////                jump_key = 1;
//            } else {
//                System.out.println("Can't jump, must be grounded");
//            }
//        }
//        if (!Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
//            //if travelling up
//            if (ysp > 0) {
//                System.out.println("low jump");
//                ysp *= 0.5;
//            }
//        }
    }

    private void getInput2() {
        if (!hurt) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                facing.x = LEFT;
                //Pressed LEFT button while moving right
                if (xsp > 0) {
                    xsp -= dec;
                } else if (xsp > -walk_speed) {
                    xsp -= acc;
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                facing.x = RIGHT;
                if (xsp < 0) {
                    xsp += dec;
                } else if (xsp < walk_speed) {
                    xsp += acc;
                }
            } else {
                //not moving apply friction
                xsp -= Math.min(Math.abs(xsp), frc) * Helper.Sign(xsp);
            }
        } else {
            //Apply friction when hurt
            float friction = 0.03f;
            xsp -= Math.min(Math.abs(xsp), friction) * Helper.Sign(xsp);
        }
    }

    private void clampSpeed() {
        if (xsp < -walk_speed) {
            xsp = -walk_speed;
        }
        if (xsp > walk_speed) {
            xsp = walk_speed;
        }
    }

//    private int pointToTileCoordsX(float x) {
//        //Array position
//        return (int) Math.floor(x / Tile.TILE_WIDTH);
//    }
//
//    private int pointToTileCoordsY(float y) {
//        //Array position
//        return (int) Math.floor(y / Tile.TILE_HEIGHT);
//    }
//
//    private Tile worldToTile(float x, float y) {
//        //Given any point -> returns a tile at the position
//        return level.getTile(pointToTileCoordsX(x), pointToTileCoordsY(y));
//    }

    public void timer() {
        if (hurt && elapsedTime - lastHurt > 60) {
//            System.out.println("elapsedTime - lastHurt: " + (elapsedTime - lastHurt));
//            System.out.println("NOt hurt anymore");
            hurt = false;
        }
        elapsedTime += 1;
    }

    public boolean isMoving() {
        return (xsp < 0 || xsp > 0 || ysp < 0 || ysp > 0);
    }

    public void moveLeft() {
        xsp = -walk_speed * Gdx.graphics.getDeltaTime();
    }

    public void moveRight() {
        xsp = walk_speed * Gdx.graphics.getDeltaTime();
    }

    public void jump() {
        System.out.println("JUMP");
        grounded = false;
        inAir = true;
        jump_key = 0;

        ysp = jump_height;
    }

    public void lowJump() {
        System.out.println("Low Jump");
        if (ysp > 0) {
            ysp *= 0.2f;
        }
    }

    public void hurt() {
        System.out.println("Hurt");
        hurt = true;
        elapsedTime = 0;
        //Kick back
//        ysp += 200 * Gdx.graphics.getDeltaTime();
        xsp = Helper.Sign(xsp) * -RECOIL_X;
        ysp = RECOIL_Y;
    }

    public void jumpOnEnemy() {
//        jump();
        grounded = false;
        inAir = true;
        jump_key = 0;

        ysp = jump_height * 0.7f;
    }

    public void update(float deltaTime) {
        //Handle input
//        getInput();
        getInput2();
//        xsp += (move * walk_speed) * deltaTime;
//        ysp += (move2 * walk_speed) * deltaTime;
        clampSpeed();

        /*Handle jump*/
//        if (grounded && jump_key == 1) {
////            System.out.println("Jump");
//            grounded = false;
//            inAir = true;
//            jump_key = 0;
//
////            ysp = JUMP_HEIGHT;
//            ysp = jump_height;
//        }


        //Handle gravity
        if (justJumped > 0) {
            justJumped -= deltaTime;
//            System.out.println(justJumped);
        }
        if (inAir) {
            ysp += gravity * deltaTime;
//            ysp = Helper.Clamp(ysp, -TERMINAL_VELOCITY * 3, TERMINAL_VELOCITY * 3);
        }
        position.add(xsp, ysp);
        bounds.lowerLeft.set(position);

        timer();
    }
}
