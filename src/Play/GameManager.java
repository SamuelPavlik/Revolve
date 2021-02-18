package Play;

import CameraPckg.Camera;
import Colliders.BoxCollider;
import Colliders.CircleCollider;
import Colliders.Collider;
import GameObjects.GameObject;
import Levels.LevelFactory;
import UIPckg.GameUI;
import UIPckg.UI;
import processing.core.PApplet;

import java.util.List;

public class GameManager extends PApplet{
    private static final int WIDTH = 1500;
    private static final int HEIGHT = 1000;
    private static GameManager instance = null;

    private GameObject player;
    private LevelFactory levelFactory;
    private Camera camera;
    private UI gameUI;

    public void setup(){
        instance = this;
        camera = new Camera(this, null);
        levelFactory = new LevelFactory(this, camera);
//        gameUI = new GameUI(this, player, camera);
        GameObject.xCentre = WIDTH/2;
        GameObject.yCentre = HEIGHT/2;
        nextLevel();
    }

    public void settings(){
        size(WIDTH, HEIGHT);
    }

    public void draw(){
        //Draw scene
        background(41, 184, 255) ;

        //update camera view
        camera.update();

        //Update all game objects
        GameObject.updateAll();

//        drawColliderBounds();

        //Draw UI
        gameUI.update();
    }

    public static void main(String[] args){
        String[] processingArgs = {"Tanks"};
        GameManager gameController = new GameManager();
        PApplet.runSketch(processingArgs, gameController);
    }

    /**
     * draws collider bounds of all colliders in the game
     */
    private void drawColliderBounds(){
//        fill(200);
        noFill();
        stroke(100);
        List<Collider> colliders = Collider.getColliders();
        for (Collider c: colliders) {
            if (c.enabled) {
                if (c instanceof BoxCollider){
                    BoxCollider bc = (BoxCollider) c;
                    rect(bc.getColX(), bc.getColY(), bc.getWidth(), bc.getHeight());
                }
                else {
                    CircleCollider bc = (CircleCollider) c;
                    ellipse(bc.getColX(), bc.getColY(), 2*bc.getRadius(), 2*bc.getRadius());
                }
            }
        }
    }

    public static GameManager getInstance() {
        if (instance == null)
            instance = new GameManager();
        return instance;
    }

    /**
     * restarts the game and resets the difficulty
     */
    public void restartLevel(){
        levelFactory.decreaseLevel();
        nextLevel();
    }

    public int getLevel() {
        return levelFactory.getLevel();
    }

    public void setLevel(int index) {
        levelFactory.setLevel(index);
    }

    /**
     * resets the rooms, items and enemies placement and the exit placement
     */
    public void nextLevel(){
//        GameObject.destroyAllExcept(player.tag, TurnPlatform.TAG);
        GameObject.destroyAll();
        player = levelFactory.getNextLevel();
        camera.setPlayer(player);
        gameUI = new GameUI(this, camera, levelFactory);

//        System.out.println("In exit");
    }
}