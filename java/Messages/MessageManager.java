package Messages;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Map;

import Interfaces.IListener;
import Interfaces.ISubject;
import network.TCPCLIENT;

/**
 * Created by Unknown on 15/06/2014.
 */
public class MessageManager implements ISubject {

    public static final String STATUS = "Status";
    public static final String SONG = "song";
    private static MessageManager instance = null;
    private static String TAG = "MessageManager";
    private static volatile int mListenerIndex = 0;
    private ArrayList<IListener> mListeners = new ArrayList<>();
    private TCPCLIENT mTcpClient = null;
    private ServerStatusMessage serverStatusMessage_Obj;
    private Song songObj;


    private MessageManager() {
    }

    public static MessageManager Instance() {

        if (instance == null) {
            instance = new MessageManager();
        }

        return instance;

    }

    public void sendMessage(String msg) {
        if (TCPCLIENT.IsConnected) {
            Log.d("SentToServer", "Sent");
            mTcpClient.sendMessage(msg);
        }
    }

    public void figureMessageType(String message) {

        // reset objects
        songObj = null;
        serverStatusMessage_Obj = null;

        if (message.length() > 0) {

            Map messageObjMap = new Gson().fromJson(message, Map.class);
            String type = messageObjMap.get("messageType").toString();

            switch (type) {

                case SONG:
                    try {
                        Gson gson = new GsonBuilder().create();
                        songObj = gson.fromJson(message, Song.class);
                        NotifyUpdates(SONG);
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                    break;

                case STATUS:
                    Gson gson = new GsonBuilder().create();
                    serverStatusMessage_Obj = gson.fromJson(message, ServerStatusMessage.class);
                    if (serverStatusMessage_Obj.getSongJson() == null) {
                        songObj = new Song();
                        Log.d(TAG, "created empty song");

                    } else if (serverStatusMessage_Obj.getSongJson() != null) {
                        songObj = gson.fromJson(serverStatusMessage_Obj.getSongJson(), Song.class);
                    }
                    NotifyUpdates(STATUS);
                    break;
                default:
                    break;
            }

        }

    }

    public void registerTcpClient(TCPCLIENT client) {
        mTcpClient = client;
    }

    /**
     * Implementations of Observer pattern
     */

    @Override
    public void RegisterListener(IListener listener) {
        mListeners.add(listener);
    }

    @Override
    public void NotifyUpdates(String what) {

        for (int i = 0; i < mListeners.size(); i++) {
            mListeners.get(i).UpdateInfo(what);
        }

    }

    /**
     * A getter for a ServerStatus message type
     */
    public ServerStatusMessage getServerStatusMessage_Obj() {
        return serverStatusMessage_Obj;
    }

    /**
     * A getter for a Song message type
     */
    public Song getSongObj() {
        return songObj;
    }

    public enum PlayerType {
        LocalPlayer,
        SoundCloud
    }
}
