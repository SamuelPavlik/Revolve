package Colliders;

import GameObjects.Component;
import GameObjects.GameObject;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Collider extends Component {
    static List<Collider> colliders = new ArrayList<>();
    static final int NO_FILL = -1;
    private static final float BOUNCE_FACTOR = 0.03f;
    private static final float SIDE_BOUNCE_FACTOR = 1;
    private static final float MIN_BOUNCE = 1;
    public static final float MIN = 3;

    PVector positionDiff;
    List<GameObject> touched;
    boolean collided;
    boolean lowerEdge = false;
    boolean upperEdge = false;
    boolean trigger;
    float surfInCollision = 0;
    float colX;
    float colY;
    float centreColX;
    float centreColY;
    List<PVector> touchVectors;
    int fill = NO_FILL;
    boolean toFill = false;

    public abstract Collider copy(GameObject gameObject);

    public Collider(PApplet pApplet, GameObject gameObject) {
        super(pApplet, gameObject);
        colliders.add(this);
    }

    Collider(GameObject gameObject, float colX, float colY) {
        super(null);
        this.colX = colX;
        this.colY = colY;
        this.positionDiff = new PVector(gameObject.position.x - colX, gameObject.position.y - colY);
        this.collided = false;
        colliders.add(this);
    }

    Collider(float colX, float colY) {
        super(null);
        this.colX = colX;
        this.colY = colY;
        this.positionDiff = new PVector(0, 0);
        this.collided = false;
        colliders.add(this);
    }

    /**
     * checks if collider collides with any other colliders
     * @return true if in collision with another object
     */
    public abstract boolean checkCollision();

    /**
     * updates collider based on object's velocity and collision with other objects
     */
    public void update(){
        if (gameObj() != null) {
            updatePosition();
        }
        PVector resultVector = new PVector(0, 0);
        if (checkCollision()) {
            lowerEdge = false;
            if (!trigger) {
                for (PVector tv : touchVectors) {
                    //in case of touching from lower edge
                    if (tv.y < 0 && gameObj().velocity.y > 0) {
                        gameObj().velocity.y = -gameObj().velocity.y * BOUNCE_FACTOR;
                        lowerEdge = true;
                    }
                    if ((tv.y > 0 && gameObj().velocity.y < 0)) {
                        gameObj().velocity.y = -gameObj().velocity.y * BOUNCE_FACTOR;
                        upperEdge = true;
                    }
                    //in case of touching from sides
                    if ((gameObj().velocity.x > 0 && tv.x < 0) || (gameObj().velocity.x < 0 && tv.x > 0))
                        gameObj().velocity.x = -gameObj().velocity.x * SIDE_BOUNCE_FACTOR;
                }
                gameObj().velocity.add(resultVector);
            }
        }
        if (toFill) {
            if (fill != NO_FILL) {
                pApplet.fill(fill);
            }
            else {
                pApplet.noFill();
            }
            drawShape();
            pApplet.noFill();
        }
    }

    protected abstract void drawShape();

    /**
     * update position of the collider so that it's in the same distance as when
     * initialized
     */
    private void updatePosition(){
        setCentre();
    }

    public void destroy(){
        colliders.remove(this);
    }

    public boolean isCollided() {
        return collided;
    }

    public boolean isLowerEdge() {
        return lowerEdge;
    }

    public boolean isUpperEdge() {
        return upperEdge;
    }

    public void setTrigger(boolean trigger) {
//        if (trigger && enabled)
//            enabled = false;
//        else if (!trigger && !enabled)
//            enabled = true;
        this.trigger = trigger;
    }

    public void setFill(int fill) {
        this.fill = fill;
        this.toFill = true;
    }

    public void setCentreColX(float centreColX) {
        this.centreColX = centreColX;
    }

    public float getColX() {
        return colX;
    }

    public float getColY() {
        return colY;
    }

    public float getCentreColX() {
        return centreColX;
    }

    public float getCentreColY() {
        return centreColY;
    }

    public void setColX(float colX) {
        this.colX = colX;
    }

    public void setColY(float colY) {
        this.colY = colY;
    }

    public float getSurfInCollision() {
        return surfInCollision;
    }

    public void setCentre() {
        this.centreColX = gameObj().position.x;
        this.centreColY = gameObj().position.y;
        this.colX = this.centreColX;
        this.colY = this.centreColY;
    }

    public List<GameObject> getTouched() {
        return touched;
    }

    public static void destroyAll(){
        colliders = new ArrayList<>();
    }

    public static void destroyThese(List<? extends Collider> cols){
        for (int i = 0; i < cols.size(); i++) {
            colliders.remove(cols.get(i));
        }
    }

    public static List<Collider> getColliders() {
        return colliders;
    }

    public static void destroyAllExcept(String tag) {
        List<Collider> except = new ArrayList<>();
        for (int i = 0; i < colliders.size(); i++) {
            if (colliders.get(i).gameObj() != null) {
                if (colliders.get(i).gameObj().tag.equalsIgnoreCase(tag)) {
                    except.add(colliders.get(i));
                }
            }
        }
        colliders = new ArrayList<>();
        colliders.addAll(except);
    }

    public static void destroyAllExcept(String... tags) {
        List<String> list = Arrays.asList(tags);
        List<Collider> except = new ArrayList<>();
        for (int i = 0; i < colliders.size(); i++) {
            if (colliders.get(i).gameObj() != null) {
                if (list.contains(colliders.get(i).gameObj().tag)) {
                    except.add(colliders.get(i));
                }
            }
        }
        colliders = new ArrayList<>();
        colliders.addAll(except);
    }

    public void resetPositionDiff() {
        this.positionDiff = new PVector(gameObj().position.x - colX, gameObj().position.y - colY);
    }
}
