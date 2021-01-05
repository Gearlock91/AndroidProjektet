package com.example.projektet;

import android.os.Parcel;
import android.os.Parcelable;

public class CryptoMessage implements Parcelable {

    private final String text;
    private final String memberNickname;
    private final boolean belongsToCurrentUser;

    public CryptoMessage(String text, boolean belongsToCurrentUser) {
        this(text, null, belongsToCurrentUser);
    }


    public CryptoMessage(String text, String memberNickname, boolean belongsToCurrentUser) {
        this.text = text;
        this.memberNickname = memberNickname;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    protected CryptoMessage(Parcel in) {
        text = in.readString();
        memberNickname = in.readString();
        belongsToCurrentUser = in.readByte() != 0;
    }

    public static final Creator<CryptoMessage> CREATOR = new Creator<CryptoMessage>() {
        @Override
        public CryptoMessage createFromParcel(Parcel in) {
            return new CryptoMessage(in);
        }

        @Override
        public CryptoMessage[] newArray(int size) {
            return new CryptoMessage[size];
        }
    };

    public String getText() {
        return text;
    }

    public String getMemberNickname() {
        return memberNickname;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(memberNickname);
        dest.writeByte((byte) (belongsToCurrentUser ? 1 : 0));
    }
}
