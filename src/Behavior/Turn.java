package Behavior;

import GameObjects.Component;
import GameObjects.GameObject;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Turn extends Component {
    private static final int AT_FILL = 200;
    private static final int T_FILL = 100;

    private List<Turnable> turnableObjects;
    private HashMap<GameObject, List<GameObject>> dynToLinksMap;
    private boolean turning = false;
    private boolean moving = false;
    private boolean transparent = false;
    private Counter turnNotifier;
    private Counter moveNotifier;
    private TurnPlatform turnPlatform;

    public Turn(PApplet pApplet, GameObject gameObject) {
        super(pApplet, gameObject);
        this.turnableObjects = new ArrayList<>();
        this.dynToLinksMap = new HashMap<>();
        this.turnNotifier = new Counter();
        this.moveNotifier = new Counter();

        GameObject platform = new GameObject(pApplet, 0, 0);
        this.turnPlatform = new TurnPlatform(pApplet, platform, gameObject);
        platform.addComponent(turnPlatform);
    }

    @Override
    public void update() {
        handleMovement();
    }

    /**
     * start turning process of all revolving objects
     */
    private void turnAllObjects() {
        turning = true;
        transparent = true;
        moving = false;
        turnPlatform.setTurning(true);
        //reset the notifiers to wait for new notifications
        turnNotifier.reset();
        moveNotifier.reset();
        for (int i = 0; i < turnableObjects.size(); i++) {
            turnableObjects.get(i).turn();
            turnableObjects.get(i).setTransparent(true);
        }

        turnPlatform.turn();
        gameObj().velocity.x = 0;
        gameObj().facing = -gameObj().facing;
    }

    /**
     * toggle moving process of all revolving objects
     * @param move true when objects are moving with player, false otherwise
     */
    private void setMoveAll(boolean move) {
        transparent = move;
        moving = move;
        turnPlatform.setMoving(move);
        moveNotifier.reset();
        for (int i = 0; i < turnableObjects.size(); i++) {
            turnableObjects.get(i).setMoving(move);
            turnableObjects.get(i).setTransparent(move);
            if (turnableObjects.get(i).collider != null) {
                turnableObjects.get(i).collider.setTrigger(move);
            }
        }
    }

    /**
     * handle turning and moving of revolving objects
     */
    private void handleMovement() {
        //in case it's not turning
        if (!turning && !turnPlatform.isTurning()) {
            if (pApplet.keyPressed && pApplet.key == ' ') {
                turnAllObjects();
            }
        }
        //in case it's done with turning
        else if (turning) {
            if (doneTurning()) {
                turning = false;
                turnNotifier.reset();
                setMoveAll(true);
            }
        }
        //in case it's done with turning and is moving
        if (moving) {
            if (doneMoving()) {
                setMoveAll(false);
            }
        }
        //in case it's in collision at the start
        else if (moveNotifier.inCollision) {
            moveNotifier.inCollision = false;
            setMoveAll(true);
        }
    }

    /**
     * add variable number of objects as revolving objects
     * @param turnables game objects to be set turnable
     */
    public void addTurnables(GameObject... turnables) {
        Turnable turnable;
        int turnSize = turnableObjects.size();
        for (GameObject obj : turnables) {
            if ((turnable = (Turnable) obj.getComponent(Turnable.class)) == null) {
                //if no Turnable behaviour present, create one
                turnable = new Turnable(pApplet, obj, gameObj());
                obj.addComponent(turnable);
            }
            if (turnSize != 0) {
                //set the object to properties of other currently revolving objects
                turnable.moving = turnableObjects.get(0).moving;
                turnable.transparent = turnableObjects.get(0).transparent;
                turnable.setPositionDiff();
                obj.collider.setTrigger(turnable.moving);
            }

            turnable.setTurnNotifier(turnNotifier);
            turnable.setMoveNotifier(moveNotifier);
            if (turnable instanceof Attachable) {
                //save the rope of attachable objects to destroy it later
                dynToLinksMap.put(obj, Link.generateRope(pApplet, obj, AT_FILL));
            } else {
                Link.generateRope(pApplet, obj, T_FILL);
            }
            turnableObjects.add(turnable);
        }

        turnPlatform.addTurnables(turnables);
    }

    /**
     * remove a turnable object from the list to not turn around player anymore
     * @param turnable
     */
    public void removeTurnable(GameObject turnable) {
        List<GameObject> links = dynToLinksMap.get(turnable);
        for (int i = 0; i < links.size(); i++) {
            links.get(i).destroy();
        }
        dynToLinksMap.remove(turnable);
        turnableObjects.remove(turnable.getComponent(Turnable.class));
        turnPlatform.removeTurnable(turnable);
        reset();
    }

    /**
     * reset the behaviour after an object has been detached
     */
    public void reset() {
        turning = false;
        moving = false;
        transparent = false;
        turnNotifier.reset();
        moveNotifier.reset();
    }

    /**
     * @return true if all the objects are out of collision, false otherwise
     */
    private boolean doneMoving() {
        return moveNotifier.getValue() == turnableObjects.size();
    }

    /**
     * @return true if all the objects have turned by 180 degrees, false otherwise
     */
    private boolean doneTurning() {
        return (turnNotifier.getValue() == turnableObjects.size()) && !turnPlatform.isTurning();
    }

    public void setTurning(boolean turning) {
        this.turning = turning;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }
}