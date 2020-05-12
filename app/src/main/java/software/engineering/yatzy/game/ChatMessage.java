package software.engineering.yatzy.game;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {

    public int msgIndex; // Unique identifier of chat messages belonging to this game
    public String senderName;
    public String message;
    public String timeStamp;
    public int replyToMsgIndex; // -1 if not an answer
    public boolean isDeleted;


    public ChatMessage(int msgIndex, String senderName, String message, String timeStamp, int replyToMsgIndex, boolean isDeleted) {
        this.msgIndex = msgIndex;
        this.senderName = senderName;
        this.message = message;
        this.timeStamp = timeStamp;
        this.replyToMsgIndex = replyToMsgIndex;
        this.isDeleted = isDeleted;
    }

}

