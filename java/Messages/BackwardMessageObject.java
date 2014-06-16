package Messages;

import com.google.gson.annotations.Expose;

/**
 * Created by Unknown1 on 6/15/14.
 */

public class BackwardMessageObject {

    public BackwardMessageObject() {

    }

    @Expose
    private String MessageType = "Backward";


    public String getMessageType() {
        return MessageType;
    }
}
