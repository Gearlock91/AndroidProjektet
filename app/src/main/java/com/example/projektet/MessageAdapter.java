package com.example.projektet;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// MessageAdapter.java
public class MessageAdapter extends BaseAdapter implements Parcelable {

    List<CryptoMessage> messages = new ArrayList<CryptoMessage>();
    Context context;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    protected MessageAdapter(Parcel in) {
        messages = in.createTypedArrayList(CryptoMessage.CREATOR);
    }

    public static final Creator<MessageAdapter> CREATOR = new Creator<MessageAdapter>() {
        @Override
        public MessageAdapter createFromParcel(Parcel in) {
            return new MessageAdapter(in);
        }

        @Override
        public MessageAdapter[] newArray(int size) {
            return new MessageAdapter[size];
        }
    };

    public List<CryptoMessage> getList(){
        return messages;
    }

    public void setList(List<CryptoMessage> list){
        messages = list;
    }

    public void clear(){
        this.messages.clear();
    }

    public void add(CryptoMessage message) {
        this.messages.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        CryptoMessage message = messages.get(i);

        if (message.isBelongsToCurrentUser()) { // this message was sent by us so let's create a basic chat bubble on the right
            convertView = messageInflater.inflate(R.layout.message_layout, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());
        } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
            convertView = messageInflater.inflate(R.layout.recieved_message, null);
           // holder.avatar = (View) convertView.findViewById(R.id.avatar);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);

            holder.name.setText(message.getMemberNickname());
            holder.messageBody.setText(message.getText());
           // drawable.setColor(Color.parseColor(message.getMemberData().getColor()));
        }

        return convertView;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(messages);
    }
}

class MessageViewHolder {
    public View avatar;
    public TextView name;
    public TextView messageBody;
}