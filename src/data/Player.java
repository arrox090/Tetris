package data;

import java.io.Serializable;

public class Player implements Serializable {

    private String name;

    private final int[][] highScore = {{0, 0}, {0, 0}, {0, 0}}; // {lvl1, lvl2, lvl3}  | {Default, modified}
    private final int[][] avgScore = {{0, 0}, {0, 0}, {0, 0}}; // {lvl1, lvl2, lvl3} | {Default, modified}
    private final long[][] totalScore = {{0, 0}, {0, 0}, {0, 0}}; // {lvl1, lvl2, lvl3} | {Default, modified}
    private final int[][] gamesPlayed = {{0, 0}, {0, 0}, {0, 0}, {0, 0}}; // {lvl1, lvl2, lvl3, total} | {Default, modified}

    private boolean showGuideBlock = true;
    private boolean showNextBlock = true;
    private boolean musicOn = true;
    private float musicVolume = -37.0f;
    public Player(String name) {
        this.name = name;
    }


    // set/get player name
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }


    // set/get high scores
    public void setHighScore(int score, int level, int gameMode) {
        this.highScore[level - 1][gameMode] = score;
    }
    public int getHighScore(int level, int gameMode) {
        return this.highScore[level - 1][gameMode];
    }


    // get average score
    public int getAverageScore(int level, int gameMode) {
        return this.avgScore[level - 1][gameMode];
    }


    // get games played
    public int getGamesPlayed(int level, int gameMode) {
        return this.gamesPlayed[level - 1][gameMode];
    }


    // increase number of games played and calculate average score
    public void increaseGamesPlayed(long score, int level, int gameMode) {
        this.totalScore[level -1][gameMode] += score;
        this.gamesPlayed[level - 1][gameMode]++;
        this.avgScore[level - 1][gameMode] = (int) (this.totalScore[level - 1][gameMode] / this.gamesPlayed[level - 1][gameMode]);
    }


    // settings
    public void flipShowGuideBlock() {
        this.showGuideBlock = !this.showGuideBlock;
    }
    public boolean getShowGuideBlock() { return this.showGuideBlock; }

    public void flipShowNextBlock() {
        this.showNextBlock = !this.showNextBlock;
    }
    public boolean getShowNextBlock() { return this.showNextBlock; }

    public void flipMusicOn() {
        this.musicOn = !this.musicOn;
    }
    public boolean getMusicOn() { return this.musicOn; }

    public void setMusicVolume(int volume) {
        this.musicVolume = -80.0f + ((volume / 100.0f) * 86.0f);
    }

    public float getMusicVolume() { return this.musicVolume; }
}
