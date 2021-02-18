package Renderers;

import Colliders.BoxCollider;
import GameObjects.GameObject;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class RendererComposite extends Renderer {
    private List<RendererLeaf> renderers;
    private static PVector zero = new PVector(0, 0);
    private int actualWidth;

    public RendererComposite(PApplet pApplet, GameObject gameObject, PImage img, int actualWidth) {
        super(pApplet, gameObject);
        this.renderers = new ArrayList<>();
        this.actualWidth = actualWidth;
        this.img = img;
        BoxCollider bCol = (BoxCollider) collider;
        RendererLeaf rl;

        for (int i = 0; i < bCol.getWidth(); i += actualWidth) {
            for (int j = 0; j < bCol.getHeight(); j += actualWidth) {
                rl = new RendererLeaf(pApplet, gameObject, img, actualWidth, actualWidth);
                rl.positionDiff = new PVector(i, j);
                this.renderers.add(rl);
            }
        }
    }

    public void addRenderer(RendererLeaf renderer) {
        renderers.add(renderer);
    }

    @Override
    public void setTransparent(boolean transparent) {
        for (Renderer r : renderers) {
            r.setTransparent(transparent);
        }
    }

    @Override
    public void update() {
    }

    @Override
    public Renderer copy(GameObject gameObject) {
        return new RendererComposite(pApplet, gameObject, img, actualWidth);
    }
}
