package GameObjects;

import processing.core.PApplet;

public abstract class Component {
    public PApplet pApplet;
    public boolean enabled = true;

    GameObject gameObject;

    public Component(PApplet pApplet) {
        this.pApplet = pApplet;
    }

    public Component(PApplet pApplet, GameObject gameObject) {
        this.pApplet = pApplet;
        this.gameObject = gameObject;
        if (gameObject != null) {
            gameObject.addComponent(this);
        }
    }

    public abstract void update();

    public GameObject gameObj() {
        return gameObject;
    }
}
