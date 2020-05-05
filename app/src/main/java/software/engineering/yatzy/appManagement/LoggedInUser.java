package software.engineering.yatzy.appManagement;

import java.io.Serializable;

public class LoggedInUser implements Serializable {

    private String nameID;
    private String sessionKey;
    public int gamesPlayed;
    public int highScore;

    public LoggedInUser(String nameID, String sessionKey, int gamesPlayed, int highScore) {
        this.nameID = nameID;
        this.sessionKey = sessionKey;
        this.gamesPlayed = gamesPlayed;
        this.highScore = highScore;
    }

    public String getNameID() {
        return nameID;
    }

    public String getSessionKey() {
        return sessionKey;
    }

}
