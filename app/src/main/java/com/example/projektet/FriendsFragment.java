package com.example.projektet;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class FriendsFragment extends Fragment {
    List<MemberData> fListDatabase;
    ListView friendsList;
    FirebaseAuth mAuth;
    ArrayAdapter<String> arrayAdapter;
    FirebaseDatabase database;
    FirebaseUser currentUser;
    private Listener listener;
    String CHANNEL_ID = "1";
    View layout;

    public interface Listener {
        void switchToChat(String name);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if(context instanceof FriendsFragment.Listener){
            this.listener = (FriendsFragment.Listener) context;
        }
        else{
            throw new RuntimeException(context.toString() + "must implement listener interface.");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_friends, container, false);
        fListDatabase = new ArrayList<MemberData>();
        friendsList = (ListView) layout.findViewById(R.id.friends_List);
        arrayAdapter = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_list_item_1);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/" + currentUser.getDisplayName() +"/Friends");

        createNotificationChannel();

        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 String name = (String) friendsList.getItemAtPosition(position).toString().trim();
                 listener.switchToChat(name);
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fListDatabase.clear();
                arrayAdapter.clear();
                MemberData friend = null;
               for(DataSnapshot child : snapshot.getChildren()){
                  friend = new MemberData(child.getValue().toString());
                  fListDatabase.add(friend);
               }
                for(MemberData f : fListDatabase){
                    arrayAdapter.add(f.getNickName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        notificationListener();

        friendsList.setAdapter(arrayAdapter);
        return layout;
    }

    private void notificationListener(){
        DatabaseReference notification = database.getReference("users/" + currentUser.getDisplayName() +"/Messages");
        notification.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(layout.getContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.baseline_notification_important_white_18dp)
                        .setContentTitle("You have a new message!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(layout.getContext());

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(001, builder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        fListDatabase.clear();
        arrayAdapter.clear();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}