package Levels;

import Behavior.*;
import CameraPckg.Camera;
import Colliders.BoxCollider;
import GameObjects.GameObject;
import MovePckg.MovePlayer;
import Renderers.RendererComposite;
import Renderers.RendererLeaf;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class LevelFactory {
    public static final int TILE_SIZE = 20;
    public static final int MAX_LEVELS = 7;

    private static final float MAX_CLOUD_SPEED = 0.15f;
    private static final float MIN_CLOUD_SPEED = -0.15f;

    private int index = -2;
    private PApplet pApplet;
    private Camera camera;
    private PImage wallSprite;
    private PImage groundSprite;
    private PImage dynamicSprite;
    private PImage exitSprite;
    private PImage cloud1Sprite;
    private PImage heavyExit;
    private PImage atSprite;
    private PImage tubeSprite;
    private GameObject player;
    private LevelInfo levelInfo;

    public LevelFactory(PApplet pApplet, Camera camera) {
        this.pApplet = pApplet;
        this.camera = camera;
        this.wallSprite = pApplet.loadImage("wall.png");
        this.groundSprite = pApplet.loadImage("grass.jpg");
        this.dynamicSprite = pApplet.loadImage("dynamic.png");
        this.exitSprite = pApplet.loadImage("exit.png");
        this.cloud1Sprite = pApplet.loadImage("cloud2.png");
        this.heavyExit = pApplet.loadImage("heavyExit.png");
        this.atSprite = pApplet.loadImage("atSprite.jpg");
        this.tubeSprite = pApplet.loadImage("tube.png");
        levelInfo = new LevelInfo();
    }

    public GameObject getNextLevel() {
        //init player
        if (index > -2) {
            player = initPlayer();
        }

        buildClouds(10);
        index++;

        switch (index) {
            case -1:
                break;
            case 0:
                buildTutorial();
                break;
            case 1:
                buildLevel1();
                break;
            case 2:
                buildLevel2();
                break;
            case 3:
                buildLevel3();
                break;
            case 4:
                buildLevel4();
                break;
            case 5:
                buildLevel5();
                break;
            case 6:
                buildLevel6();
                break;
            case 7:
                buildLevel7();
                break;
            default:
                System.out.println("Unknown level");
        }

        return player;
    }

    public LevelInfo getLevelInfo() {
        return levelInfo;
    }

    public void decreaseLevel() {
        index--;
    }

    public void setLevel(int index) {
        this.index = index;
    }

    public int getLevel() {
        return index;
    }

    private GameObject initPlayer() {
        //init player
        GameObject player = new GameObject(pApplet, 0, 0, true);
        player.addComponent(new MovePlayer(pApplet, player, null));
        player.addComponent(new BoxCollider(pApplet, player, 50, 50));
        player.addComponent(new RendererLeaf(pApplet, pApplet.loadImage("ghost.png"), new PVector(0, 0), 1, 1));
        Turn turn = new Turn(pApplet, player);
        player.addComponent(turn);
        player.tag = "Player";

        return player;
    }

    private void buildClouds(int num) {
        GameObject cloud;
        for (int i = 0; i < num; i++) {
            cloud = new GameObject(pApplet,
                    ((float) (Math.random() * pApplet.width - pApplet.width/2)),
                    ((float) (-Math.random() * pApplet.height/2) - 100));
            cloud.addComponent(new RendererLeaf(pApplet, cloud1Sprite, new PVector(0, 0), 1, 1));
            cloud.velocity.x = (float) (MIN_CLOUD_SPEED + Math.random()*(MAX_CLOUD_SPEED - MIN_CLOUD_SPEED));
            cloud.drag = false;
        }
    }

    private GameObject buildObject(float x, float y, boolean gravity, int width, int height, PImage img) {
        GameObject obj = new GameObject(pApplet, x, y, gravity);
        obj.addComponent(new BoxCollider(pApplet, obj, width, height));
        obj.addComponent(new RendererComposite(pApplet, obj, img, TILE_SIZE));
//        obj.addComponent(new Turnable(pApplet, obj, player));

        return obj;
    }

    private GameObject buildAttachable(float x, float y, boolean gravity, int width, int height, GameObject player) {
        GameObject obj = new GameObject(pApplet, x, y, gravity);
        obj.addComponent(new BoxCollider(pApplet, obj, width, height));
        obj.addComponent(new RendererComposite(pApplet, obj, atSprite, TILE_SIZE));
        obj.addComponent(new Attachable(pApplet, obj, player, camera));

        return obj;
    }

    private GameObject buildCollidable(float x, float y, boolean gravity, int width, int height, PImage img) {
        GameObject obj = new GameObject(pApplet, x, y, gravity);
        obj.addComponent(new BoxCollider(pApplet, obj, width, height));
        obj.addComponent(new RendererComposite(pApplet, obj, img, TILE_SIZE));
        obj.tag = "Collidable";

        return obj;
    }

    private GameObject buildExit(float x, float y, boolean gravity, GameObject player, boolean isTurnable) {
        GameObject exit = new GameObject(pApplet, x, y, gravity);
        exit.addComponent(new BoxCollider(pApplet, exit, Exit.WIDTH, Exit.HEIGHT));
        if (gravity) {
            exit.addComponent(new RendererLeaf(pApplet, heavyExit, new PVector(0, 0), 0.7f, 0.8f));
        }
        else {
            exit.addComponent(new RendererLeaf(pApplet, exitSprite, new PVector(0, 0), 0.9f, 1));
        }
        if (isTurnable) {
            exit.addComponent(new Turnable(pApplet, exit, player));
        }
        exit.addComponent(new Exit(pApplet, exit));

        return exit;
    }

    private void buildTutorial() {
        player.resetPos(-350, -80);
        Turn turn = (Turn) player.getComponent(Turn.class);

        GameObject standPlatf = buildCollidable(480, 100, false, TILE_SIZE*170, TILE_SIZE, groundSprite);
        GameObject wall = buildCollidable(-400, 40, false, TILE_SIZE, 5*TILE_SIZE, wallSprite);
        GameObject longTile = buildObject(-470, 100, false, TILE_SIZE*5, TILE_SIZE, dynamicSprite);
        GameObject longTile1 = buildAttachable(-150, 40, false, TILE_SIZE, 5*TILE_SIZE, player);
        GameObject exit = buildExit(-100, 40, false, player, true);

        GameObject af = new GameObject(pApplet, 600, 40);
        af.addComponent(new AreaEffector(pApplet, af, new PVector(0, -1), 100, 100));

        turn.addTurnables(longTile, longTile1, exit);

        levelInfo.reset("",
                "Tutorial");
    }


    private void buildLevel1() {
        player.resetPos(0, -80);
        Turn turn = (Turn) player.getComponent(Turn.class);
        GameObject standPlatf = buildCollidable(0, 100, false, TILE_SIZE * 20, TILE_SIZE, groundSprite);
        GameObject wall = buildCollidable(-90, 40, false, TILE_SIZE * 3, TILE_SIZE * 5, wallSprite);
        GameObject longTile = buildObject(-200, 100, false, TILE_SIZE * 5, TILE_SIZE, dynamicSprite);
        GameObject exit = buildExit(100, 40, false, player, true);

        turn.addTurnables(longTile, exit);

        levelInfo.reset("Try to get the platform to such position that it is not in collisiion with anything",
                        "Hello Revolving World!");
        // "If one of the objects is in collision with environment, all objects are transparent " +
        // "and move with the player"
    }

    private void buildLevel2() {
        player.resetPos(-200, -80);
        Turn turn = (Turn) player.getComponent(Turn.class);

        GameObject standPlatf = buildCollidable(0, 100, false, TILE_SIZE*40, TILE_SIZE, groundSprite);
        GameObject wall1 = buildCollidable(-300, 40,  false, 60, 100, wallSprite);
        GameObject wall2 = buildCollidable(0, 40, false, 60, 100, wallSprite);
        GameObject wall3 = buildCollidable(300, 40, false, 60, 100, wallSprite);
        GameObject longTile = buildObject(-470, 100, false, TILE_SIZE*5, TILE_SIZE, dynamicSprite);
        GameObject exit = buildExit(370, 40, false, player, true);

        turn.addTurnables(longTile, exit);

        levelInfo.reset("You can change your distance to the objects revolving around you when they're not transparent.",
                "Pong");
    }

    /**
     * learn turning transparency 1
     */
    private void buildLevel3() {
        player.resetPos(-270, -50);
        Turn turn = (Turn) player.getComponent(Turn.class);

        GameObject standPlatf1 = buildCollidable(-200, 0, false, 15*TILE_SIZE, TILE_SIZE, groundSprite);
        GameObject standPlatf2 = buildCollidable(-300, 200, false, 15*TILE_SIZE, TILE_SIZE, groundSprite);
        GameObject wall = buildCollidable(-340, -60, false, TILE_SIZE, 5*TILE_SIZE, wallSprite);
        GameObject longTile1 = buildObject(40, 200, false, 5*TILE_SIZE, TILE_SIZE, dynamicSprite);
        GameObject longTile2 = buildObject(40, 0, false, 5*TILE_SIZE, TILE_SIZE, dynamicSprite);
        GameObject longTile3 = buildObject(160, 200, false, 5*TILE_SIZE, TILE_SIZE, dynamicSprite);
        GameObject exit = buildExit(-300, 140, false, player, true);

        turn.addTurnables(longTile1, longTile2, longTile3);

        levelInfo.reset("When the objects turn around you they are transparent and do not collide with anything",
                "Fall of Faith");
    }


    /**
     * learn turning transparency 2
     */
    private void buildLevel4() {
        player.resetPos(-400, -140);
        Turn turn = (Turn) player.getComponent(Turn.class);

        GameObject standPlatf1 = buildCollidable(200, 100, false, 30*TILE_SIZE, TILE_SIZE, groundSprite);
        GameObject standPlatf2 = buildCollidable(350, 0, false, 30*TILE_SIZE, TILE_SIZE, groundSprite);
        GameObject standPlatf3 = buildCollidable(-400, -100, false, 6*TILE_SIZE, TILE_SIZE, groundSprite);
        GameObject longTile1 = buildObject(-570, 100, false, 10*TILE_SIZE, TILE_SIZE, dynamicSprite);
        GameObject longTile2 = buildObject(-570, -100, false, 10*TILE_SIZE, TILE_SIZE, dynamicSprite);
        GameObject exit = buildExit(370, 40, false, player, true);

        turn.addTurnables(longTile1, longTile2, exit);

        levelInfo.reset("Do not reduce your distance from the exit unless necessary",
                "Don't Go Away");
    }

    /**
     * learn attachability 1
     */
    private void buildLevel5() {
        player.resetPos(-400, -140);
        Turn turn = (Turn) player.getComponent(Turn.class);

        GameObject standPlatf1 = buildCollidable(400, 100, false, 30*TILE_SIZE, TILE_SIZE, groundSprite);
        GameObject standPlatf2 = buildCollidable(-400, 100, false, 10*TILE_SIZE, TILE_SIZE, groundSprite);
        GameObject longTile1 = buildAttachable(-600, 100, false, 9*TILE_SIZE, TILE_SIZE, player);
        GameObject longTile2 = buildObject(-200, 100, false, 9*TILE_SIZE, TILE_SIZE, dynamicSprite);
        GameObject exit = buildExit(370, 40, false, player, true);

        turn.addTurnables(longTile1, longTile2, exit);

        levelInfo.reset("Detach an object when you don't want it to turn around you anymore",
                "I'll Make My Own Path");
    }

    /**
     * learn attachability 2
     */
    private void buildLevel6() {
        player.resetPos(-400, -150);
        Turn turn = (Turn) player.getComponent(Turn.class);

        GameObject standPlatf1 = buildCollidable(350, 100, false, 35*TILE_SIZE, TILE_SIZE, groundSprite);

        GameObject toSpit = new GameObject(pApplet, 400, 0, true);
        toSpit.addComponent(new BoxCollider(pApplet, toSpit, 3*TILE_SIZE, TILE_SIZE));
        toSpit.addComponent(new RendererComposite(pApplet, toSpit, wallSprite, TILE_SIZE));
        GameObject spitter = new GameObject(pApplet, 400, -300, false);
        spitter.addComponent(new ObjectGenerator(pApplet, spitter, toSpit, 100));
        spitter.addComponent(new RendererLeaf(pApplet, tubeSprite, new PVector(0, 0), 0.2f, 0.2f));

        GameObject longTile1 = buildObject(-680, 100, false, 10*TILE_SIZE, TILE_SIZE, dynamicSprite);
        GameObject longTile2 = buildAttachable(-400, -100, false, 10*TILE_SIZE, TILE_SIZE, player);
        GameObject exit = buildExit(600, 40, false, player, true);

        turn.addTurnables(longTile1, longTile2);

        levelInfo.reset("The attachable objects can only be disattached when they're not transparent but they can " +
                        "be attached at any time",
                "Thick as a Temporary Brick");
    }

    /**
     * learn attachability and gravity
     */
    private void buildLevel7() {
        player.resetPos(0, -100);
        Turn turn = (Turn) player.getComponent(Turn.class);

        GameObject standPlatf1 = buildCollidable(0, -50, false, 7*TILE_SIZE, TILE_SIZE, groundSprite);
        GameObject standPlatf2 = buildCollidable(550, 300, false, 10*TILE_SIZE, TILE_SIZE, groundSprite);

        GameObject af = new GameObject(pApplet, -320, 200);
        af.addComponent(new AreaEffector(pApplet, af, new PVector(0, -1), 500, 1000));

        GameObject wall1 = buildCollidable(-60, -110, false, TILE_SIZE, 5*TILE_SIZE, wallSprite);
        GameObject longTile2 = buildAttachable(-300, -50, false, 22*TILE_SIZE, TILE_SIZE, player);
        GameObject exit = buildExit(500, -110, true, player, true);

        turn.addTurnables(longTile2, exit);

        levelInfo.reset("Exit can fall onto another revolving object too",
                "Up And Down And Round We Go...");

    }
}
