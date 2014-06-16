package Messages;

import com.google.gson.annotations.Expose;

/**
 * Created by Unknown1 on 6/15/14.
 */

public class ForwardMessageObject {

    public ForwardMessageObject() {

    }

    @Expose
    private String MessageType = "Forward";


    public String getMessageType() {
        return MessageType;
    }
}
