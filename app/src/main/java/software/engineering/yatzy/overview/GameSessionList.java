package software.engineering.yatzy.overview;

public class GameSessionList {

    private String title;
    private String gameStatus;

    public GameSessionList(String title, String gameStatus) {
        this.title = title;
        this.gameStatus = gameStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }
}
