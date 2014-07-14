package network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import Messages.KillAndRestartMessageObject;
import Messages.MessageManager;

/**
 * Created by Unknown1 on 6/24/14.
 */
public class NetworkService extends IntentService {

    private String TAG = "Service";
    private TCPCLIENT client;
    private MessageManager messageManager;
    private String killMessage;


    public NetworkService() {
        super("NetworkServiceWorker");
        messageManager = MessageManager.Instance();
        Gson gson = new Gson();
        killMessage = gson.toJson(new KillAndRestartMessageObject());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "starting the service");
        new Thread(new Runnable() {
            @Override
            public void run() {
                client = new TCPCLIENT();
                client.run();
            }
        }).start();


    }

    @Override
    public void onDestroy() {

        messageManager.sendMessage(killMessage);
        client = null;
        Log.d(TAG, "Sent message - " + killMessage);
        Log.d(TAG, "Service destroyed");

    }

}
