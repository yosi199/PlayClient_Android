package Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.gson.Gson;
import com.loop_to_infinity.play.R;

import Messages.BackwardMessageObject;
import Messages.ForwardMessageObject;
import Messages.MessageManager;
import Messages.PlayMessageObject;
import Messages.ShuffleMessageObject;
import network.TCPCLIENT;

/**
 * Created by Unknown on 28/06/2014.
 */
public class MediaControlsComponent extends Fragment {

    Button play;
    Button back;
    Button forward;
    CheckBox shuffle;

    private MessageManager messageManager;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_controls_frame, container, false);
        super.onCreateView(inflater, container, null);

        final Gson jsonMaker = new Gson();
        messageManager = MessageManager.Instance();

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

    /**
     * Sends a message back to server
     *
     * @param json - the message Object to send as a JSON file
     */
    private void DispatchToServer(String json) {
        //  Toast.makeText(getActivity(), json, Toast.LENGTH_SHORT).show();

        if (messageManager != null && TCPCLIENT.IsConnected) {
            messageManager.sendMessage(json);
        }
    }
}
