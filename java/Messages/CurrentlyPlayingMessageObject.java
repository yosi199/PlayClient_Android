package Messages;

import com.google.gson.annotations.Expose;

/**
 * Created by Unknown1 on 6/15/14.
 */

public class CurrentlyPlayingMessageObject {

    @Expose
    private String MessageType = "CurrentlyPlaying";

    public CurrentlyPlayingMessageObject() {

    }

    public String getMessageType() {
        return MessageType;
    }
}
