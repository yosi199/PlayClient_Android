package Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.loop_to_infinity.play.R;

import java.util.concurrent.TimeUnit;

import Interfaces.IListener;
import Messages.BackwardMessageObject;
import Messages.ForwardMessageObject;
import Messages.MessageManager;
import Messages.PlayMessageObject;
import Messages.ServerStatusMessage;
import Messages.ShuffleMessageObject;
import Messages.Song;
import Messages.StopMessageObject;
import Messages.VolumeObject;
import network.NetworkService;
import network.TCPCLIENT;


/**
 * Created by Unknown1 on 7/10/13.
 */
public class Play_Main extends Fragment implements IListener {

    private static final String TAG = "Play_Main_Fragment";

    public static String MessengerObject = "Messenger";

    // Views and fields
    private TextView tv1;
    private Button connectButton;
    private Button play;
    private Button back;
    private Button forward;
    private Button stop;
    private SeekBar volume;
    private CheckBox shuffle;

    // Object Instances
    private Play_Main mainFrag;
    private MessageManager messageManager;

    // Fields
    private int _currentVolume;
    private int _originalVolume;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_ui_frame, container, false);
        super.onCreateView(inflater, container, null);

        mainFrag = this;
        final Gson jsonMaker = new Gson();

        messageManager = MessageManager.Instance();
        messageManager.RegisterListener(mainFrag);


        volume = (SeekBar) view.findViewById(R.id.seekBar);
        volume.setVisibility(View.INVISIBLE);

        connectButton = (Button) view.findViewById(R.id.connectBT);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // ***** IntentService Way ****** //

                Intent intent = new Intent(getActivity(), NetworkService.class);
                getActivity().startService(intent);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Once client is connected, give the server some info about client
                            TCPCLIENT.mCountDown.await(3000, TimeUnit.MILLISECONDS);

                            String json = jsonMaker.toJson(new DeviceInfo());
                            DispatchToServer(json);
                        } catch (InterruptedException ie) {
                            ie.getMessage();
                        }
                    }
                }).run();

            }
        });


        play = (Button) view.findViewById(R.id.playBT);
        play.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View view) {

                                        String json = jsonMaker.toJson(new PlayMessageObject());
                                        DispatchToServer(json);
                                    }
                                }
        );

        back = (Button) view.findViewById(R.id.backBT);
        back.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View view) {

                                        String json = jsonMaker.toJson(new BackwardMessageObject());
                                        DispatchToServer(json);

                                    }
                                }
        );

        forward = (Button) view.findViewById(R.id.forwardBT);
        forward.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View view) {

                                           String json = jsonMaker.toJson(new ForwardMessageObject());
                                           DispatchToServer(json);


                                       }

                                   }
        );

        stop = (Button) view.findViewById(R.id.stopBT);
        stop.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View view) {
                                        String json = jsonMaker.toJson(new StopMessageObject());
                                        DispatchToServer(json);
                                    }
                                }
        );

        // Handle SeekBar volume events logic
        final VolumeObject volumeObject = new VolumeObject();
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (progress > _currentVolume) {
                    volumeObject.SetWhichWay("Up");
                    _currentVolume = progress;
                } else if (progress < _currentVolume) {
                    volumeObject.SetWhichWay("Down");
                    _currentVolume = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int stepsDifferent = 0;

                // If the user changed volume up...
                if (_currentVolume > _originalVolume) {
                    Log.d("_currentVolume", "" + _currentVolume);
                    Log.d("_originalVolume", "" + _originalVolume);
                    stepsDifferent = _currentVolume - _originalVolume;
                    Log.d("stepsDifferent", "" + stepsDifferent);
                    _originalVolume = _currentVolume;
                }
                // If the user changed volume down...
                else if (_currentVolume < _originalVolume) {
                    Log.d("_currentVolume", "" + _currentVolume);
                    Log.d("_originalVolume", "" + _originalVolume);
                    stepsDifferent = _originalVolume - _currentVolume;
                    Log.d("stepsDifferent", "" + stepsDifferent);
                    _originalVolume = _currentVolume;
                }

                volumeObject.setProgress(stepsDifferent);

                String json = jsonMaker.toJson(volumeObject);
                DispatchToServer(json);

            }
        });

        tv1 = (TextView) view.findViewById(R.id.tv1);
        tv1.setText("Connect and start playing");

        final ShuffleMessageObject shuffleMessage = new ShuffleMessageObject();
        shuffle = (CheckBox) view.findViewById(R.id.shuffle);
        shuffle.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View view) {
                                           if (shuffle.isChecked()) {
                                               shuffleMessage.setIsShuffleOn(true);
                                               String json = jsonMaker.toJson(shuffleMessage);
                                               DispatchToServer(json);
                                           } else {
                                               shuffleMessage.setIsShuffleOn(false);
                                               String json = jsonMaker.toJson(shuffleMessage);
                                               DispatchToServer(json);

                                           }
                                       }
                                   }
        );

        return view;
    }

    @Override
    public void UpdateInfo(String whatUpdate) {

        switch (whatUpdate) {

            case MessageManager.SONG:
                getSongUpdate();
                break;
            case MessageManager.STATUS:
                getStatusUpdates();
        }
    }

    private void getSongUpdate() {
        final Song song = messageManager.getSongObj();

        final String artistName = song.getArtistName();
        final String title = song.getTitleName();

        tv1.post(new Runnable() {
            @Override
            public void run() {
                tv1.setText(artistName + " - " + title);
            }
        });


    }

    /**
    Gets information about values set at the server side
     */
    private void getStatusUpdates() {


        ServerStatusMessage messageFromServer = messageManager.getServerStatusMessage_Obj();

        // Get shuffle value from server
        final boolean shuffleOnS = messageFromServer.getIsShuffleOn();

        // Get original Volume values from server
        final float maxVolume = messageFromServer.getMaxVolume();
        final float currentVolume = messageFromServer.getCurrentVolume();

        final int maxVolumeFinal = (int) maxVolume;
        final int currentVolumeFinal = (int) currentVolume;

        volume.post(new Runnable() {
            @Override
            public void run() {

                shuffle.setChecked(shuffleOnS);

                _currentVolume = currentVolumeFinal;
                _originalVolume = currentVolumeFinal;

                volume.setMax(maxVolumeFinal);
                volume.setProgress(currentVolumeFinal);
                volume.setVisibility(View.VISIBLE);

            }
        });
    }

    /**
     * Sends a message back to server
     * @param json - the message Object to send as a JSON file
     */
    private void DispatchToServer(String json) {
        //  Toast.makeText(getActivity(), json, Toast.LENGTH_SHORT).show();

        if (messageManager != null && TCPCLIENT.IsConnected) {
            messageManager.sendMessage(json);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart:

                break;

        }

        return false;
    }

    /**
     * Inner class to get device Name for use by server
     */
    private class DeviceInfo {

        @Expose
        private String deviceName = getDevice();

        @Expose
        private String MessageType = "DeviceInfo";

        public DeviceInfo() {
        }

        // Get device name to inform server who connected
        private String getDevice() {
            String Manufacturer = Build.MANUFACTURER;
            String Model = Build.MODEL;

            if (Model.startsWith(Manufacturer)) {
                return capitilize(Model);
            } else {
                return capitilize(Manufacturer) + " " + Model;
            }
        }

        private String capitilize(String s) {
            if (s == null || s.length() == 0) {
                return "";
            }

            char first = s.charAt(0);
            if (Character.isUpperCase(first)) {
                return s;

            } else {
                return Character.toUpperCase(first) + s.substring(1);
            }

        }
    }
}

