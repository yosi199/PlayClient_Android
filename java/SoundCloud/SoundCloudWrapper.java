package SoundCloud;

import android.util.Log;

import java.io.IOException;

/**
 * Created by Unknown on 09/07/2014.
 */
public class SoundCloudWrapper {

    private ApiWrapper wrapper = null;

    private String TAG = "SoundCloudWrapper";
    private Token _token;

    public SoundCloudWrapper() {

        // Create a wrapper instance
        wrapper = new ApiWrapper("8d3806bf633d53b91db0538ca5714ae0", "b9d57a3716cfe60294753dca1a7ef978",
                null, null);
    }

    public void Login() {
        try {
            _token = wrapper.login("yosimizrachi7@gmail.com", "11221122");
            Log.d(TAG, "Token - " + _token);
        } catch (IOException io) {
            Log.d(TAG, "Login Failed (Exception) - " + io.getMessage());
        }
    }


}
