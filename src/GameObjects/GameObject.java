package GameObjects;

import Colliders.Collider;
import Play.GameManager;
import Renderers.Renderer;
import Renderers.RendererComposite;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameObject {
    public static float xCentre;
    public static float yCentre;
    public static List<GameObject> gameObjects = new ArrayList<>();
    public static boolean update = true;

    private static final PVector GRAVITY = new PVector(0, 0.4f);
    private static final float AIR_DRAG = 0.95f ;
    private static final float COLLISION_DRAG = 0.90f ;
    private static final float SCREEN_DELTA = 50;

    public Collider collider = null;
    private PApplet pApplet;
    public PVector position;
    public PVector velocity;
    public float orientation;
    public boolean gravity = false;
    private boolean dynamic = true;
    public String tag = "";
    public boolean enabled = true;
    public boolean drag = true;
    public int facing = 1;

    public List<Component> components = new ArrayList<>();
    private boolean destroyed;

    public GameObject() {
        this.pApplet = GameManager.getInstance();
        this.position = new PVector();
        this.velocity = new PVector();
        this.destroyed = false;
    }

    public GameObject(PApplet pApplet, float x, float y) {
        this.pApplet = pApplet;
        this.position = new PVector(xCentre + x, yCentre + y);
        this.velocity = new PVector(0, 0);
        this.destroyed = false;

        gameObjects.add(this);
    }

    public GameObject(PApplet pApplet, float x, float y, boolean gravity) {
        this.pApplet = pApplet;
        this.position = new PVector(xCentre + x, yCentre + y);
        this.velocity = new PVector(0, 0);
        this.gravity = gravity;
        this.destroyed = false;

        gameObjects.add(this);
    }

    /**
     * updates game object based on its components and position
     */
    public void update(){
        if (pApplet == null || !enabled)
            return;
        //if gravity applies
        if (gravity)
            velocity.add(GRAVITY);
        //if player or ai input is taken into account
        for (int i = 0; i < components.size(); i++) {
            components.get(i).update();
        }
        //apply drag
        if (drag)
            applyDrag();
        //adds accumulated velocity vector to the position vector
        if (dynamic) {
            position.add(velocity);
        }
    }

    public void resetPos(float x, float y) {
        this.position.x = xCentre + x;
        this.position.y = yCentre + y;
    }

    /**
     * removes object from the array list and and its collider too, if it has one
     */
    public void destroy(){
        Component comp = getComponent(Collider.class);
        if (comp instanceof Collider)
            ((Collider) comp).destroy();

        gameObjects.remove(this);
        this.destroyed = true;
    }

    /**
     * updates all objects in the array list
     */
    public static void updateAll(){
        if (update) {
            for (int i = 0; i < gameObjects.size(); i++) {
                gameObjects.get(i).update();
            }
        }
    }

    /**
     * sets all objects in the array list to destroyed and resets it
     */
    public static void destroyAll(){
        for (int i = 0; i < gameObjects.size(); i++) {
            gameObjects.get(i).destroyed = true;
        }
        gameObjects = new ArrayList<>();
        Collider.destroyAll();
    }

    /**
     * sets all objects in the array list to destroyed and resets it except for objects with given tag
     */
    public static void destroyAllExcept(String tag){
        List<GameObject> except = new ArrayList<>();
        for (int i = 0; i < gameObjects.size(); i++) {
            if (gameObjects.get(i).tag.equalsIgnoreCase(tag)) {
                except.add(gameObjects.get(i));
            }
        }
        gameObjects = new ArrayList<>();
        gameObjects.addAll(except);
        Collider.destroyAllExcept(tag);
    }

    /**
     * sets all objects in the array list to destroyed and resets it except for objects with given tag
     */
    public static void destroyAllExcept(String... tags){
        List<GameObject> except = new ArrayList<>();
        List<String> list =  Arrays.asList(tags);
        for (int i = 0; i < gameObjects.size(); i++) {
            if (list.contains(gameObjects.get(i).tag)) {
                except.add(gameObjects.get(i));
            }
        }
        gameObjects = new ArrayList<>();
        gameObjects.addAll(except);
        Collider.destroyAllExcept(tags);
    }

    /**
     * destroys all objects touched by this object currently
     * works only for circle colliders
     */
    void destroyTouched(){
        Collider collider = (Collider) this.getComponent(Collider.class);
        if (collider != null) {
            for (GameObject obj: (collider).getTouched()) {
                obj.destroy();
            }
        }
    }

    /**
     * applies movement resistance based on whether the object is touching anything or not
     */
    private void applyDrag(){
        if (collider != null && collider.isCollided()){
            velocity.mult(COLLISION_DRAG);
        }
        else{
            velocity.mult(AIR_DRAG);
        }
    }

    /**
     * destroys object if outside of the screen
     */
    private void onOutOfBounds(){
        if ((position.x + SCREEN_DELTA < 0 || position.x - SCREEN_DELTA > pApplet.width) || position.y > pApplet.height){
            this.destroy();
        }
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public GameObject copy(){
        GameObject obj = new GameObject(pApplet, position.x, position.y);

        obj.position = position.copy();
        obj.tag = tag;
        obj.gravity = gravity;
        obj.pApplet = pApplet;
        obj.velocity = velocity;
        obj.orientation = orientation;
        obj.destroyed = false;

        Component c = this.getComponent(Collider.class);
        Component r = this.getComponent(Renderer.class);
        if (c != null) {
            obj.addComponent(((Collider) c).copy(obj));
        }
        if (r != null) {
            obj.addComponent(((Renderer) r).copy(obj));
        }

        return obj;
    }

    /**
     * returns component of given class if there is one
     * @param classType class of the component
     * @return component, if there is one, or null
     */
    public Component getComponent(Class<? extends Component> classType){
        for (int i = 0; i < components.size(); i++) {
            Class c = components.get(i).getClass();
            if (c.getSimpleName().endsWith(classType.getSimpleName())){
                return components.get(i);
            }
            else if (c.getSuperclass().getSimpleName().endsWith(classType.getSimpleName())) {
                return components.get(i);
            }
        }
        return null;
    }

    /**
     * returns component of given class if there is one
     * @param classType class of the component
     * @return component, if there is one, or null
     */
    public Component removeComponent(Class<? extends Component> classType){
        for (int i = 0; i < components.size(); i++) {
            Class c = components.get(i).getClass();
            if (c.getSimpleName().endsWith(classType.getSimpleName())){
                return components.remove(i);
            }
        }
        return null;
    }


    /**
     * attach given component to the game object
     * @param component component to attach
     */
    public void addComponent(Component component){
        if (!components.contains(component)) {
            components.add(component);
            if (component instanceof Collider)
                collider = (Collider) component;
        }
        component.gameObject = this;

    }

    @Override
    public String toString() {
        return tag;
    }

    public static GameObject get(String tag) {
        for (GameObject obj : gameObjects) {
            if (obj.tag.equals(tag))
                return obj;
        }
        return null;
    }
}
