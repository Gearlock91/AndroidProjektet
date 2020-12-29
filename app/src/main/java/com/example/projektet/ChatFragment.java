package com.example.projektet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class ChatFragment extends Fragment {

    EditText message;
    ImageButton sendButton;
    DatabaseReference myRef;
    FirebaseAuth myAuth;
    String currentUser;
    MessageAdapter receivedMessagesAdapter;
    List<CryptoMessage> messageFromUser;
    ListView messageView;
    String fromSender;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle test = getArguments();
        fromSender = test.getString("name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_chat, container, false);
        // Inflate the layout for this fragment
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        sendButton = (ImageButton) layout.findViewById(R.id.send_button);
        message = (EditText) layout.findViewById(R.id.message_text);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = database.getReference("users/" + fromSender + "Messages/" + currentUser);
                myRef.setValue(message.getText().toString());
            }
        });

        myAuth = FirebaseAuth.getInstance();
        currentUser = myAuth.getCurrentUser().getDisplayName();

        myRef = database.getReference("users/"+ currentUser +"/Messages/"+ fromSender );
        receivedMessagesAdapter = new MessageAdapter(layout.getContext());
        messageFromUser = new ArrayList<CryptoMessage>();
        messageView = layout.findViewById(R.id.messages_view);
        readDatabase();
        messageView.setAdapter(receivedMessagesAdapter);
        return layout;
    }

    private void readDatabase(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageFromUser.clear();
                receivedMessagesAdapter.clear();
                CryptoMessage messageToList = null;
                for(DataSnapshot message : snapshot.getChildren()){
                    messageToList = new CryptoMessage(message.getValue().toString(),fromSender,false);
                    messageFromUser.add(messageToList);
                }
                for(CryptoMessage m : messageFromUser){
                    receivedMessagesAdapter.add(m);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}