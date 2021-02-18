package Colliders;

import GameObjects.GameObject;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class BoxCollider extends Collider {
    private static final float DELTA = 5;

    float width;
    float height;

    public BoxCollider(PApplet pApplet, GameObject gameObject, float width, float height) {
        super(pApplet, gameObject);
        this.colX = gameObject.position.x - width/2;
        this.colY = gameObject.position.y - height/2;
        this.positionDiff = new PVector(gameObject.position.x - colX, gameObject.position.y - colY);
        this.width = width;
        this.height = height;
        setCentre();
    }

    public BoxCollider(PApplet pApplet, GameObject gameObject, float width, float height, boolean trigger) {
        this(pApplet, gameObject, width, height);
        this.trigger = trigger;
    }


    public BoxCollider(GameObject gameObject, float x, float y, float width, float height) {
        super(gameObject, x, y);
        this.width = width;
        this.height = height;
        setCentre();
    }

    public BoxCollider(float x, float y, float width, float height) {
        super(x, y);
        this.width = width;
        this.height = height;
        setCentre();
    }

    /**
     * checks if collider collides with any other colliders and initializes touchVectors
     * @return true if in collision with another object
     */
    @Override
    public boolean checkCollision() {
        touched = new ArrayList<>();
        touchVectors = new ArrayList<>();
        collided = false;
        surfInCollision = 0;
        for (Collider m: colliders) {
            if (m.equals(this) || (!m.enabled && !m.trigger))
                continue;

            //in case of the other object being circle
            if (m instanceof CircleCollider){
                CircleCollider circle = (CircleCollider) m;
                if ((circle.colY + circle.radius > this.colY)
                        && (circle.colY - circle.radius < this.colY + this.height)){
                    if ((circle.colX + circle.radius > this.colX)
                            && (circle.colX - circle.radius < this.colX + this.width)){
                        PVector touchVector = null;
                        if (m.gameObj() != null) {
                            touched.add(m.gameObj());
                        }
                        //if touching from lower edge
                        if ((circle.colX + circle.radius > this.colX) && (circle.colX - circle.radius < this.colX + this.width)){
                            if ((circle.colY - circle.radius < this.colY + this.height)){
                                touchVector = new PVector(0, -1);
                            }
                            //if touching from upper edge
                            else if (circle.colY + circle.radius > this.colY){
                                touchVector = new PVector(0, 1);
                            }
                        }
                        //if touching from sides
                        else if ((circle.colY > this.colY) && (circle.colY < this.colY + this.height)){
                            //from left
                            if (circle.colX < this.colX) {
                                touchVector = new PVector(1, 0);
                            }
                            //from right
                            else{
                                touchVector = new PVector(-1, 0);
                            }
                        }

                        if (touchVector != null) {
                            collided = true;
                            if (!trigger && !m.trigger) {
                                touchVectors.add(touchVector);
                            }
                        }
                    }
                }
            }
            //in case of the other object being rectangle
            else if (m instanceof BoxCollider) {
                BoxCollider box = (BoxCollider) m;
                //collision from upper edge
                if ((box.colY + box.height > this.colY)
                        && (box.colY < this.colY + this.height)){
                    if ((box.colX + box.width > this.colX)
                            && (box.colX < this.colX + this.width)){
                        if (m.gameObj() != null) {
                            touched.add(m.gameObj());
                        }
                        PVector touchVector = new PVector(0, 0);
                        //touching from upper edge
                        if (this.colY <= (box.colY + box.height) && (this.colY >= box.colY)){
                            touchVector.y = 1;
                        }
                        //touching from lower edge
                        if ((this.colY + this.height >= box.colY) && (this.colY <= box.colY)){
                            touchVector.y = -1;
                        }
                        //touching from left
                        if (this.colX <= (box.colX + box.width) && (this.colX >= box.colX)){
                            touchVector.x = 1;
                        }
                        //touching from right
                        if ((this.colX + this.width >= box.colX) && (this.colX <= box.colX)){
                            touchVector.x = -1;
                        }
                        if (touchVector.x != 0 || touchVector.y != 0) {
                            collided = true;
                            //resolve larger surface point !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            if (!trigger && !m.trigger) {
                                if (touchVector.x != 0 && touchVector.y != 0){
                                    touchVector = getLargerContactSurface(box, touchVector);
                                }
                                touchVectors.add(touchVector);
                            }
                            else {
                                getLargerContactSurface(box, touchVector);
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
    protected void drawShape() {
        pApplet.rect(getColX(), getColY(), width, height);
    }

    private PVector getLargerContactSurface(BoxCollider box, PVector touchVector) {
        float leftX;
        float rightX;
        float upperY;
        float lowerY;

        if (colX > box.colX) {
            leftX = colX;
        }
        else {
            leftX = box.colX;
        }

        if (colX + width < box.colX + box.width) {
            rightX = colX + width;
        }
        else {
            rightX = box.colX + box.width;
        }

        if (colY > box.colY) {
            upperY = colY;
        }
        else {
            upperY = box.colY;
        }

        if (colY + height < box.colY + box.height) {
            lowerY = colY + height;
        }
        else {
            lowerY = box.colY + box.height;
        }

        float xSurface = rightX - leftX;
        float ySurface = lowerY - upperY;
        if (xSurface > ySurface){
            if (ySurface > surfInCollision)
                surfInCollision = ySurface;
            return new PVector(0, touchVector.y);
        }
        else {
            if (xSurface > surfInCollision)
                surfInCollision = xSurface;
            if (surfInCollision > MIN) {
                return new PVector(touchVector.x, 0);
            }
        }

        return new PVector(0, 0);
    }


    public List<BoxCollider> splitCollider(int coord1, int coord2, boolean byX){
        List<BoxCollider> cols = new ArrayList<>();
        if (byX){
            if ((coord1 == colX) && (coord2 == colX + width)){

            }
            else if (coord2 == colX + width){
                cols.add(new BoxCollider(coord1, colY, width - Math.abs(coord1 - coord2), height));
            }
            else if (coord1 == colX){
                cols.add(new BoxCollider(coord2, colY, width - Math.abs(coord1 - coord2), height));
            }
            else {
                cols.add(new BoxCollider(colX, colY, coord1 - colX, height));
                cols.add(new BoxCollider(coord2, colY, (colX+width) - coord2, height));
            }

            this.destroy();
        }
        else {
            if ((coord1 == colY) && (coord2 == colY + height)){

            }
            else if (coord2 == colY + height){
                cols.add(new BoxCollider(colX, coord1, width, height - Math.abs(coord1 - coord2)));
            }
            else if (coord1 == colY){
                cols.add(new BoxCollider(colX, coord2, width, height - Math.abs(coord1 - coord2)));
            }
            else {
                cols.add(new BoxCollider(colX, colY, width, coord1 - colY));
                cols.add(new BoxCollider(colX, coord2, width, (colY+height) - coord2));
            }

            this.destroy();
        }

        return cols;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public Collider copy(GameObject gameObject) {
        return new BoxCollider(pApplet, gameObject, width, height, trigger);
    }

    @Override
    public void setCentre() {
        this.centreColX = gameObj().position.x;
        this.centreColY = gameObj().position.y;
        this.colX = centreColX - width/2;
        this.colY = centreColY - height/2;
    }
}