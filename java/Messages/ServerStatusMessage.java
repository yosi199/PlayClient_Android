package Messages;

import Utilities.Constants;

/**
 * Created by Unknown on 21/06/2014.
 */
public class ServerStatusMessage {

    private String messageType = "";
    private float CurrentVolume = 0;
    private float MaxVolume = 0;
    private float MinVolume = 0;
    private String PlayerTypeSet = Constants.LocalPlayer;
    private String SongJson;
    private Boolean IsShuffleOn = false;

    public ServerStatusMessage() {
    }

    public String getPlayerTypeSet() {
        return PlayerTypeSet;
    }

    public void setPlayerTypeSet(String playerTypeSet) {
        PlayerTypeSet = playerTypeSet;
    }

    public Boolean getIsShuffleOn() {
        return IsShuffleOn;
    }

    public void setIsShuffleOn(Boolean isShuffleOn) {
        IsShuffleOn = isShuffleOn;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public float getCurrentVolume() {
        return CurrentVolume;
    }

    public void setCurrentVolume(float currentVolume) {
        this.CurrentVolume = currentVolume;
    }

    public float getMaxVolume() {
        return MaxVolume;
    }

    public void setMaxVolume(float maxVolume) {
        this.MaxVolume = maxVolume;
    }

    public float getMinVolume() {
        return MinVolume;
    }

    public void setMinVolume(float minVolume) {
        this.MinVolume = minVolume;
    }

    public String getSongJson() {

        return SongJson;
    }

    public void setSongJson(String songJson) {
        SongJson = songJson;
    }
}
