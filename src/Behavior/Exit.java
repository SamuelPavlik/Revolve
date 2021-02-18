package Behavior;

import Colliders.BoxCollider;
import Colliders.Collider;
import GameObjects.Component;
import GameObjects.GameObject;
import Levels.LevelFactory;
import Play.GameManager;
import processing.core.PApplet;

/**
 * When the object's collider is reached set next level
 */
public class Exit extends Component {
    public static final float WIDTH = 50;
    public static final float HEIGHT = 100;

    private Collider collider;
    private Turnable turnable;

    public Exit(PApplet pApplet, GameObject gameObject) {
        super(pApplet, gameObject);
        this.collider = gameObject.collider;
        this.turnable = (Turnable) gameObject.getComponent(Turnable.class);
        gameObj().addComponent(collider);
        gameObject.tag = "Exit";
    }

    @Override
    public void update() {
        checkExit();
    }

    /**
     * sets the next level if the exit is reached
     */
    private void checkExit() {
        if (turnable != null && (turnable.isMoving() || turnable.isTurning()))
            return;
        if (collider.isCollided()){
            for (int i = 0; i < collider.getTouched().size(); i++) {
                GameObject obj = collider.getTouched().get(i);
                if (obj.tag.equals("Player")){
                    if (GameManager.getInstance().getLevel() == LevelFactory.MAX_LEVELS) {
                        //if last level, go back to menu
                        GameManager.getInstance().setLevel(-1);
                        GameManager.getInstance().restartLevel();
                    }
                    else {
                        GameManager.getInstance().nextLevel();
                    }
                }
            }
        }
    }
}
