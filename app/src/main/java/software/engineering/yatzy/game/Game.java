package software.engineering.yatzy.game;

public class Game {
    private int gameID;
    private String gameName;
    private String state;
    private TurnState turnState;
    private String WinnerName;
    private int winnerScore;


    public Game(int gameID, String gameName, String state, TurnState turnState, String winnerName, int winnerScore) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.state = state;
        this.turnState = turnState;
        WinnerName = winnerName;
        this.winnerScore = winnerScore;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public TurnState getTurnState() {
        return turnState;
    }

    public void setTurnState(TurnState turnState) {
        this.turnState = turnState;
    }

    public String getWinnerName() {
        return WinnerName;
    }

    public void setWinnerName(String winnerName) {
        WinnerName = winnerName;
    }

    public int getWinnerScore() {
        return winnerScore;
    }

    public void setWinnerScore(int winnerScore) {
        this.winnerScore = winnerScore;
    }
}
