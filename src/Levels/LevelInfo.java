package Levels;

public class LevelInfo {
    private String hint;
    private String levelName;

    public LevelInfo() {
    }

    public void reset(String hint, String levelName) {
        this.hint = hint;
        this.levelName = levelName;
    }

    public String getHint() {
        return hint;
    }

    public String getLevelName() {
        return levelName;
    }
}
