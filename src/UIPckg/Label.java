package UIPckg;

import processing.core.PApplet;

/**
 * source: https://blog.startingelectronics.com/a-simple-button-for-processing-language-code/
 */
public class Label {
    private static final int DIFF = 5;

    private PApplet pApplet;
    private String label;
    float x;    // top left corner x position
    float y;    // top left corner y position
    private float width;    // width of button
    private float height;    // height of button
    private boolean isButton;
    public boolean enabled = true;
    private int timer = -1;

    public Label(PApplet pApplet, String labelB, float xpos, float ypos, boolean isButton) {
        this.pApplet = pApplet;
        this.label = labelB;
        this.x = xpos;
        this.y = ypos;
        this.width = (labelB.length() + 1) * GameUI.SCORE_TEXT_WIDTH;
        this.height = GameUI.SCORE_TEXT_HEIGHT;
//        this.width = width;
//        this.height = height;
        this.isButton = isButton;
    }

    public Label(PApplet pApplet, String labelB, float xpos, float ypos, boolean isButton, int textSize) {
        this.pApplet = pApplet;
        this.label = labelB;
        this.x = xpos;
        this.y = ypos;
        this.width = (labelB.length() + 1) * textSize/2;
        this.height = textSize;
//        this.width = width;
//        this.height = height;
        this.isButton = isButton;
    }


    public void update() {
        if (!enabled)
            return;
        if (timer != -1) {
            if (timer != 0) timer--;
            else enabled = false;
        }

        if (isButton && mouseIsOver()) {
            pApplet.fill(218);
        } else {
            pApplet.fill(250);
        }
        pApplet.stroke(141);
        pApplet.rect(x, y, width, height, 10);
        pApplet.textAlign(PApplet.CENTER, PApplet.CENTER);
        pApplet.fill(0);
        pApplet.textSize(height - DIFF);
        pApplet.text(label, x + (width / 2), y + (height / 2));
    }

    public boolean mouseIsOver() {
        if (pApplet.mouseX > x && pApplet.mouseX < (x + width) && pApplet.mouseY > y && pApplet.mouseY < (y + height)) {
            return true;
        }
        return false;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        float lastWidth = width;
        this.label = label;
        this.width = (label.length() + 1) * GameUI.SCORE_TEXT_WIDTH;
        this.height = GameUI.SCORE_TEXT_HEIGHT;
        this.x += lastWidth - width;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void setButton(boolean button) {
        isButton = button;
    }
}
