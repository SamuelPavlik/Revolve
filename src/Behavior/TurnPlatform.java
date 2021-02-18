package Behavior;

import Colliders.BoxCollider;
import GameObjects.Component;
import GameObjects.GameObject;
import Levels.LevelFactory;
import Renderers.RendererComposite;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TurnPlatform extends Component {
    private static final float HEIGHT = 20;
    private static final float FROM_SIDES = 20;
    private static final float TURN_TIME = Turnable.TURN_TIME;
    private static final int FILL = 200;
    private static final int ABOVE_PLAYER = 200;
    public static final String TAG = "TurnPlatform";

    private List<GameObject> turnables;
    private GameObject player;
    private BoxCollider collider;
    private int turnDir = 0;
    private float turnSpeed;
    private boolean turning = false;
    private boolean moving = false;
    private GameObject corner1Obj;
    private GameObject corner2Obj;
    private PVector positionDiff;
    private PVector targetPosition;

    public TurnPlatform(PApplet pApplet, GameObject gameObject, GameObject player) {
        super(pApplet, gameObject);
        this.player = player;
        this.turnables = new ArrayList<>();
        gameObject.position.x = player.position.x;
        gameObject.position.y = 100;
        this.collider = new BoxCollider(pApplet, gameObject, 2*FROM_SIDES, HEIGHT, true);
        collider.setFill(FILL);
        this.targetPosition = new PVector();
        gameObject.addComponent(collider);
        gameObject.tag = TAG;
    }

    @Override
    public void update() {
        if (turning) executeTurning();
        else if (moving) move();
        setLength();
    }

    public void turn() {
        turning = true;
        setPositionDiff();

        float x1;
        if (corner1Obj.tag.equals("Player")) {
            x1 = corner1Obj.position.x;
        } else {
            x1 = ((Turnable) corner1Obj.getComponent(Turnable.class)).getTargetPos().x;
        }
        float x2;
        if (corner2Obj.tag.equals("Player")) {
            x2 = corner2Obj.position.x;
        } else {
            x2 = ((Turnable) corner2Obj.getComponent(Turnable.class)).getTargetPos().x;
        }

        targetPosition = new PVector((x1 + x2)/2, gameObj().position.y);

        if (targetPosition.x > gameObj().position.x)
            turnDir = 1;
        else
            turnDir = -1;

        turnSpeed = Math.abs(targetPosition.x - gameObj().position.x) / TURN_TIME;
    }

    private void executeTurning() {
        if (Math.abs(targetPosition.x - gameObj().position.x) <= turnSpeed) {
            gameObj().position = targetPosition.copy();
            turning = false;
            moving = true;
            setPositionDiff();
        }
        else {
            gameObj().position.x += turnSpeed * turnDir;
        }
    }

    private void move() {
        if (moving && !turning) {
            if (positionDiff != null) {
                gameObj().position.x = player.position.x + positionDiff.x;
            } else {
                setPositionDiff();
                gameObj().position.x = player.position.x + positionDiff.x;
            }
        }
    }

    public void addTurnables(GameObject... turnables) {
        this.turnables.addAll(Arrays.asList(turnables));
        setLength();
   }

    public void removeTurnable(GameObject turnable) {
        this.turnables.remove(turnable);
        setLength();
    }

    public void setTurning(boolean turn) {
        this.turning = turn;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public void reset() {
        this.turnables = new ArrayList<>();
        gameObj().position.x = player.position.x;
        gameObj().position.y = player.position.y - 200;
        this.collider = new BoxCollider(pApplet, gameObj(), 2*FROM_SIDES, HEIGHT);
        this.targetPosition = new PVector();
        this.turning = false;
    }

    public boolean isTurning() {
        return turning;
    }

    public boolean isMoving() {
        return moving;
    }

    private void setLength() {
        corner1Obj = player;
        corner2Obj = player;
        for (GameObject obj : turnables) {
            if (collider.getWidth() >= 0) {
                if (obj.position.x < corner1Obj.position.x)
                    corner1Obj = obj;
                if (obj.position.x > corner2Obj.position.x)
                    corner2Obj = obj;

            } else {
                if (obj.position.x > corner1Obj.position.x)
                    corner1Obj = obj;
                if (obj.position.x < corner2Obj.position.x)
                    corner2Obj = obj;
            }

        }

        if (corner2Obj.position.x > corner1Obj.position.x)
            collider.setWidth((corner2Obj.position.x - corner1Obj.position.x) + 2*FROM_SIDES);
        else
            collider.setWidth((corner2Obj.position.x - corner1Obj.position.x) - 2*FROM_SIDES);
        gameObj().position.x = corner1Obj.position.x + (corner2Obj.position.x - corner1Obj.position.x)/2;
    }

    private void updateLength() {
        if (corner2Obj.position.x > corner1Obj.position.x) {
            collider.setWidth((corner2Obj.position.x - corner1Obj.position.x) + 2*FROM_SIDES);
        }
        else {
            collider.setWidth((corner2Obj.position.x - corner1Obj.position.x) - 2*FROM_SIDES);
        }
//        if (corner1Obj.tag.equals("Player") || corner2Obj.tag.equals("Player")) {
//            gameObj().position.x = corner1Obj.position.x + collider.getWidth()/2;
//        }
        gameObj().position.x = corner1Obj.position.x + (corner2Obj.position.x - corner1Obj.position.x)/2;
    }

    private void setPositionDiff() {
        this.positionDiff = new PVector(gameObj().position.x - player.position.x,
                gameObj().position.y/* - player.position.y*/);
    }
}