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

import Interfaces.IListener;
import Interfaces.ISubject;
import Messages.MessageManager;
import Messages.ServerStatusMessage;

/**
 * Created by Unknown1 on 7/12/13.
 */
public class TCPCLIENT implements ISubject {

    // Server connection info
    public static final String SERVERIP = "10.0.0.5";
    public static final int SERVERPORT = 5555;
    // Server readers/writers
    private boolean mRun = false;
    private PrintWriter out;
    private BufferedReader in;
    // Registered listeners to pass messages to UI
    private OnMessageReceived mMessageListener = null;
    private IListener mUpdatesListener;
    // Some instance variables
    private String serverMessage;
    private MessageManager messageHandler = null;
    private ServerStatusMessage _statusMessageObject;

    public static CountDownLatch mCountDown = new CountDownLatch(1);


    public TCPCLIENT(OnMessageReceived listener) {
        mMessageListener = listener;

    }

    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            out.println(message + "<EOF>");
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

            }


            try {

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                mCountDown.countDown();

                while (mRun) {
                    serverMessage = in.readLine();
                    Log.d("dasd", serverMessage);
                    messageHandler = MessageManager.Instance();
                    Object obj = messageHandler.figureMessageType(serverMessage, mMessageListener);

                    // Check to see if message replied with a status update
                    if (obj instanceof ServerStatusMessage) {
                        _statusMessageObject = (ServerStatusMessage) obj;
                        // Tell UI to GET info it wants
                        NotifyUpdates();

                    }


                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        //      mMessageListener.messageReceived(serverMessage);


                    }
                    serverMessage = null;


                }

            } catch (Exception e) {


                Log.e("TCP", "S: Error - " + e.getMessage());
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

    // Returns the server status updates object so UI can extract data from.
    public ServerStatusMessage getStatusUpdate() {
        return _statusMessageObject;
    }

    @Override
    public void RegisterListener(IListener listener) {
        mUpdatesListener = listener;
    }

    @Override
    public void NotifyUpdates() {
        mUpdatesListener.UpdateInfo();

    }

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}
