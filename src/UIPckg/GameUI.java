package UIPckg;

import CameraPckg.Camera;
import Levels.LevelFactory;
import Play.GameManager;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class GameUI extends UI {
    private static final float MENU_TEXT_SIZE = 20;

    private static final String HINT = "Hint";
    private static final String RESTART = "Restart";
    private static final String MENU = " Menu ";
    private static final String LEVEL = "LEVEL ";
    private static final String START_GAME = "Start Game";
    private static final String EXIT = "Exit";
    public static final float SCORE_TEXT_WIDTH = 10;
    public static final float SCORE_TEXT_HEIGHT = 20;
    private static final float SCORE_POS_DIFF = 50;


    private List<Label> allLabels;
    private List<Label> staticLabels;
    private Camera camera;
    private LevelFactory levelFactory;

    public GameUI(PApplet pApplet, Camera camera, LevelFactory levelFactory) {
        super(pApplet);
        this.allLabels = new ArrayList<>();
        this.staticLabels = new ArrayList<>();
        this.camera = camera;
        this.levelFactory = levelFactory;

        if (levelFactory.getLevel() == -1) {
            float xCenter = pApplet.width / 2;
            float yCenter = pApplet.height / 2 - 100;
            Label startButton = new Label(pApplet, START_GAME, xCenter - 30, yCenter,true);

            allLabels.add(startButton);

            for (int i = 0; i < LevelFactory.MAX_LEVELS; i++) {
                Label lvlButton = new Label(pApplet, LEVEL + (i + 1), xCenter - 13, startButton.y + 40 + i*40,true);
                allLabels.add(lvlButton);
            }
            Label exitButton = new Label(pApplet, EXIT, xCenter, allLabels.get(allLabels.size() - 1).y + 40,true);
            allLabels.add(exitButton);
        }
        else {
            Label leftArrow = new Label(pApplet, "Move Left: A", SCORE_POS_DIFF, pApplet.height - 3*SCORE_POS_DIFF, false);
            Label rightArrow = new Label(pApplet, "Move Right: D", SCORE_POS_DIFF, pApplet.height - 2*SCORE_POS_DIFF, false);
            Label turnLabel = new Label(pApplet, "Turn around: Space", SCORE_POS_DIFF, pApplet.height - SCORE_POS_DIFF, false);
            Label hintButton = new Label(pApplet, HINT,
                    pApplet.width - (SCORE_POS_DIFF + (HINT.length()) * SCORE_TEXT_WIDTH), SCORE_POS_DIFF, true);
            Label restartButton = new Label(pApplet, RESTART, SCORE_POS_DIFF,
                    SCORE_POS_DIFF, true);
            Label menuButton = new Label(pApplet, MENU, SCORE_POS_DIFF,SCORE_POS_DIFF + 40, true);

            String level = LEVEL + levelFactory.getLevel() + ": " + levelFactory.getLevelInfo().getLevelName();
            int size = 30;
            Label levelLabel = new Label(pApplet, level,
                    (pApplet.width / 2 - ((level.length() + 1) * size) / 4),
                    pApplet.height / 2 - 3*SCORE_TEXT_HEIGHT, false, size);
            levelLabel.setTimer(100);

            allLabels.add(leftArrow);
            allLabels.add(rightArrow);
            allLabels.add(turnLabel);
            allLabels.add(hintButton);
            allLabels.add(restartButton);
            allLabels.add(menuButton);
            allLabels.add(levelLabel);

            if (levelFactory.getLevel() == 0) {
                Label revolvingTut = new Label(pApplet, "The objects hanging from the upper platform can revolve around you",
                        200, 800, false);
                Label areaEfTut = new Label(pApplet, "The area effectors add force in a direction of the arrows " +
                        "to solid objects in its area",
                        800, 700, false);
                Label nonColTut = new Label(pApplet, "Once all of the revolving objects are out of collision, they" +
                        " become solid and you can change your distance towards them",
                        1400, 800, false);
                Label atTut = new Label(pApplet, "The red revolving objects with lighter color of links can be" +
                        " detached once solid and attached at any time by clicking on them",
                        2000, 700, false, 20);

                staticLabels.add(revolvingTut);
                staticLabels.add(areaEfTut);
                staticLabels.add(nonColTut);
                staticLabels.add(atTut);
            }
        }
    }

    /**
     * update game menu and ui
     */
    public void update(){
        pApplet.pushMatrix();
        pApplet.translate(camera.getCurrPos().x - pApplet.width/2, camera.getCurrPos().y - pApplet.height/2);

        //update all dynamic labels
        for (Label allLabel : allLabels) {
            allLabel.update();
        }

        pApplet.popMatrix();

        //update all static labels
        for (Label staticLabel : staticLabels) {
            staticLabel.update();
        }

        onButtonPressed();
    }

    private Label getLabel(String label) {
        for (int i = 0; i < allLabels.size(); i++) {
            if (allLabels.get(i).getLabel().startsWith(label))
                return allLabels.get(i);
        }

        return null;
    }

    private void onButtonPressed() {
        if (pApplet.mousePressed) {
            if (levelFactory.getLevel() > -1) {
                for (Label button : allLabels) {
                    if (button.mouseIsOver()) {
                        String label = button.getLabel();
                        if (label.equals(RESTART))
                            GameManager.getInstance().restartLevel();
                        else if (label.equals(HINT)) {
                            button.setLabel(levelFactory.getLevelInfo().getHint());
                            button.setButton(false);
                        }
                        else if (label.equals(MENU)) {
                            GameManager.getInstance().setLevel(-1);
                            GameManager.getInstance().restartLevel();
                        }
                    }
                }
            }
            else {
                for (Label button : allLabels) {
                    if (button.mouseIsOver()) {
                        String label = button.getLabel();
                        if (label.equals(START_GAME))
                            GameManager.getInstance().nextLevel();
                        else if (label.equals(EXIT)) {
                            System.exit(0);
                        }
                        else {
                            int lvl = Integer.parseInt(label.substring(label.length() - 1, label.length()));
                            GameManager.getInstance().setLevel(lvl);
                            GameManager.getInstance().restartLevel();
                        }
                    }
                }
            }
        }
    }
}
