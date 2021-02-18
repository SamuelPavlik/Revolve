package CameraPckg;

import GameObjects.GameObject;
import PCGPckg.Room;
import processing.core.PApplet;
import processing.core.PVector;

public class Camera {
    private static final float MOVE_WINDOW = 400;

    private PApplet pApplet;
    private PVector currPos;
    private GameObject player;

    public Camera(PApplet pApplet, GameObject player) {
        this.pApplet = pApplet;
        this.player = player;
        this.currPos = new PVector(pApplet.width/2, pApplet.height/2);
    }

    /**
     * update camere position to look at the room where player is
     */
    public void update() {
        if (player != null) {
            setCurrPos();
            pApplet.translate(pApplet.width/2 - currPos.x/* - positionDiff.x*/,pApplet.height/2 - currPos.y/* - positionDiff.y*/);
        }
        else pApplet.translate(0, 0);
    }

    public PVector getCurrPos() {
        return currPos;
    }

    public void setPlayer(GameObject player) {
        this.player = player;
        this.currPos = new PVector(pApplet.width/2, pApplet.height/2);
    }

    public void setCurrPos() {
        if (player.position.x - currPos.x > MOVE_WINDOW) currPos.x = player.position.x - MOVE_WINDOW;
        if (currPos.x - player.position.x > MOVE_WINDOW) currPos.x = player.position.x + MOVE_WINDOW;
        if (player.position.y - currPos.y > MOVE_WINDOW) currPos.y = player.position.y - MOVE_WINDOW;
        if (currPos.y - player.position.y > MOVE_WINDOW) currPos.y = player.position.y + MOVE_WINDOW;
    }
}
