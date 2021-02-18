package Behavior;

import CameraPckg.Camera;
import GameObjects.GameObject;
import Renderers.Renderer;
import processing.core.PApplet;
import processing.core.PVector;

public class Attachable extends Turnable {
    private Camera camera;
    private Turn turn;
    private boolean attached;
    private boolean pressed;
    private boolean usesGravity;
    private Renderer renderer;

    public Attachable(PApplet pApplet, GameObject gameObject, GameObject player, Camera camera) {
        super(pApplet, gameObject, player);
        this.camera = camera;
        this.turn = (Turn) player.getComponent(Turn.class);
        this.renderer = (Renderer) gameObject.getComponent(Renderer.class);
        this.attached = true;
        this.usesGravity = gameObject.gravity;
        gameObject.gravity = false;
    }

    @Override
    public void update() {
        if (pressed && !pApplet.mousePressed) pressed = false;

        if (attached) {
            //if attached treat like any other revolving object
            if (!turning && !moving) {
                renderer.setTransparent(false);
                onMouseOver();
            }
            super.update();
        }
        else {
            //if not attached treat like any non-revolving object
            //object is set to transparent when mouse is over and attached if clicked
            if (!transparent) renderer.setTransparent(false);
            onMouseOver();
        }
    }

    /**
     * set to transparent when mouse is over and toggle attachment when mouse is clicked
     */
    private void onMouseOver() {
        PVector camPos = camera.getCurrPos();

        if (!pressed && collider != null) {
            if ((collider.getColY() + (pApplet.height / 2 - camPos.y) < pApplet.mouseY)
                    && (collider.getColY() + collider.getHeight()  + (pApplet.height / 2 - camPos.y) > pApplet.mouseY)
                    && (collider.getColX() + (pApplet.width / 2 - camPos.x) < pApplet.mouseX)
                    && (collider.getColX() + collider.getWidth() + (pApplet.width / 2 - camPos.x) > pApplet.mouseX)) {
                renderer.setTransparent(true);
                if (pApplet.mousePressed) {
                    if (attached) {
                        turn.removeTurnable(gameObj());
                        attached = false;
                        if (usesGravity)
                            gameObj().gravity = true;
                    } else {
                        turn.addTurnables(gameObj());
                        attached = true;
                        gameObj().gravity = false;
                    }
                    pressed = true;
                }
            }
        }
    }
}
