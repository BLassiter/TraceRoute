package com.brandonlassiter.traceroute;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by brandon on 6/11/15.
 */
public class MainFragment extends Fragment implements View.OnClickListener{

    ParseObject currentRoom;
    Button createRoomButton;
    ProgressDialog dialog;
    TextView label;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createRoomButton = (Button)getView().findViewById(R.id.createRoomButton);
        createRoomButton.setOnClickListener(this);

        label = (TextView)getView().findViewById(R.id.label);
    }

    @Override
    public void onClick(View v) {

        dialog = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Creating Room");
        dialog.show();

        try {

            final ParseObject room = new ParseObject("GameRoom");

            ArrayList<String> users = new ArrayList<>();

            users.add( ParseUser.getCurrentUser().getUsername());

            room.put("users", Arrays.asList(users));

            room.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if(e == null) {

                        Toast.makeText(getActivity(), "Room " + room.getObjectId() + " Created Successfully", Toast.LENGTH_LONG).show();
                        currentRoom = room;
                        dialog.hide();

                        label.setText("Room ID: " + room.getObjectId());
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
