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
import androidx.fragment.app.FragmentManager;
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
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

/**
 * Aktiviteten som hanterar alla användarens aktiviteter. Så som
 * att byta till respektive chatt-fragment {@link ChatFragment} eller gå till fragmentet
 * som lägger till användare {@link AddFriendFragment}.
 * @author Andreas Roghe, Sofia Ågren
 * @version 2020-01-05
 */

public class HeadActivity extends AppCompatActivity implements FriendsListAdapter.Listener {

    private String CHANNEL_ID = "1";
    private FirebaseDatabase database;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private List<MemberData> fListDatabase;
    private ListView friendsList;
    private ChatFragment chatFragment;
    private AddFriendFragment addFriendFragment;
    private FriendsListAdapter friendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head);

        //Sparar ner fragment för att kunna återskapa om skärmen roteras.
        if (savedInstanceState != null) {
            chatFragment = (ChatFragment) getSupportFragmentManager().getFragment(savedInstanceState, "chatFragment");
            addFriendFragment = (AddFriendFragment) getSupportFragmentManager().getFragment(savedInstanceState, "addFriendFragment");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        createNotificationChannel();
        notificationListener();

        fListDatabase = new ArrayList<MemberData>();
        friendsList = (ListView) findViewById(R.id.friends_List);

        friendAdapter = new FriendsListAdapter(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/" + currentUser.getDisplayName() + "/Friends");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fListDatabase.clear();
                friendAdapter.clear();
                MemberData friend = null;
                for (DataSnapshot child : snapshot.getChildren()) {
                    friend = new MemberData(child.getKey());
                    fListDatabase.add(friend);
                }
                for (MemberData f : fListDatabase) {
                    friendAdapter.add(f);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        friendsList.setAdapter(friendAdapter);
    }

    /**
     * Lägger till en knapp i meny-baren för att kunna läggatill vänner.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem addFriend = menu.findItem(R.id.action_addFriend);

        addFriend.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addFriendFragment = new AddFriendFragment();
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

    /**
     * Skapar en notifications lyssnare i syftet att lyssna på meddelanden som kommer
     * in till användaren.
     */
    private void notificationListener() {
        DatabaseReference notification = database.getReference("users/" + currentUser.getDisplayName() + "/Messages");
        notification.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    DatabaseReference notice = database.getReference("users/" + currentUser.getDisplayName() + "/Messages/" + child.getKey());
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

    /**
     * Enligt Android-developers behövdes de skapas en ny notificationskanal
     * för att lyssna på händelser och skicka till Androids notifikationshanterare.
     * Koden är tagen från {@see <a href="https://developer.android.com/training/notify-user/channels">Android-Developer: Notify users</a>}
     */
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

    /**
     * Funktionen implementars utifrån det interface som deklareades i
     * {@link FriendsListAdapter}.
     * @param position
     */
    @Override
    public void startChat(int position) {
        MemberData friendMember = (MemberData) friendsList.getItemAtPosition(position);
        Bundle nameBundle = new Bundle();
        nameBundle.putString("name", friendMember.getNickName());
        if (chatFragment == null) {
            chatFragment = new ChatFragment();
        }
        chatFragment.setArguments(nameBundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container_fragment, chatFragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    /**
     * Sparar ner fragmenten i syftet att hantera rotationsöverlevnad.
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
        super.onSaveInstanceState(outState);
        //Save the fragment's instance
        if (chatFragment != null) {
            if (chatFragment.isVisible()) {
                getSupportFragmentManager().putFragment(outState, "chatFragment", chatFragment);
            }
        }
        if (addFriendFragment != null) {
            if (addFriendFragment.isVisible()) {
                getSupportFragmentManager().putFragment(outState, "addFriendFragment", addFriendFragment);
            }
        }
    }
}