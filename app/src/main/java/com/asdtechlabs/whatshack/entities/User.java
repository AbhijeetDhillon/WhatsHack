package com.asdtechlabs.whatshack.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.asdtechlabs.whatshack.contracts.UserContract;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.support.constraint.motion.MotionScene.TAG;

/**
 * Created by dduggan.
 */

public class User implements Parcelable, Persistable {

    // Will be database key
    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    // Where we heard from them
    public InetAddress address;

    public User() {
    }

    public User(Cursor cursor) {
        // TODO
        name = UserContract.getName(cursor);
        // timestamp = UserContract.getTimestamp(cursor);
        DateFormat iso8601Format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        try {
            this.timestamp = (Date) iso8601Format.parse(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.TIMESTAMP)));
        } catch (ParseException e) {
            Log.e(TAG, "Parsing ISO8601 datetime failed", e);
        }



    }

    public User(Parcel in) {
        // TODO
        name = (String) in.readSerializable();
        timestamp = (Date) in.readSerializable();
    }

    @Override
    public void writeToProvider(ContentValues out) {
        // TODO
        UserContract.putName(out,name);
        UserContract.putTimestamp(out,String.valueOf(timestamp));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // TODO
        out.writeSerializable(name);
        out.writeSerializable(timestamp);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            // TODO
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            // TODO
            return new User[size];
        }

    };
}
