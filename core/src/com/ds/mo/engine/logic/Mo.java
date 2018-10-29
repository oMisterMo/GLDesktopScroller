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

    private Vector2 decc = new Vector2(8, 0);
    private Vector2 fric = new Vector2(0.2f, 0);


    //Previous stuff below----------------------------------------
    public float xsp;
    public float ysp;
    public float gsp;
    public float acc = 2;       //acceleration
    public float dec = 8;       //deceleration
    public float frc = 0.2f;       //friction
    public int top_speed = 6;

    private int jump_key = 0;
    private int move = 0;
    private int move2 = 0;

//    public boolean grounded = false;
//    public boolean inAir = true;

    private float justJumped = 0; //Time since player pressed jump
    public float jump_height = 4;
    public float gravity = -5 * 2f;
//    public int walk_speed = 80;
    public float walk_speed = 80 * 0.01666f;

    /*Ray colliders*/
    public static final int RAY_LEN = 8;
//    public Ray leftFoot;
//    public Ray rightFoot;
//    public Ray rightSen;
//    public Ray leftSen;
//    public Ray leftHead;
//    public Ray rightHead;
//    private Vector2 temp = new Vector2();

    public boolean hurt = false;
    public boolean dead = false;
    public int lastHurt = 0;

    private int elapsedTime = 0;

    public Mo(float x, float y) {
        super(x, y, MO_WIDTH, MO_HEIGHT);
        System.out.println("Loading player...");
//        this.level = level;
        this.facing = new Vector2(RIGHT, 0);    //either -1 or 1 (left or right)
//        accel.set(2, 0);
//        initRay();
        dead = false;

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
//        temp.set(position);
//        temp.x += 2;
//        temp.y += Mo.MO_HEIGHT / 2;
//        leftFoot = new Ray(temp, new Vector2(0, -1));
//        leftHead = new Ray(temp, new Vector2(0, 1));
//        temp.x = position.x + Mo.MO_WIDTH;
//        temp.x -= 2;
//        rightFoot = new Ray(temp, new Vector2(0, -1));
//        rightHead = new Ray(temp, new Vector2(0, 1));
//        //Set temp to center of player (for left/right sensor)
//        temp.set(position);
//        temp.x += Mo.MO_WIDTH / 2;
//        temp.y += Mo.MO_HEIGHT / 2;
//        leftSen = new Ray(temp, new Vector2(-1, 0));
//        rightSen = new Ray(temp, new Vector2(1, 0));
    }

//    public void updateRay() {
//
//    }

    private void getInput() {
        int key_left = 0;
        int key_right = 0;
        int key_up = 0;
        int key_down = 0;
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
            key_left = 1;
            facing.x = LEFT;
        } else {
            key_left = 0;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            key_right = 1;
            facing.x = RIGHT;
        } else {
            key_right = 0;
        }
        move = key_right - key_left;    //move: -1 (left), 0, or 1 (right)
        move2 = key_up - key_down;    //move: -1 (left), 0, or 1 (right)
//        System.out.println("move: " + move);
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

    private void getInput3() {
        // TODO: 21/10/2018 complete with new velocity variable
        if (!hurt && !dead) {
//            if(hurt || dead) return;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                facing.x = LEFT;
                //Pressed LEFT button while moving right
                if (velocity.x > 0) {
//                    xsp -= dec;
//                    velocity.sub(decc);
                    velocity.x -= dec;
                } else if (velocity.x > -walk_speed) {
//                    xsp -= acc;
//                    velocity.sub(accel);
                    velocity.x -= acc;
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                facing.x = RIGHT;
                if (velocity.x < 0) {
//                    xsp += dec;
//                    velocity.add(decc);
                    velocity.x += dec;
                } else if (velocity.x < walk_speed) {
//                    xsp += acc;
//                    velocity.add(accel);
                    velocity.x += acc;
                }
            } else {
                //not moving apply friction
//                xsp -= Math.min(Math.abs(xsp), frc) * Helper.Sign(xsp);
                velocity.x -= Math.min(Math.abs(velocity.x), frc) * Helper.Sign(velocity.x);
            }
        } else {
            //Apply friction when hurt
            float friction = 0.03f;
//            xsp -= Math.min(Math.abs(xsp), friction) * Helper.Sign(xsp);
            velocity.x -= Math.min(Math.abs(velocity.x), friction) * Helper.Sign(velocity.x);
        }
    }

    private void clampXSpeed() {
//        if (xsp < -walk_speed) {
//            xsp = -walk_speed;
//        }
//        if (xsp > walk_speed) {
//            xsp = walk_speed;
//        }
        if (velocity.x < -walk_speed) {
            velocity.x = -walk_speed;
        }
        if (velocity.x > walk_speed) {
            velocity.x = walk_speed;
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
        // TODO: 24/10/2018 make real timer
        if (hurt && elapsedTime - lastHurt > 60) {
            hurt = false;
        }
        elapsedTime += 1;
    }

    public boolean isMoving() {
//        return (xsp < 0 || xsp > 0 || ysp < 0 || ysp > 0);
        return (velocity.x < 0 || velocity.x > 0 || velocity.y < 0 || velocity.y > 0);
    }

    public void moveLeft() {
        xsp = -walk_speed * Gdx.graphics.getDeltaTime();
    }

    public void moveRight() {
        xsp = walk_speed * Gdx.graphics.getDeltaTime();
    }

    public void jump() {
//        System.out.println("JUMP");
        grounded = false;
        inAir = true;
        jump_key = 0;

//        ysp = jump_height;
        velocity.y = jump_height;
    }

    public void lowJump() {
//        System.out.println("Low Jump");
//        if (ysp > 0) {
//            ysp *= 0.2f;
//        }
        if (velocity.y > 0) {
            velocity.y *= 0.2f;
        }
    }

    public void hurt() {
//        System.out.println("Hurt");
        hurt = true;
        elapsedTime = 0;
        //Kick back
//        xsp = Helper.Sign(xsp) * -RECOIL_X; //Send player at opposite velocity
//        ysp = RECOIL_Y;
        velocity.x = Helper.Sign(velocity.x) * -RECOIL_X; //Send player at opposite velocity
        velocity.y = RECOIL_Y;
    }

    public void die(){
        dead = true;
//        elapsedTime = 0;
        velocity.y = jump_height/2;
        grounded = false;
        inAir = true;
    }

    public void jumpOnEnemy() {
//        jump();
        grounded = false;
        inAir = true;
        jump_key = 0;

//        ysp = jump_height * 0.7f;
        velocity.y = jump_height * 0.7f;
    }

    public void update(float deltaTime) {
        //Handle input
//        getInput();
//        getInput2();
        getInput3();
//        xsp += (move * walk_speed) * deltaTime;
//        ysp += (move2 * walk_speed) * deltaTime;
        clampXSpeed();

        //Handle gravity
        if (justJumped > 0) {
            justJumped -= deltaTime;
        }
        if (inAir) {
//            ysp += gravity * deltaTime;
            velocity.y += gravity * deltaTime;
//            ysp = Helper.Clamp(ysp, -TERMINAL_VELOCITY * 3, TERMINAL_VELOCITY * 3);
        }
//        position.add(xsp, ysp);
//        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        position.add(velocity.x, velocity.y);
        bounds.lowerLeft.set(position);

        timer();
    }
}
