package Messages;

import com.google.gson.annotations.Expose;

/**
 * Created by Unknown1 on 6/15/14.
 */

public class VolumeObject {

    public VolumeObject() {

    }

    @Expose
    private String MessageType = "Volume";

    @Expose
    private String WhichWay = "Up";

    @Expose
    private int progress = 0;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
    }

    public void setWhichWay(String whichWay) {
        WhichWay = whichWay;
    }


    public String getWhichWay() {
        return WhichWay;
    }

    public void SetWhichWay(String direction) {
        WhichWay = direction;
    }


    public String getMessageType() {
        return MessageType;
    }
}
