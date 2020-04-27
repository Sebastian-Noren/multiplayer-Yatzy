package software.engineering.yatzy.game;

import java.util.Arrays;

public class Player {
    private String name;
    private int[] scoreBoard;

    public Player(String name) {
        this.name = name;
    }

    public Player(String name, int[] scoreBoard) {
        this.name = name;
        this.scoreBoard = scoreBoard;
    }

    public Player() {
    }


    public int[] getScoreBoard() {
        return scoreBoard;
    }

    public void setScoreBoard(int[] scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    public int getScoreBoardElement(int index){

        return scoreBoard[index];
    }

    public void setScoreBoardElement(int index, int value){
        this.scoreBoard[index] = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", scoreBoard=" + Arrays.toString(scoreBoard) +
                '}';
    }
}
