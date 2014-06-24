package Messages;

import com.google.gson.annotations.Expose;

/**
 * Created by Unknown1 on 6/15/14.
 */

public class ShuffleMessageObject {

    public ShuffleMessageObject() {

    }

    @Expose
    private String MessageType = "Shuffle";

    @Expose
    private Boolean IsShuffleOn = false;

    public Boolean getIsShuffleOn() {
        return IsShuffleOn;
    }

    public void setIsShuffleOn(Boolean isShuffleOn) {
        IsShuffleOn = isShuffleOn;
    }

    public String getMessageType() {
        return MessageType;
    }
}
