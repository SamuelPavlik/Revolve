package Other;

import processing.core.PImage;

public class Item implements Comparable{
    private PImage pImage;
    private String name;
    private int value;
    private int radius;
    private int addStrength;
    private int addHealth;
    private int addMaxHealth;

    public Item(PImage pImage, String name, int value, int radius) {
        this.pImage = pImage;
        this.name = name;
        this.value = value;
        this.radius = radius;
        this.addStrength = 0;
        this.addHealth = 0;
    }

    public Item(PImage pImage, String name, int value, int radius, int addStrength, int addHealth, int addMaxHealth) {
        this.pImage = pImage;
        this.name = name;
        this.value = value;
        this.radius = radius;
        this.addStrength = addStrength;
        this.addHealth = addHealth;
        this.addMaxHealth = addMaxHealth;
    }

    public String getName() {
        return name;
    }

    public int getRadius() {
        return radius;
    }

    public PImage getpImage() {
        return pImage;
    }

    public int getAddStrength() {
        return addStrength;
    }

    public int getAddHealth() {
        return addHealth;
    }

    public int getAddMaxHealth() {
        return addMaxHealth;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Item)){
            return 0;
        }
        return ((Item) o).value - this.value;
    }
}
