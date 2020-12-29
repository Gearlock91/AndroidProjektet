package com.example.projektet;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;


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
    FirebaseDatabase database;
    int id = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle test = getArguments();
        fromSender = test.getString("name");
        database = FirebaseDatabase.getInstance();
        readSentMessage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_chat, container, false);
        // Inflate the layout for this fragment
        sendButton = (ImageButton) layout.findViewById(R.id.send_button);
        message = (EditText) layout.findViewById(R.id.message_text);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = database.getReference("users/" + fromSender + "/Messages/" + currentUser +"/");
                myRef.push().setValue(message.getText().toString());
                //myRef.setValue(message.getText().toString());
                receivedMessagesAdapter.add(new CryptoMessage(message.getText().toString(), true));
                message.setText("");
            }
        });

        myAuth = FirebaseAuth.getInstance();
        currentUser = myAuth.getCurrentUser().getDisplayName();
        receivedMessagesAdapter = new MessageAdapter(layout.getContext());
        messageFromUser = new ArrayList<CryptoMessage>();
        messageView = layout.findViewById(R.id.messages_view);
        readMessageFromSender();
        readOnce();
        messageView.setAdapter(receivedMessagesAdapter);
        return layout;
    }

    @Override
    public void onStart(){
        super.onStart();
        receivedMessagesAdapter.clear();
    }


    private void readOnce(){
        myRef = database.getReference("users/"+ currentUser +"/Messages/"+ fromSender );
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                receivedMessagesAdapter.add(new CryptoMessage(snapshot.getValue().toString(), fromSender,false));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readSentMessage(){
        List<String> sentMessages = new ArrayList<String>();
        myRef = database.getReference("users/" + fromSender + "/Messages/" + currentUser);

        ValueEventListener sm = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()){
                    sentMessages.add(child.toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        myRef.addValueEventListener(sm);
        myRef.removeEventListener(sm);

    }

    private void readMessageFromSender(){
        myRef = database.getReference("users/"+ currentUser +"/Messages/"+ fromSender );
        ValueEventListener firstRun = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageFromUser.clear();
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
        };

        myRef.addValueEventListener(firstRun);
        myRef.removeEventListener(firstRun);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        myRef = database.getReference("users/"+ currentUser +"/Messages/"+ fromSender );
        myRef.removeValue();
    }

}