package com.example.projektet;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Denna klass är en adapter för att hålla reda på vännerlistan.
 * @author Andres Roghe, Sofia Ågren.
 * @version 2020-01-05
 */
public class FriendsListAdapter extends BaseAdapter {

    private List<MemberData> friendsList = new ArrayList<MemberData>();
    private Context context;
    private Listener listener;

    public interface Listener {
        void startChat(int position);
    }

    public FriendsListAdapter(Context context) {
        this.context = context;

        if (context instanceof FriendsListAdapter.Listener) {
            this.listener = (Listener) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement listener interface.");
        }
    }

    public void add(MemberData friend) {
        friendsList.add(friend);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        friendsList.remove(position);
    }

    public void clear() {
        friendsList.clear();
    }

    @Override
    public int getCount() {
        return friendsList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater friendInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        FriendHolder holder = new FriendHolder();
        MemberData friend = friendsList.get(position);
        convertView = friendInflater.inflate(R.layout.friends_layout, null);
        holder.nickName = (TextView) convertView.findViewById(R.id.friend_holder);
        holder.removeButton = (ImageButton) convertView.findViewById(R.id.remove_friend);

        holder.nickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.startChat(position);
            }
        });

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("users/" + currentUser.getDisplayName() + "/" + "Friends/" + friendsList.get(position).getNickName());
                ref.removeValue();

                SQLiteOpenHelper sqlCryptoHelper = new SqlCryptoHelper(FriendsListAdapter.this.context);
                SQLiteDatabase db = sqlCryptoHelper.getWritableDatabase();
                db.delete("CRYPTOLEDGER", "FRIEND=" + "'" + friend.getNickName() + "'", null);
                friendsList.remove(position);
                notifyDataSetChanged();
            }
        });

        convertView.setTag(holder);
        holder.nickName.setText(friend.getNickName());
        return convertView;
    }

    public class FriendHolder {
        public TextView nickName;
        public ImageButton removeButton;
    }
}
