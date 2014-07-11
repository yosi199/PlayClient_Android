package Messages;

import com.google.gson.annotations.Expose;

/**
 * Created by Unknown1 on 6/15/14.
 */

public class KillAndRestartMessageObject {

    @Expose
    private String MessageType = "KillAndRestart";

    public KillAndRestartMessageObject() {

    }

    public String getMessageType() {
        return MessageType;
    }
}
