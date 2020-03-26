package com.asdtechlabs.whatshack.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.asdtechlabs.whatshack.contracts.MessageContract;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.constraint.motion.MotionScene.TAG;

/**
 * Created by dduggan.
 */

public class Message implements Parcelable, Persistable {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public String GroupName;

    public Message() {
    }

    public Message(Cursor cursor) {
        // TODO
        this.messageText = MessageContract.getMessageText(cursor);
        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.timestamp = iso8601Format.parse(MessageContract.getTimeStamp(cursor));
        } catch (ParseException e) {
            Log.e(TAG, "Parsing ISO8601 datetime failed", e);
        }

        this.sender = MessageContract.getsenderColumn(cursor);
        this.GroupName = MessageContract.getGroupName(cursor);
    }

    public Message(Parcel in) {
        // TODO
        messageText = in.readString();
        timestamp = (Date) in.readSerializable();
        sender = in.readString();
        GroupName = in.readString();
    }

    @Override
    public void writeToProvider(ContentValues out) {
        // TODO
        MessageContract.putMessageText(out,messageText);
        MessageContract.putTimeStamp(out, String.valueOf(timestamp));
        MessageContract.putsenderColumn(out,sender);
        MessageContract.putGroupName(out,GroupName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        dest.writeString(messageText);
        dest.writeSerializable(String.valueOf(timestamp));
        dest.writeString(sender);
        dest.writeString(GroupName);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        @Override
        public Message createFromParcel(Parcel source) {
            // TODO
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            // TODO
            return new Message[size];
        }

    };

}

