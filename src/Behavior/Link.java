package Behavior;

import Colliders.BoxCollider;
import Colliders.CircleCollider;
import GameObjects.Component;
import GameObjects.GameObject;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Link extends Component {
    public static final String TAG = "Link";

    private static final float MIN_LINK_DIST = 40;
    private static final float MAX = 1;
    private static final float RES = 0.04f;
    private GameObject platform;
    private GameObject attachedTo1;
    private GameObject attachedTo2;
    private float xDiff;
    private boolean hook = false;

    public Link(PApplet pApplet, GameObject gameObject, GameObject attachedTo1, GameObject attachedTo2, int fill) {
        super(pApplet, gameObject);
        this.attachedTo1 = attachedTo1;
        this.attachedTo2 = attachedTo2;
        this.platform = GameObject.get(TurnPlatform.TAG);
        this.xDiff = attachedTo2.position.x - platform.collider.getColX();

        gameObject.position.x = attachedTo1.position.x;
        gameObject.position.y = attachedTo1.position.y;
        gameObject.addComponent(new CircleCollider(pApplet, gameObject, 10, true));
        gameObject.tag = Link.TAG;
        gameObject.collider.setFill(fill);
    }

    public Link(PApplet pApplet, GameObject gameObject, GameObject attachedTo1, GameObject attachedTo2, int fill, boolean hook) {
        this(pApplet, gameObject, attachedTo1, attachedTo2, fill);
        this.hook = hook;
    }

    @Override
    public void update() {
        resolve();
    }

    /**
     * resolve object's velocity in relation to the attached objects
     */
    private void resolve() {
        if (((BoxCollider) platform.collider).getWidth() < 0 && xDiff > 0) {
            xDiff *= -1;
        }
        else if (((BoxCollider) platform.collider).getWidth() > 0 && xDiff < 0) {
            xDiff *= -1;
        }

        PVector current = new PVector(0, 0);
        //add spring of the first object
        if (attachedTo1 != null) {
            current.add(attachedTo1.position.copy().sub(gameObj().position));
        }
        //add spring of the second object or attach to the revolving platform
        if (attachedTo2 != null) {
            if (hook) {
                current.add(new PVector(attachedTo2.position.x, platform.collider.getColY()).sub(gameObj().position));
            }
            else {
                current.add(attachedTo2.position.copy().sub(gameObj().position));
            }
        }

        if (current.mag() > MAX) {
            gameObj().velocity.add(current.mult(RES));
        }
    }

    /**
     * generate rope of links from given revolving object to platform
     * @param pApplet PApplet object
     * @param dynObj revolving object
     * @param fill fill of the links
     * @return list of links represented as game objects
     */
    public static List<GameObject> generateRope(PApplet pApplet, GameObject dynObj, int fill) {
        List<GameObject> links = new ArrayList<>();
        GameObject link;
        GameObject platform = GameObject.get(TurnPlatform.TAG);
        int numOfLinks = (int) ((dynObj.position.y - platform.position.y) / MIN_LINK_DIST);
        //generate link objects
        for (int i = 0; i < numOfLinks; i++) {
            link = new GameObject(pApplet, 0, 0, false);
            links.add(link);
        }
        //attach links to each other by Link component
        for (int i = 0; i < numOfLinks; i++) {
            if (i == 0) {
                links.get(i).addComponent(new Link(pApplet, links.get(i), links.get(i + 1), dynObj, fill, true));
            }
            else if (i == numOfLinks - 1) {
                links.get(i).addComponent(new Link(pApplet, links.get(i), links.get(i - 1), dynObj, fill));
            }
            else {
                links.get(i).addComponent(new Link(pApplet, links.get(i), links.get(i - 1), links.get(i + 1), fill));
            }
        }

        return links;
    }
}
