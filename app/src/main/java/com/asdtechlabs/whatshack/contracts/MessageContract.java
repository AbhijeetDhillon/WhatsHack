package com.asdtechlabs.whatshack.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by dduggan.
 */

public class MessageContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Message");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public static final String ID = _ID;

    public static final String MESSAGE_TEXT = "message_text";

    public static final String TIMESTAMP = "timestamp";

    public static final String SENDER = "user";

    public static final String GROUP_NAME = "userId";


    // TODO remaining columns in Messages table

    private static int messageTextColumn = -1;
    private static int timeStampColumn = -1;
    private static int senderColumn = -1;
    private static int senderIdColumn = -1;

    public static String getMessageText(Cursor cursor) {
        if (messageTextColumn < 0) {
            messageTextColumn = cursor.getColumnIndexOrThrow(MESSAGE_TEXT);
        }
        return cursor.getString(messageTextColumn);
    }

    public static void putMessageText(ContentValues out, String messageText) {
        out.put(MESSAGE_TEXT, messageText);
    }

    // TODO remaining getter and putter operations for other columns

    public static String getGroupName(Cursor cursor) {
        if (senderIdColumn < 0) {
            senderIdColumn = cursor.getColumnIndexOrThrow(GROUP_NAME);
        }
        return cursor.getString(senderIdColumn);
    }

    public static void putGroupName(ContentValues out, String groupName) {
        out.put(GROUP_NAME, groupName);
    }

    public static String getTimeStamp(Cursor cursor)
    {
        if(timeStampColumn<0)
        {
            timeStampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return cursor.getString(timeStampColumn);
    }

    public static void putTimeStamp(ContentValues out, String timestamp) {
        out.put(TIMESTAMP, timestamp);
    }

    public static String getsenderColumn(Cursor cursor)
    {
        if(senderColumn<0)
        {
            senderColumn = cursor.getColumnIndexOrThrow(SENDER);
        }
        return cursor.getString(senderColumn);
    }

    public static void putsenderColumn(ContentValues out, String sender) {

        out.put(SENDER, sender);
    }


}
