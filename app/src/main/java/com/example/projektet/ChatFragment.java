package com.example.projektet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scaledrone.lib.Message;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;


public class ChatFragment extends Fragment implements RoomListener {

    ImageButton sendButton;
    DatabaseReference myRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_chat, container, false);
        // Inflate the layout for this fragment

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");

        sendButton = (ImageButton) layout.findViewById(R.id.send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(layout);
            }
        });

        return layout;
    }

    @Override
    public void onOpen(Room room) {

    }

    @Override
    public void onOpenFailure(Room room, Exception ex) {

    }

    @Override
    public void onMessage(Room room, Message message) {

    }

    public void sendMessage(View view) {
//        EditText message = view.findViewById(R.id.message_text);
//        String messageToSend = message.toString().trim();
//        if (message.length() > 0) {
//            CryptoMessage myMessage = new CryptoMessage(messageToSend,);
//            myRef.child(String.valueOf(id)).setValue(myMessage);
//            message.getText().clear();
//        }
    }
}