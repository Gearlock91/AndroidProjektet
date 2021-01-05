package com.example.projektet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static android.content.ContentValues.TAG;


public class ChatFragment extends Fragment {

    EditText message;
    ImageButton sendButton;
    DatabaseReference myRef;
    FirebaseAuth myAuth;
    String currentUser;
    MessageAdapter receivedMessagesAdapter;
    ArrayList<CryptoMessage> messages;
    ListView messageView;
    String fromSender;
    FirebaseDatabase database;
    String privateKey;
    PrivateKey pKey;
    CryptoMessage saveSentMessage;

    int id = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle test = getArguments();
        fromSender = test.getString("name");
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_chat, container, false);
        sendButton = (ImageButton) layout.findViewById(R.id.send_button);
        message = (EditText) layout.findViewById(R.id.message_text);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String myMessage = message.getText().toString();

                     myRef = database.getReference("users/" + fromSender + "/Friends/" + currentUser);

                     myRef.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             if(snapshot.exists()){
                                 myRef = database.getReference("users/" + fromSender + "/Friends/" + currentUser + "/PubKey");
                                 myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                         Encryption encryption = new Encryption();
                                         byte[] publicBytes = Base64.getDecoder().decode(snapshot.getValue().toString());
                                         X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                                         try {
                                             KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                                             PublicKey pubKey = keyFactory.generatePublic(keySpec);
                                             encryption.encryptMessage(myMessage.getBytes(), pubKey);
                                         } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                                             e.printStackTrace();
                                         }
                                         myRef = database.getReference("users/" + fromSender + "/Messages/" + currentUser +"/");
                                         Log.d(TAG, encryption.getMessageEncrypted());
                                         myRef.push().setValue(encryption.getMessageEncrypted());
                                     }
                                     @Override
                                     public void onCancelled(@NonNull DatabaseError error) {

                                     }
                                 });
                                 CryptoMessage save = new CryptoMessage(myMessage, true);
                                 receivedMessagesAdapter.add(save);
                                 messageView.smoothScrollToPosition(receivedMessagesAdapter.getCount());
                                 messages.add(save);
                             }else{
                                 Toast.makeText(layout.getContext(), "User needs to add you as a friend first.", Toast.LENGTH_LONG).show();
                             }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {

                         }
                     });
                message.setText("");
            }
        });

        myAuth = FirebaseAuth.getInstance();
        currentUser = myAuth.getCurrentUser().getDisplayName();
        receivedMessagesAdapter = new MessageAdapter(layout.getContext());
        messages = new ArrayList<CryptoMessage>();
        messageView = layout.findViewById(R.id.messages_view);
        readOnce();
        if(savedInstanceState != null){
            messages.clear();
            receivedMessagesAdapter.clear();
            messages = savedInstanceState.getParcelableArrayList("messages");
            for(CryptoMessage m : messages) {
                receivedMessagesAdapter.add(m);
            }
        }
        messageView.setAdapter(receivedMessagesAdapter);
        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
            saveInstanceState.clear();
            saveInstanceState.putParcelableArrayList("messages", messages);
            super.onSaveInstanceState(saveInstanceState);

    }

    private void readOnce(){
        myRef = database.getReference("users/"+ currentUser +"/Messages/"+ fromSender );
        SQLiteOpenHelper sqlCryptoHelper = new SqlCryptoHelper(getContext());
        SQLiteDatabase db = sqlCryptoHelper.getReadableDatabase();
        Cursor friendCursor;

        friendCursor = db.query("CRYPTOLEDGER", new String[]{"FRIEND","PRIVATE_KEY"}, ("FRIEND=" + "'"+fromSender+"'"),null,null,null,null);

        if(friendCursor.moveToFirst()){
            privateKey = friendCursor.getString(1);
            friendCursor.close();
            db.close();
        }

        Encryption encryption = new Encryption();
        byte[] privateBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            pKey = keyFactory.generatePrivate(keySpec);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                encryption.decryptMessage(snapshot.getValue().toString().getBytes(), pKey);
                receivedMessagesAdapter.add(new CryptoMessage(encryption.getMessageDecrypted(), fromSender,false));
                messages.add(new CryptoMessage(encryption.getMessageDecrypted(), fromSender,false));
                messageView.smoothScrollToPosition(receivedMessagesAdapter.getCount());
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        myRef = database.getReference("users/"+ currentUser +"/Messages/"+ fromSender );
        myRef.removeValue();
    }

}