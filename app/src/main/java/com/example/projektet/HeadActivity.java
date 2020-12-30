package com.example.projektet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HeadActivity extends AppCompatActivity {

    String CHANNEL_ID = "1";
    FirebaseDatabase database;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    List<MemberData> fListDatabase;
    ListView friendsList;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        createNotificationChannel();
        notificationListener();

        fListDatabase = new ArrayList<MemberData>();
        friendsList = (ListView) findViewById(R.id.friends_List);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/" + currentUser.getDisplayName() +"/Friends");


        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) friendsList.getItemAtPosition(position).toString().trim();
                Bundle nameBundle = new Bundle();
                nameBundle.putString("name", name);
                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(nameBundle);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.container_fragment, chatFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
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

        friendsList.setAdapter(arrayAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem addFriend = menu.findItem(R.id.action_addFriend);
        //MenuItem startChat = menu.findItem(R.id.action_sendMessage);

        addFriend.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AddFriendFragment addFriendFragment = new AddFriendFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragmentArea, addFriendFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack("addFriend");
                ft.commit();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }


    private void notificationListener(){
        DatabaseReference notification = database.getReference("users/" + currentUser.getDisplayName() +"/Messages");
        notification.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()){
                    DatabaseReference notice = database.getReference("users/" + currentUser.getDisplayName() +"/Messages/" + child.getKey());
                    notice.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(HeadActivity.this, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.baseline_notification_important_white_18dp)
                                    .setContentTitle("You have a new message!")
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(HeadActivity.this);

                            // notificationId is a unique int for each notification that you must define
                            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}