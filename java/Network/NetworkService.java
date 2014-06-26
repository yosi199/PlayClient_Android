package Network;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Unknown1 on 6/24/14.
 */
public class NetworkService extends IntentService {

    public NetworkService() {
        super("NetworkServiceWorker");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

}
