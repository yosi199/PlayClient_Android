package Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loop_to_infinity.play.R;

/**
 * Created by Unknown on 10/07/2014.
 */
public class SoundCloudFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sound_cloud_fragment, container, false);
    }
}