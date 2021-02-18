package Behavior;

/**
 * class to be notified when turning or moving of an object is done for the player object
 */
public class Counter {
    private int value = 0;
    public boolean inCollision = false;

    public int getValue() {
        return value;
    }

    public void done() {
        this.value++;
    }

    public void undone() {
        this.value--;
    }

    public void reset() {
        this.value = 0;
    }

    @Override
    public String toString() {
        return "Counter{" +
                "value=" + value +
                '}';
    }
}
