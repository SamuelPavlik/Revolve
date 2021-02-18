package Behavior;

import Colliders.BoxCollider;
import GameObjects.Component;
import GameObjects.GameObject;
import Other.Shapes;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.List;

/**
 * add given velocity to all dynamic objects in its area
 */
public class AreaEffector extends Component {
    private static final float SIZE = 100;
    private static final float BHS = 10;
    private static final float EHS = 1;

    private PVector velocity;
    private BoxCollider collider;

    public AreaEffector(PApplet pApplet, GameObject gameObject, PVector velocity, float width, float height) {
        super(pApplet, gameObject);
        this.velocity = velocity;
        this.collider = new BoxCollider(pApplet, gameObject, width, height, true);
        gameObject.addComponent(collider);
    }

    @Override
    public void update() {
        //add velocity to all objects in area
        if (collider.isCollided()) {
            List<GameObject> touched = collider.getTouched();
            for (GameObject t : touched) {
                if (t.gravity)
                    t.velocity.add(velocity);
            }
        }

        //draw horizontal arrows
        if (velocity.x != 0) {
            float diff = collider.getHeight() / 4;
            float halfSize = (velocity.x*collider.getWidth())/4;

            Shapes.drawArrow(pApplet,
                                collider.getCentreColX() + halfSize,
                            collider.getColY() + diff,
                            collider.getCentreColX() - halfSize,
                            collider.getColY() + diff,
                                BHS, EHS, false);
            Shapes.drawArrow(pApplet,
                    collider.getCentreColX() + halfSize,
                    collider.getColY() + 2*diff,
                    collider.getCentreColX() - halfSize,
                    collider.getColY() + 2*diff,
                    BHS, EHS, false);
            Shapes.drawArrow(pApplet,
                    collider.getCentreColX() + halfSize,
                    collider.getColY() + 3*diff,
                    collider.getCentreColX() - halfSize,
                    collider.getColY() + 3*diff,
                    BHS, EHS, false);
        }
        //draw vertical arrows
        else if (velocity.y != 0) {
            float diff = collider.getWidth() / 4;
            float halfSize = (velocity.y*collider.getHeight())/4;

            Shapes.drawArrow(pApplet,
                    collider.getColX() + diff,
                    collider.getCentreColY() + halfSize,
                    collider.getColX() + diff,
                    collider.getCentreColY() - halfSize,
                    BHS, EHS, false);
            Shapes.drawArrow(pApplet,
                    collider.getColX() + 2*diff,
                    collider.getCentreColY() + halfSize,
                    collider.getColX() + 2*diff,
                    collider.getCentreColY() - halfSize,
                    BHS, EHS, false);
            Shapes.drawArrow(pApplet,
                    collider.getColX() + 3*diff,
                    collider.getCentreColY() + halfSize,
                    collider.getColX() + 3*diff,
                    collider.getCentreColY() - halfSize,
                    BHS, EHS, false);

        }

        collider.setFill(-1);
    }
}
