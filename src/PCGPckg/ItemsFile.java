package PCGPckg;

import Other.Item;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * class to store item and equipment information
 */
public class ItemsFile {
    public static List<Item> getItems1(PApplet pApplet){
        List<Item> items = new ArrayList<>();
        items.add(new Item(pApplet.loadImage("gold_pile_0.png"), "Large Treasure", 100, 20));
        items.add(new Item(pApplet.loadImage("smallT.png"), "Small Treasure",  50, 20));
        items.add(new Item(pApplet.loadImage("medT.png"), "Medium Treasure", 80, 20));
        return items;
    }

    public static List<Item> getEquip1(PApplet pApplet){
        List<Item> items = new ArrayList<>();
        items.add(new Item(pApplet.loadImage("helmet1.png"), "Helmet", 10, 20, 0, 0, 10));
        items.add(new Item(pApplet.loadImage("healPotion.png"), "Potion", 30, 20, 0, 80, 0));
        items.add(new Item(pApplet.loadImage("healPotion.png"), "Potion", 10, 20, 0, 50, 0));
        items.add(new Item(pApplet.loadImage("armor1.png"), "Armor", 20, 20, 0, 0, 30));
        items.add(new Item(pApplet.loadImage("armor2.png"), "Armor", 60, 20, 0, 0, 90));
        items.add(new Item(pApplet.loadImage("sword1.png"), "Sword", 40, 20, 20, 0, 0));
        items.add(new Item(pApplet.loadImage("sword2.png"), "Sword", 80, 20, 60, 0, 0));
        return items;
    }

}
