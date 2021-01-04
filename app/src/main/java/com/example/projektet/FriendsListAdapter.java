package com.example.projektet;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class FriendsListAdapter extends BaseAdapter {

    List<MemberData> friendsList = new ArrayList<MemberData>();
    Context context;
    Listener listener;


    public interface Listener{
        void startChat(int position);
    }


    public FriendsListAdapter(Context context) {
        this.context = context;

        if(context instanceof FriendsListAdapter.Listener){
            this.listener = (Listener) context;
        }
        else{
            throw new RuntimeException(context.toString() + "must implement listener interface.");
        }
    }

    public void add(MemberData friend){
        friendsList.add(friend);
        notifyDataSetChanged();
    }

    public void remove(int position){
        friendsList.remove(position);
    }

    public void clear(){friendsList.clear();}

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
                friendsList.remove(position);
                notifyDataSetChanged();
            }
        });

        convertView.setTag(holder);
        holder.nickName.setText(friend.getNickName());
        return convertView;
    }

    public class FriendHolder{
        public TextView nickName;
        public ImageButton removeButton;
    }
}
