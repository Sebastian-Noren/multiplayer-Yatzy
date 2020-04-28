package software.engineering.yatzy.game;

public class TurnState {
    private int currentPlayer;
    private int rollTurn;
    private int[] diceValues;

    public TurnState(int currentPlayer, int rollTurn, int[] diceValues) {
        this.currentPlayer = currentPlayer;
        this.rollTurn = rollTurn;
        this.diceValues = diceValues;
    }

    public TurnState() {
        // Used to assign a nonsense scoreboard before game start
        currentPlayer = -1;
        rollTurn = -1;
        diceValues = new int[]{-1};
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
}
