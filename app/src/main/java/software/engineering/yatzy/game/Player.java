package software.engineering.yatzy.game;

import java.util.Arrays;

public class Player {
    private PlayerParticipation participation;
    private String name;
    private int[] scoreBoard;

    public Player(String name) {
        this.name = name;
    }

    public Player(String name, PlayerParticipation participation, int[] scoreBoard) {
        this.name = name;
        this.participation = participation;
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

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", scoreBoard=" + Arrays.toString(scoreBoard) +
                '}';
    }
}
