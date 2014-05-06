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

import com.loop_to_infinity.play.PlayClient;
import com.loop_to_infinity.play.R;

import Network.TCPCLIENT;


/**
 * Created by Unknown1 on 7/10/13.
 */
public class Play_Main extends Fragment {

    private static final String PLAY = "play";
    private static final String STOP = "stop";
    private static final String BACK = "back";
    private static final String FORWARD = "forward";
    private static final String SetSHUFFLE = "shuffle";
//    private static final String CONNECT = "connect";

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

        final int waitTime = 1000;


        connectButton = (Button) view.findViewById(R.id.connectBT);
        connectButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {

                                                 new connectTask().execute("");
                                                 wait_A_Few(connectButton, waitTime);

                                                 final Thread t = new Thread(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         while (mTCPCLIENT == null) {
                                                             Log.e("sad", "NULL");

                                                             try {
                                                                 Thread.sleep(500);
                                                                 if (mTCPCLIENT != null) {
                                                                     mTCPCLIENT.sendMessage(getDeviceName());
                                                                     break;
                                                                 }
                                                             } catch (Exception e) {
                                                             }
                                                         }

                                                         if (mTCPCLIENT != null) {
                                                             mTCPCLIENT.sendMessage(getDeviceName());
                                                             Log.e("sad", "NOT NULL");
                                                         }

                                                     }


                                                 }
                                                 );
                                                 t.start();
                                             }
                                         }
        );

        play = (Button) view.findViewById(R.id.playBT);
        play.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View view) {
                                        if (mTCPCLIENT != null) {
                                            mTCPCLIENT.sendMessage(PLAY);
                                            wait_A_Few(play, waitTime);
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
                                            wait_A_Few(back, waitTime);
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
                                               wait_A_Few(forward, waitTime);


                                           }

                                       }

                                   }
        );

        stop = (Button) view.findViewById(R.id.stopBT);
        stop.setOnClickListener(new View.OnClickListener()

                                {
                                    @Override
                                    public void onClick(View view) {
                                        if (mTCPCLIENT != null) {
                                            mTCPCLIENT.sendMessage(STOP);
                                            wait_A_Few(stop, waitTime);
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
                                               wait_A_Few(shuffle, waitTime);
                                           }
                                       }
                                   }
        );

        return view;
    }

    // A timer function to disable button

    private void wait_A_Few(Button b, int time) {

        final Button bt = b;
        final int timeWait = time;

        // Disable button
        bt.setEnabled(false);


        // Wait X seconds and enable it again
        Thread wait_A_Second = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(timeWait);
                        getActivity().runOnUiThread((new Runnable() {
                            public void run() {
                                bt.setEnabled(true);
                            }
                        }));


                    }
                } catch (InterruptedException ex) {
                    Log.d("Thread", "Button" + bt.getText() + " Couldn't be disabled");

                }
            }
        };

        wait_A_Second.start();

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
