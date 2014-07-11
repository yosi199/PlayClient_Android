package network;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import Fragments.Play_Main;
import Messages.KillAndRestartMessageObject;
import Messages.MessageManager;

/**
 * Created by Unknown1 on 7/12/13.
 */
public class TCPCLIENT {

    // Server connection info
    public static final String SERVERIP = "10.0.0.5";
    public static final int SERVERPORT = 5555;
    public static Boolean IsConnected = false;
    // Server readers/writers
    private boolean mRun = false;
    private PrintWriter out;
    private BufferedReader in;

    // Some instance variables
    private String serverMessage;
    private MessageManager messageHandler = null;


    public TCPCLIENT() {

        // get the messageHandler instance and pass the message
        messageHandler = MessageManager.Instance();
        messageHandler.registerTcpClient(this);
    }

    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {

            // inform the server and kill the connection
            if (message.contains("CloseSelf")) {
                Gson gson = new Gson();
                String killJSON = gson.toJson(new KillAndRestartMessageObject());

                out.println(killJSON + "<EOF>");
                out.flush();

                stopClient();

            } else {
                Log.d("SendFromServer", message);

                out.println(message + "<EOF>");
                out.flush();
            }
        }
    }

    public void stopClient() {
        mRun = false;
        IsConnected = false;
    }

    public void run() {
        mRun = true;

        try {

            InetAddress serverAddress = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");

            Socket socket = new Socket(serverAddress, SERVERPORT);

            if (socket.isConnected()) {
                Log.e("TCP Client", "Connected!");
                IsConnected = true;
                messageHandler.registerTcpClient(this);
            }


            try {

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Play_Main.mCountDown.countDown();


                while (mRun) {
                    serverMessage = in.readLine();
                    Log.d("dasd", serverMessage);

                    messageHandler.figureMessageType(serverMessage);
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
            IsConnected = false;
            e.printStackTrace();
        } catch (IOException e) {
            IsConnected = false;
            e.printStackTrace();
        }
    }


}
