package Messages;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import Network.TCPCLIENT;

/**
 * Created by Unknown on 15/06/2014.
 */
public class MessageManager {

    private static MessageManager instance = null;
    private TCPCLIENT.OnMessageReceived mMessageListener = null;
    private static String TAG = "MessageManager";


    private MessageManager() {
    }

    public static MessageManager Instance() {

        if (instance == null) {
            instance = new MessageManager();
        }

        return instance;

    }

    public Object figureMessageType(String message, TCPCLIENT.OnMessageReceived mMessageListener) {

        Object obj = null;

        if (message.length() > 0) {

            Map messageObjMap = new Gson().fromJson(message, Map.class);
            String type = messageObjMap.get("messageType").toString();

            switch (type) {

                case "song":
                    try {
                        Gson gson = new GsonBuilder().create();
                        Song song = gson.fromJson(message, Song.class);
                        mMessageListener.messageReceived(song.getArtistName() + " - " + song.getTitleName());
                        obj = song;
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                    break;

                case "Status":
                    Gson gson = new GsonBuilder().create();
                    ServerStatusMessage statusMessage = gson.fromJson(message, ServerStatusMessage.class);
                    obj = statusMessage;
                    break;
                default:
                    obj = null;
            }

        }

        return obj;
    }

    public enum PlayerType {
        LocalPlayer,
        SoundCloud
    }
}
