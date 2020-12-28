package com.example.projektet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scaledrone.lib.Message;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class ChatFragment extends Fragment {

    ImageButton sendButton;
    DatabaseReference myRef;
    FirebaseAuth myAuth;
    String currentUser;
    ArrayAdapter<String> receivedMessagesAdapter;
    List<CryptoMessage> messageFromUser;
    ListView messageView;


    @Override
    public void onCreate(Bundle savedOnInstanceState) {
        super.onCreate(savedOnInstanceState);
        myAuth = FirebaseAuth.getInstance();
        currentUser = myAuth.getCurrentUser().getDisplayName();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/"+ currentUser +"/Messages/Larsson/" );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_chat, container, false);
        // Inflate the layout for this fragment

        receivedMessagesAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1);
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
                CryptoMessage messageToList = null;
                for(DataSnapshot message : snapshot.getChildren()){
                    Log.d(TAG, message.getValue().toString());
                    messageToList = new CryptoMessage(message.getValue().toString());
                    messageFromUser.add(messageToList);
                }

                for(CryptoMessage m : messageFromUser){
                    receivedMessagesAdapter.add(m.getText());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}