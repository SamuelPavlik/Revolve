package Behavior;

import Colliders.BoxCollider;
import Colliders.Collider;
import GameObjects.Component;
import GameObjects.GameObject;
import Renderers.Renderer;
import processing.core.PApplet;
import processing.core.PVector;

public class Turnable extends Component {
    public static final float TURN_TIME = 10;
    public static final String TAG = "Turnable";
    private static final float MIN = 10;

    boolean moving;
    boolean turning;
    boolean transparent;

    private boolean gravity;
    private GameObject player;
    private PVector positionDiff;
    private PVector targetPosition;
    private int turnDir = 0;
    private Counter turnNotifier;
    private Counter moveNotifier;
    private boolean counterSent = false;
    private float turnSpeed;
    private Renderer renderer;

    BoxCollider collider;

    public Turnable(PApplet pApplet, GameObject gameObject, GameObject player) {
        super(pApplet, gameObject);
        gameObject.tag = TAG;
        this.player = player;
        this.transparent = false;
        this.moving = false;
        this.collider = (BoxCollider) gameObject.collider;
        this.gravity = gameObject.gravity;
        setPositionDiff();
        renderer = (Renderer) gameObject.getComponent(Renderer.class);
    }

    @Override
    public void update() {
        if (turning) {
            //keep turning if not turned by 180 degrees yet
            executeTurning();
        }
        else if (moving){
            //keep moving if other turnables not out of collision yet
            move();
        }
        else if (moveNotifier != null && isInCollision()) {
            //notify player if in collision at the start of level
            moveNotifier.inCollision = true;
        }

        //if player inside it means he/she is falling through
        onPlayerInside();
    }

    /**
     * start the turning process
     */
    public void turn() {
        transparent = true;
        turning = true;
        counterSent = false;
        gameObj().collider.setTrigger(true);

//        if (!moving)
//            turnOffIfGravity();

        //set position difference in relation to player
        setPositionDiff();
        //set target position to reach
        targetPosition = new PVector(player.position.x - positionDiff.x, gameObj().position.y);

        if (targetPosition.x > gameObj().position.x)
            turnDir = 1;
        else
            turnDir = -1;

        turnSpeed = Math.abs(targetPosition.x - gameObj().position.x) / TURN_TIME;
    }

    /**
     * //keep turning if not turned by 180 degrees yet, else finish the turning process and notify the player object
     */
    private void executeTurning() {
        if (Math.abs(targetPosition.x - gameObj().position.x) <= turnSpeed) {
            gameObj().position = targetPosition.copy();
            turning = false;
            setPositionDiff();
            turnNotifier.done();
//            if (!moving)
//                turnOffIfGravity();
        }
        else {
            gameObj().position.x += turnSpeed * turnDir;
        }
    }

    /**
     * keep moving if other turnables not out of collision yet, else notify the player object
     */
    private void move() {
        if (moving && !turning) {
            if (isInCollision()) {
                //if reached collision again notify the player object
                if (counterSent) {
                    moveNotifier.undone();
                    counterSent = false;
                }
            }
            else {
                if (!counterSent) {
                    moveNotifier.done();
                    counterSent = true;
                }
            }
            gameObj().position.x = player.position.x + positionDiff.x;
        }
    }

    public void setMoving(boolean val) {
        this.moving = val;
        counterSent = false;
        transparent = val;
        if (!val)
            gameObj().gravity = gravity;

    }

    public void setTurnNotifier(Counter turnNotifier) {
        this.turnNotifier = turnNotifier;
    }

    public void setMoveNotifier(Counter moveNotifier) {
        this.moveNotifier = moveNotifier;
    }

    public void setPositionDiff() {
        this.positionDiff = new PVector(gameObj().position.x - player.position.x,
                gameObj().position.y/* - player.position.y*/);
    }

    public void setTransparent(boolean transparent) {
        renderer.setTransparent(transparent);
    }

    private void turnOffIfGravity() {
        this.gravity = gameObj().gravity;
        gameObj().gravity = false;
    }

    public void toggleGravity() {
        gameObj().gravity = gravity;
    }

    private boolean isInCollision() {
        Collider objCol = gameObj().collider;
        if (objCol.isCollided()) {
            for (GameObject obj : objCol.getTouched()) {
                if (obj.velocity.y == 0 && gameObj().velocity.y == 0) {
                    if ((obj.tag.equals("Collidable") || obj.tag.equals("Turnable"))
                            && (gameObj().collider.getSurfInCollision() > Collider.MIN)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    private void onPlayerInside() {
        Collider objCol = gameObj().collider;
        if (objCol.isCollided()) {
            for (GameObject obj : objCol.getTouched()) {
                if (obj.tag.equals("Player") && obj.collider.getColY() + ((BoxCollider) obj.collider).getHeight() - MIN > gameObj().collider.getColY())
                    objCol.setTrigger(true);
            }
        }
    }

    public PVector getTargetPos() {
        return targetPosition;
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean isTurning() {
        return turning;
    }
}
