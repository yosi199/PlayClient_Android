package Messages;

import com.google.gson.annotations.Expose;

/**
 * Created by Unknown1 on 6/15/14.
 */

public class PlayMessageObject {

    public PlayMessageObject() {

    }

    @Expose
    private String MessageType = "Play";


    public String getMessageType() {
        return MessageType;
    }
}
