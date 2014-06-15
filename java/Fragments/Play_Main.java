package Fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loop_to_infinity.play.PlayClient;
import com.loop_to_infinity.play.R;

import Messages.PlayMessageObject;
import Messages.StopMessageObject;
import Network.TCPCLIENT;


/**
 * Created by Unknown1 on 7/10/13.
 */
public class Play_Main extends Fragment {


    // static Constants
    private static final String PLAY = "play";
    private static final String STOP = "stop";
    private static final String BACK = "back";
    private static final String FORWARD = "forward";
    private static final String SetSHUFFLE = "shuffle";

    private static final String TAG = "Play_Main_Fragment";
//    private static final String CONNECT = "connect";

    // Views and fields
    private TextView tv1;
    private TCPCLIENT mTCPCLIENT = null;
    private Button connectButton;
    private Button play;
    private Button back;
    private Button forward;
    private Button stop;
    private CheckBox shuffle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_ui_frame, container, false);
        super.onCreateView(inflater, container, null);

        final Gson jsonMaker = new Gson();
//        final PlayMessageObject defaultMessages = new PlayMessageObject();

        connectButton = (Button) view.findViewById(R.id.connectBT);
        connectButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 // Disable button until a connection is made
                                                 connectButton.setEnabled(false);
                                                 // Create a server instance and connect
                                                 new connectTask().execute("");
                                                 // Wait until the client is connected and then release the button
                                                 new Thread(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         try {
                                                             TCPCLIENT.mCountDown.await();
                                                         } catch (InterruptedException ie) {
                                                             ie.getMessage();
                                                         }

                                                         // when a connection is made, enable the button again
                                                         connectButton.post(new Runnable() {
                                                             @Override
                                                             public void run() {
                                                                 connectButton.setEnabled(true);
                                                             }
                                                         });

                                                     }
                                                 }).start();
                                             }
                                         }
        );

        play = (Button) view.findViewById(R.id.playBT);
        play.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View view) {

                                        String json = jsonMaker.toJson(new PlayMessageObject());
                                        Toast.makeText(getActivity(), json, Toast.LENGTH_SHORT).show();

                                        if (mTCPCLIENT != null) {
                                            mTCPCLIENT.sendMessage(json);
                                        }
                                    }
                                }
        );

        back = (Button) view.findViewById(R.id.backBT);
        back.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View view) {
                                        if (mTCPCLIENT != null) {

                                            mTCPCLIENT.sendMessage(BACK);

                                        }
                                    }
                                }
        );

        forward = (Button) view.findViewById(R.id.forwardBT);
        forward.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View view) {
                                           if (mTCPCLIENT != null) {
                                               mTCPCLIENT.sendMessage(FORWARD);


                                           }

                                       }

                                   }
        );

        stop = (Button) view.findViewById(R.id.stopBT);
        stop.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View view) {
                                        String json = jsonMaker.toJson(new StopMessageObject());
                                        Toast.makeText(getActivity(), json, Toast.LENGTH_SHORT).show();
                                        if (mTCPCLIENT != null) {

                                            mTCPCLIENT.sendMessage(STOP);
                                        }
                                    }
                                }
        );

        tv1 = (TextView) view.findViewById(R.id.tv1);
        tv1.setText("Connect and start playing");

        shuffle = (CheckBox) view.findViewById(R.id.shuffle);
        shuffle.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View view) {
                                           if (mTCPCLIENT != null) {
                                               mTCPCLIENT.sendMessage(SetSHUFFLE);
                                           }
                                       }
                                   }
        );

        return view;
    }


    public class connectTask extends AsyncTask<String, String, TCPCLIENT> {

        @Override
        protected TCPCLIENT doInBackground(String... message) {

            //we create a TCPClient object and
            mTCPCLIENT = new TCPCLIENT(new TCPCLIENT.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);

                }
            });
            mTCPCLIENT.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(PlayClient.ctx, "" + values[0], Toast.LENGTH_SHORT).show();
            tv1.setText(values[0]);
            Log.d("Server", "" + values[0]);
        }
    }

    // Get device name to inform server who connected
    private String getDeviceName() {
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
