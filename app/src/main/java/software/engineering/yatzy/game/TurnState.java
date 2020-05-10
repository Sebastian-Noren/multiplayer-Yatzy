package software.engineering.yatzy.game;

import java.util.Arrays;

public class TurnState {
    private int currentPlayer;
    private int rollTurn;
    private int[] diceValues;
    public int[] rolledDiceBitMap;

    public TurnState(int currentPlayer, int rollTurn, int[] diceValues) {
        this.currentPlayer = currentPlayer;
        this.rollTurn = rollTurn;
        this.diceValues = diceValues;
        rolledDiceBitMap = new int[]{0, 0, 0, 0, 0};
    }

    public TurnState() {
        // Used to assign a nonsense scoreboard before game start
        currentPlayer = -1;
        rollTurn = -1;
        diceValues = new int[]{-1};
        rolledDiceBitMap = new int[]{0, 0, 0, 0, 0};
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getRollTurn() {
        return rollTurn;
    }

    public void setRollTurn(int rollTurn) {
        this.rollTurn = rollTurn;
    }

    public int[] getDiceValues() {
        return diceValues;
    }

    public void setDiceValues(int[] diceValues) {
        this.diceValues = diceValues;
    }
    public int getDiceElement(int index){
        return diceValues[index];
    }

    @Override
    public String toString() {
        return "TurnState{" +
                "currentPlayer=" + currentPlayer +
                ", rollTurn=" + rollTurn +
                ", diceValues=" + Arrays.toString(diceValues) +
                '}';
    }
}
