package com.asdtechlabs.whatshack.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by dduggan.
 */

public class UserContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "User");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    // TODO define column names, getters for cursors, setters for contentvalues

    public static final String NAME = "userName";

    public static final String TIMESTAMP = "timestamp";

    public static final String GROUPNAME = "groupName";

    private static int senderColumn = -1;
    private static int timeStampColumn = -1;
    private static int groupColumn = -1;

    public static String getName(Cursor cursor) {
        if (senderColumn < 0) {
            senderColumn = cursor.getColumnIndexOrThrow(NAME);
        }
        return cursor.getString(senderColumn);
    }

    public static void putName(ContentValues out, String name) {
        out.put(NAME, name);
    }

    public static String getTimestamp(Cursor cursor) {
        if (timeStampColumn < 0) {
            timeStampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return cursor.getString(timeStampColumn);
    }

    public static void putTimestamp(ContentValues out, String timestamp) {
        out.put(TIMESTAMP, timestamp);
    }
    public static String getInet(Cursor cursor) {
        if (groupColumn < 0) {
            groupColumn = cursor.getColumnIndexOrThrow(GROUPNAME);
        }
        return cursor.getString(groupColumn);
    }

    public static void putInet(ContentValues out, String inet) {
        out.put(GROUPNAME, inet);
    }

}
