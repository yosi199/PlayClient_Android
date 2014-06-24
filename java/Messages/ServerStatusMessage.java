package Messages;

/**
 * Created by Unknown on 21/06/2014.
 */
public class ServerStatusMessage {

    private String messageType = "";
    private float CurrentVolume = 0;
    private float MaxVolume = 0;
    private float MinVolume = 0;

    public Boolean getIsShuffleOn() {
        return IsShuffleOn;
    }

    public void setIsShuffleOn(Boolean isShuffleOn) {
        IsShuffleOn = isShuffleOn;
    }

    private Boolean IsShuffleOn = false;

    public void setMinVolume(float minVolume) {
        this.MinVolume = minVolume;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setCurrentVolume(float currentVolume) {
        this.CurrentVolume = currentVolume;
    }

    public void setMaxVolume(float maxVolume) {
        this.MaxVolume = maxVolume;
    }


    public String getMessageType() {
        return messageType;
    }

    public float getCurrentVolume() {
        return CurrentVolume;
    }

    public float getMaxVolume() {
        return MaxVolume;
    }

    public float getMinVolume() {
        return MinVolume;
    }

    public ServerStatusMessage() {
    }
}
