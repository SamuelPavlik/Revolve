package Renderers;

import Colliders.Collider;
import GameObjects.Component;
import GameObjects.GameObject;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public abstract class Renderer extends Component {
    PVector positionDiff;
    Collider collider;
    boolean transparent;
    PImage img;

    public Renderer(PApplet pApplet, PVector positionDiff) {
        super(pApplet);
        this.positionDiff = positionDiff;
    }

    public Renderer(PApplet pApplet, GameObject gameObject) {
        super(pApplet, gameObject);
        this.collider = gameObject.collider;
    }

    public abstract Renderer copy(GameObject gameObject);

    @Override
    public abstract void update();

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    public PImage getImg() {
        return img;
    }
}
