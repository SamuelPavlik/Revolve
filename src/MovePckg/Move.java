package MovePckg;

import GameObjects.Component;
import GameObjects.GameObject;
import processing.core.PApplet;
import processing.core.PVector;

// A representation of a kinematic character
public abstract class Move extends Component{
    private final float SLOW_RADIUS = 20f ;
    final float TARGET_RADIUS = 3f ;
    private final float DRAG = 0.95f ;
    private final float HEADING_DELTA = 0.1f;
    static final float maxSpeed = 4f ;
    static final float maxAccel = 0.3f ;

    public Move(PApplet pApplet, GameObject gameObject) {
        super(pApplet, gameObject);
    }

    @Override
    public void update() {
    }
}
