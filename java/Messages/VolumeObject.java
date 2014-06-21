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
