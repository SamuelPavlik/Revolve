package Colliders;

import GameObjects.GameObject;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class CircleCollider extends Collider {
    float radius;

    public CircleCollider(PApplet pApplet, GameObject gameObject, float radius, boolean trigger) {
        super(pApplet, gameObject);
        this.colX = gameObject.position.x;
        this.colY = gameObject.position.y;
        this.centreColX = this.colX;
        this.centreColY = this.colY;
        this.radius = radius;
        this.touched = new ArrayList<>();
        this.trigger = trigger;
    }

    public CircleCollider(GameObject gameObject, float x, float y, float radius) {
        super(gameObject, x, y);
        this.radius = radius;
        this.touched = new ArrayList<>();
    }

    public CircleCollider(GameObject gameObject, float radius) {
        super(gameObject, gameObject.position.x, gameObject.position.y);
        this.radius = radius;
        this.touched = new ArrayList<>();
    }

    public CircleCollider(GameObject gameObject, float radius, boolean trigger) {
        super(gameObject, gameObject.position.x, gameObject.position.y);
        this.trigger = trigger;
        this.radius = radius;
        this.touched = new ArrayList<>();
    }


    /**
     * checks if collider collides with any other colliders and initializes the fields touchVectors and touched
     * @return true if in collision with another object
     */
    @Override
    public boolean checkCollision() {
        touched = new ArrayList<>();
        touchVectors = new ArrayList<>();
        PVector touchVector = null;

        for (Collider m : colliders) {
            if (m.equals(this))
                continue;
            if (m instanceof CircleCollider){
                CircleCollider circle = (CircleCollider) m;
                PVector dist = new PVector(this.colX - circle.colX, this.colY - circle.colY);
                if (dist.mag() < this.radius + circle.radius){
                    collided = true;
                    touchVector = new PVector(this.colX - circle.colX, this.colY - circle.colY).normalize();
                    touchVectors.add(touchVector);
                    if (m.gameObj() != null) {
                        touched.add(m.gameObj());
                    }
                }
            }
            else if (m instanceof BoxCollider) {
                BoxCollider box = (BoxCollider) m;
                if ((this.colY + this.radius > box.colY)
                        && (this.colY - this.radius < box.colY + box.height)){
                    if ((this.colX + this.radius > box.colX)
                            && (this.colX - this.radius < box.colX + box.width)){

                        if (m.gameObj() != null) {
                            touched.add(m.gameObj());
                        }
                        collided = true;
                        touchVector = new PVector(0, 0);
                        float hor = 0;
                        float vert = 0;

                        if ((this.colX + this.radius > box.colX)
                                && (this.colX - this.radius < box.colX + box.width)) {
                            //if touching from upper edge
                            if ((this.colY - this.radius < box.colY + box.height) && (this.colY > box.colY)){
                                touchVector.add(new PVector(0, 1));
//                                hor = (box.colY + box.height) - (this.colY - this.radius);
                            }
                            //if touching from lower edge
                            else if ((this.colY + this.radius > box.colY) && (this.colY < box.colY)){
                                lowerEdge = true;
                                touchVector.add(new PVector(0, -1));
//                                hor = (this.colY + this.radius) - (box.colY);
                            }
                        }

                        //if touching from sides
                        if ((this.colY + this.radius > box.colY) && (this.colY - this.radius < box.colY + box.height)){
                            //from right
                            if (this.colX + this.radius > box.colX) {
                                touchVector.add(new PVector(-1, 0));
                            }
                            //from left
                            else if (this.colX - this.radius < box.colX + box.width){
                                touchVector.add(new PVector(1, 0));
                            }
                        }

                        if (touchVector.x != 0 || touchVector.y != 0) {
                            //resolve larger surface point !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            if (!trigger && !m.trigger) {
                                touchVectors.add(touchVector);
                            }
                        }
                        collided = true;
                    }
                }
            }
        }

        return collided;
    }

    @Override
    public Collider copy(GameObject gameObject) {
        return new CircleCollider(pApplet, gameObject, radius, trigger);
    }

    @Override
    protected void drawShape() {
        pApplet.ellipse(colX, colY, 2*radius, 2*radius);
    }

    public float getRadius() {
        return radius;
    }
}
