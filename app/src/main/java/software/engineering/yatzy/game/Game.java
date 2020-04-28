package software.engineering.yatzy.game;

import java.util.ArrayList;

public class Game {
    private int gameID;
    private String gameName;
    private GameState state;
    private TurnState turnState;
    private ArrayList<Player> playerList;
    private String winnerName;
    private int winnerScore;


    public Game(int gameID, String gameName, GameState state, TurnState turnState, ArrayList<Player> playerList, String winnerName, int winnerScore) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.state = state;
        this.turnState = turnState;
        this.playerList = playerList;
        this.winnerName = winnerName;
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

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public TurnState getTurnState() {
        return turnState;
    }

    public void setTurnState(TurnState turnState) {
        this.turnState = turnState;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public int getWinnerScore() {
        return winnerScore;
    }

    public void setWinnerScore(int winnerScore) {
        this.winnerScore = winnerScore;
    }

    public Player getPlayer(int index) {
        return playerList.get(index);
    }
}