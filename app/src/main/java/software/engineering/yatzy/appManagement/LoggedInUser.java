package software.engineering.yatzy.appManagement;

public class LoggedInUser {

    private String nameID;
    private String sessionKey;
    private int gamesPlayed;
    private int highScore;

    public LoggedInUser(String nameID, String sessionKey, int gamesPlayed, int highScore) {
        this.nameID = nameID;
        this.sessionKey = sessionKey;
        this.gamesPlayed = gamesPlayed;
        this.highScore = highScore;
    }


}
