package network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Unknown1 on 6/24/14.
 */
public class NetworkService extends IntentService {

    private String TAG = "Service";

    public NetworkService() {
        super("NetworkServiceWorker");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "starting the service");
        TCPCLIENT client = new TCPCLIENT();
        client.run();


    }

}
