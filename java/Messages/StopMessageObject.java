package Messages;

import com.google.gson.annotations.Expose;

/**
 * Created by Unknown1 on 6/15/14.
 */

public class StopMessageObject {

    public StopMessageObject() {

    }

    @Expose
    String MessageType = "Stop";


    public String getMessageType() {
        return MessageType;
    }
}
