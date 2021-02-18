package Behavior;

import GameObjects.Component;
import GameObjects.GameObject;
import processing.core.PApplet;

public class Temporary extends Component {
    private int time;

    public Temporary(PApplet pApplet, GameObject gameObject, int time) {
        super(pApplet, gameObject);
        this.time = time;
        gameObject.tag = "Temporary";
    }

    @Override
    public void update() {
        //destroy object after given number of frames passed
        if (time-- == 0) gameObj().destroy();
    }
}
