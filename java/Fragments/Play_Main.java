package Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.loop_to_infinity.play.R;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import Interfaces.IListener;
import Messages.CurrentlyPlayingMessageObject;
import Messages.MessageManager;
import Messages.ServerStatusMessage;
import Messages.Song;
import network.NetworkService;
import network.TCPCLIENT;


/**
 * Created by Unknown1 on 7/10/13.
 */
public class Play_Main extends Fragment implements IListener {

    private static final String TAG = "Play_Main_Fragment";
    public static CountDownLatch mCountDown = new CountDownLatch(1);

    private Typeface roboto;

    // Views
    private TextView tv1;
    private TextView pullToConnect;
    private Button connectButton;
    private SwipeRefreshLayout mSwipeLayout;


    // Object Instances
    private Play_Main mainFrag;
    private MessageManager messageManager;

    // Animation
    private Animation fadeIn;
    private Animation fadeOut;

    // Fields
    private String isConnectedText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_ui_frame, container, false);
        super.onCreateView(inflater, container, null);

        mainFrag = this;
        final Gson jsonMaker = new Gson();

        roboto = Typeface.createFromAsset(getActivity().getAssets(), "fonts/robotot.ttf");

        fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);

        messageManager = MessageManager.Instance();
        // Register as a general listener
        messageManager.RegisterListener(mainFrag);
        // Register as the UI to interact with
        messageManager.registerUI(mainFrag);

        pullToConnect = (TextView) view.findViewById(R.id.pullToConnect);
        pullToConnect.setTypeface(roboto);

        if (TCPCLIENT.IsConnected) {
            pullToConnect.setText(R.string.connected);
            String json = jsonMaker.toJson(new CurrentlyPlayingMessageObject());
            DispatchToServer(json);

        }

        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                              @Override
                                              public void onRefresh() {

                                                  pullToConnect.setText(R.string.connecting);
                                                  startConnecting();

                                                  Log.d("refreshed", "refreshed");
                                              }


                                          }
        );


        tv1 = (TextView) view.findViewById(R.id.tv1);
        tv1.setTypeface(roboto);
        tv1.setText("Connect and start playing");


        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void UpdateInfo(String whatUpdate) {

        final String what = whatUpdate;

        new Thread(new Runnable() {
            @Override
            public void run() {

                switch (what) {

                    case MessageManager.SONG:
                        getSongUpdate();
                        break;
                    case MessageManager.STATUS:
                        getStatusUpdates();
                }
            }
        }).start();
    }


    /**
     * Listener* Gets the song json sent from server
     */

    private void getSongUpdate() {
        final Song song = messageManager.getSongObj();
        final String artistName = song.getArtistName();
        final String title = song.getTitleName();


        tv1.post(new Runnable() {
            @Override
            public void run() {

                float xSize = tv1.getX();
                float ySize = tv1.getY();

                float xScale = tv1.getScaleX();
                float yScale = tv1.getScaleY();

                final TranslateAnimation xAnimation = new TranslateAnimation(xSize, 10000f, 0f, 0f);
                final ScaleAnimation sAnimation = new ScaleAnimation(0, xScale, 0, yScale);


                xAnimation.setDuration(150);
                xAnimation.setFillAfter(true);
                xAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        tv1.setText(artistName + " - " + title);
                        tv1.startAnimation(sAnimation);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                tv1.startAnimation(xAnimation);


                sAnimation.setDuration(150);
                sAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


            }
        });


    }

    /**
     * Gets information about values set at the server side
     */
    private void getStatusUpdates() {
        ServerStatusMessage messageFromServer = messageManager.getServerStatusMessage_Obj();
    }


    /**
     * Sends a message back to server
     *
     * @param json - the message Object to send as a JSON file
     */
    private void DispatchToServer(final String json) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                if (messageManager != null && TCPCLIENT.IsConnected) {
                    messageManager.sendMessage(json);
                }
            }
        }).start();

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

    private boolean startConnecting() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    isConnectedText = "";

                    // If the server is not connected, start the service and connect
                    if (!TCPCLIENT.IsConnected) {


                        // ***** IntentService  ****** //
                        Intent intent = new Intent(getActivity(), NetworkService.class);
                        getActivity().startService(intent);


                        // If server connected - the bool will be true
                        boolean waitedSuccessfully = mCountDown.await(5000, TimeUnit.MILLISECONDS);

                        if (waitedSuccessfully) {
                            isConnectedText = getString(R.string.connected);
                            pullToConnect.post(new Runnable() {
                                @Override
                                public void run() {
                                    // Stop pull-to-refresh
                                    pullToConnect.setText(isConnectedText);
                                    mSwipeLayout.setRefreshing(false);

                                    // Once client is connected, give the server some info about client
                                    final Gson jsonMaker = new Gson();

                                    String json = jsonMaker.toJson(new DeviceInfo());
                                    DispatchToServer(json);
                                }
                            });


                        } else if (!waitedSuccessfully) {
                            isConnectedText = getString(R.string.failed_to_connect);
                            pullToConnect.post(new Runnable() {
                                @Override
                                public void run() {
                                    // Stop pull-to-refresh
                                    pullToConnect.setText(isConnectedText);
                                    mSwipeLayout.setRefreshing(false);
                                }
                            });

                        }
                    } else if (TCPCLIENT.IsConnected) {

                        pullToConnect.post(new Runnable() {
                            @Override
                            public void run() {
                                isConnectedText = getString(R.string.connected);
                                pullToConnect.setText(isConnectedText);
                                mSwipeLayout.setRefreshing(false);
                            }
                        });
                    }

                } catch (InterruptedException e) {
                }

            }
        }).start();

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

