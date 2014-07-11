package Fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.google.gson.Gson;
import com.loop_to_infinity.play.R;

import Interfaces.IListener;
import Messages.BackwardMessageObject;
import Messages.ForwardMessageObject;
import Messages.MessageManager;
import Messages.PlayMessageObject;
import Messages.ServerStatusMessage;
import Messages.ShuffleMessageObject;
import Messages.VolumeObject;
import network.TCPCLIENT;

/**
 * Created by Unknown on 28/06/2014.
 */
public class MediaControlsComponent extends Fragment implements IListener {

    private Button play;
    private Button back;
    private Button forward;
    private CheckBox shuffle;
    private SeekBar volume;

    private String TAG = "mediaContols";

    private MessageManager messageManager;

    private Typeface roboto;
    private int _currentVolume;
    private int _originalVolume;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_controls_frame, container, false);
        super.onCreateView(inflater, container, null);

        roboto = Typeface.createFromAsset(getActivity().getAssets(), "fonts/robotot.ttf");

        final Gson jsonMaker = new Gson();
        messageManager = MessageManager.Instance();
        messageManager.RegisterListener(this);

        play = (Button) view.findViewById(R.id.play_pause);
        play.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View view) {

                                        String json = jsonMaker.toJson(new PlayMessageObject());
                                        DispatchToServer(json);
                                    }
                                }
        );

        back = (Button) view.findViewById(R.id.rewind);
        back.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View view) {

                                        String json = jsonMaker.toJson(new BackwardMessageObject());
                                        DispatchToServer(json);

                                    }
                                }
        );

        forward = (Button) view.findViewById(R.id.forward);
        forward.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View view) {

                                           String json = jsonMaker.toJson(new ForwardMessageObject());
                                           DispatchToServer(json);


                                       }

                                   }
        );

        final ShuffleMessageObject shuffleMessage = new ShuffleMessageObject();
        shuffle = (CheckBox) view.findViewById(R.id.shuffle);
        shuffle.setTypeface(roboto);
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

        volume = (SeekBar) view.findViewById(R.id.seekBar);

        // Handle SeekBar volume events logic
        final VolumeObject volumeObject = new VolumeObject();
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()

                                          {
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
                                          }
        );

        return view;
    }

    /**
     * Sends a message back to server
     *
     * @param json - the message Object to send as a JSON file
     */
    private void DispatchToServer(String json) {
        //  Toast.makeText(getActivity(), json, Toast.LENGTH_SHORT).show();

        if (messageManager != null && TCPCLIENT.IsConnected) {
            Log.d("DispatchToServer", "Dispatched");
            messageManager.sendMessage(json);
        }
    }

    @Override
    public void UpdateInfo(String whatUpdate) {

        final String what = whatUpdate;
        final ServerStatusMessage messageFromServer = messageManager.getServerStatusMessage_Obj();

        new Thread(new Runnable() {
            @Override
            public void run() {

                switch (what) {
                    case MessageManager.STATUS:


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

                                //      shuffle.setChecked(shuffleOnS);

                                _currentVolume = currentVolumeFinal;
                                _originalVolume = currentVolumeFinal;

                                volume.setMax(maxVolumeFinal);
                                volume.setProgress(currentVolumeFinal);

                            }
                        });

                        break;

                }

            }
        }).start();
    }

}
