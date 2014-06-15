package Network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Unknown1 on 7/12/13.
 */
public class TCPCLIENT {

    // Server connection info
    public static final String SERVERIP = "10.0.0.5";
    public static final int SERVERPORT = 5555;
    // Server readers/writers
    private boolean mRun = false;
    private PrintWriter out;
    private BufferedReader in;
    // Registered listener to pass messages to UI
    private OnMessageReceived mMessageListener = null;
    private String serverMessage;

    public static CountDownLatch mCountDown;



    public TCPCLIENT(OnMessageReceived listener) {
        mMessageListener = listener;
        mCountDown = new CountDownLatch(1);

    }

    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public void stopClient() {
        mRun = false;
    }

    public void run() {
        mRun = true;

        try {

            InetAddress serverAddress = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");

            Socket socket = new Socket(serverAddress, SERVERPORT);

            if (socket.isConnected()) {
                Log.e("TCP Client", "Connected!");
                mCountDown.countDown();

            }

            else if (!socket.isConnected()){
                mCountDown.countDown();
            }

            try {

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (mRun) {
                    serverMessage = in.readLine();

                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);


                    }
                    serverMessage = null;


                }

            } catch (Exception e) {

                Log.e("TCP", "S: Error");
                mMessageListener.messageReceived("Disconnected");
                run();


            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}
