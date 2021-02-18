package Behavior;

import Colliders.Collider;
import GameObjects.Component;
import GameObjects.GameObject;
import Renderers.Renderer;
import Renderers.RendererComposite;
import processing.core.PApplet;

/**
 * generate copies of given object infinitely long and destroy each copy after a given time
 */
public class ObjectGenerator extends Component {
    private static final int DELAY = 200;

    private GameObject toGen;
    private int genTime;
    private int curTime;

    public ObjectGenerator(PApplet pApplet, GameObject gameObject, GameObject toGen, int genTime) {
        super(pApplet, gameObject);
        this.toGen = toGen;
        gameObject.tag = "Generator";
        toGen.position = gameObject.position.copy();
        this.genTime = genTime;
        this.curTime = 0;
        toGen.addComponent(new Temporary(pApplet, toGen, genTime + DELAY));
    }

    @Override
    public void update() {
        curTime++;
        if (curTime == genTime) {
            //create simple copy of the object with collider and renderer
            GameObject newObj = new GameObject(pApplet, gameObj().position.x, gameObj().position.y, toGen.gravity);
            newObj.addComponent(((Collider) toGen.getComponent(Collider.class)).copy(newObj));
            newObj.addComponent(((Renderer) toGen.getComponent(RendererComposite.class)).copy(newObj));
            newObj.addComponent(new Temporary(pApplet, newObj, genTime + DELAY));

            toGen = newObj;
            toGen.position = gameObj().position.copy();
            curTime = 0;
        }
    }
}
