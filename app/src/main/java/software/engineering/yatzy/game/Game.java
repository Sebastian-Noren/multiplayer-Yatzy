package software.engineering.yatzy.game;

import java.util.ArrayList;

public class Game {
    private int gameID;
    private String gameName;
    public GameState state;
    private TurnState turnState;
    public ArrayList<Player> playerList;
    private String winnerName;
    private int winnerScore;
    private int playerListSize;


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

    // Add score in a single scoreboard cell
    public void updateScoreBoard(int playerIndex, int scoreboardIndex, int scoreboardValue) {
        playerList.get(playerIndex).setScoreBoardElement(scoreboardIndex, scoreboardValue);
    }

    // Called when all player's has responded and those that have declined has been removed
    public void updatePlayerList(ArrayList<Player> updatedPlayerList) {
        //playerList.clear();
        playerList = updatedPlayerList;
    }

    public Player getPlayer(int index) {
        return playerList.get(index);
    }

    public Player getPlayerByName(String nameID) {
        for(Player player : playerList) {
            if(player.getName().equals(nameID)) {
                return player;
            }
        }
        return  null;
    }

    public int getPlayerListSize(){
        return playerList.size();
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameID=" + gameID +
                ", gameName='" + gameName + '\'' +
                ", state=" + state +
                ", turnState=" + turnState +
                ", playerList=" + playerList +
                ", winnerName='" + winnerName + '\'' +
                ", winnerScore=" + winnerScore +
                ", playerListSize=" + playerListSize +
                '}';
    }
}
