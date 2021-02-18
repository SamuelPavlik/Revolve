package MovePckg;

import CameraPckg.Camera;
import GameObjects.GameObject;
import processing.core.PApplet;

public class MovePlayer extends Move {
    private Camera camera;

    public MovePlayer(PApplet pApplet, GameObject gameObject, Camera camera) {
        super(pApplet, gameObject);
        this.camera = camera;
    }

    @Override
    public void update() {
        if(pApplet.keyPressed){
//            if (gameObj().collider.isLowerEdge() || gameObj().collider.isUpperEdge()) {
//            }
            if(pApplet.key == 'd'){
                if (gameObj().velocity.x <= maxSpeed) {
                    gameObj().velocity.x += maxAccel;
                }
            }
            else if(pApplet.key == 'a'){
                if (gameObj().velocity.x >= -maxSpeed) {
                    gameObj().velocity.x -= maxAccel;
                }
            }

        }
    }
}
