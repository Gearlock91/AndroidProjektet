package com.example.projektet;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class FriendsListAdapter  extends BaseAdapter {

    List<MemberData> friendsList = new ArrayList<MemberData>();
    Context context;


    public FriendsListAdapter(Context context) {
        this.context = context;
    }

    public void add(MemberData friend){
        friendsList.add(friend);
        notifyDataSetChanged();
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater friendInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        FriendHolder holder = null;
        MemberData friend = friendsList.get(position);
        convertView = friendInflater.inflate(R.layout.friends_layout, null);
        holder.nickName = (TextView) convertView.findViewById(R.id.friend_holder);
        convertView.setTag(holder);
        holder.nickName.setText(friend.getNickName());
        return convertView;
    }

    public class FriendHolder{
        public TextView nickName;
    }
}
