package Renderers;

import GameObjects.GameObject;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

public class RendererLeaf extends Renderer {
    private static final PVector zero = new PVector(0, 0);
    private static final float ALPHA = 100;
    private static final int SIZE_MULTIPLIER = 10;

    private float width;
    private float height;
    private int imageMode = PConstants.CORNER;

    public RendererLeaf(PApplet pApplet, PImage img, PVector positionDiff, float width, float height) {
        super(pApplet, positionDiff);
        this.width = width;
        this.height = height;
        this.img = img;
    }

    public RendererLeaf(PApplet pApplet, PImage img, PVector positionDiff, float width, float height, int imageMode) {
        super(pApplet, positionDiff);
        this.width = width;
        this.height = height;
        this.img = img;
        this.imageMode = imageMode;
    }

    public RendererLeaf(PApplet pApplet, GameObject gameObject, PImage img, float width, float height) {
        super(pApplet, gameObject);
        this.width = width;
        this.height = height;
        this.img = img;
    }

    public RendererLeaf(PApplet pApplet, PImage img, float width, float height, int imageMode) {
        super(pApplet, zero);
        this.img = img;
        this.width = width;
        this.height = height;
        this.imageMode = imageMode;
    }

    /**
     * draw the given PImage with position and rotation
     */
    @Override
    public void update() {
        if (gameObj() == null)
            return;
        pApplet.pushMatrix();
        if (transparent)
            pApplet.tint(255, ALPHA);

        pApplet.scale(gameObj().facing, 1);

        if (collider == null) {
            pApplet.translate((gameObj().position.x + positionDiff.x)*gameObj().facing, (gameObj().position.y + positionDiff.y));
            pApplet.rotate(gameObj().orientation);
            if (img != null) {
                pApplet.imageMode(PConstants.CENTER);
                pApplet.image(img, 0, 0, img.width*width*gameObj().facing, img.height*height);
                pApplet.imageMode(PConstants.CORNER);
            }
        }
        else {
            pApplet.translate((collider.getColX() + positionDiff.x)*gameObj().facing, collider.getColY() + positionDiff.y);
            pApplet.rotate(gameObj().orientation);
            if (img != null) {
                pApplet.imageMode(PConstants.CORNER);
                pApplet.image(img, 0, 0, width*gameObj().facing, height);
//            pApplet.imageMode(PConstants.CORNER);
            }
        }

        if (transparent)
            pApplet.noTint();
        pApplet.popMatrix();
    }

    @Override
    public Renderer copy(GameObject gameObject) {
        return new RendererLeaf(pApplet, img, positionDiff, width, height);
    }
}
