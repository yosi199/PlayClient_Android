package Messages;

import com.google.gson.annotations.Expose;

/**
 * Created by Unknown1 on 6/15/14.
 */

public class SetPlayerMessageObject {

    public SetPlayerMessageObject() {

    }

    @Expose
    private String MessageType = "SetPlayer";

    @Expose
    private String NewPlayer = "";

    public String getNewPlayer() {
        return NewPlayer;
    }

    public void setNewPlayer(String newPlayer) {
        NewPlayer = newPlayer;
    }

    public String getMessageType() {
        return MessageType;
    }
}
