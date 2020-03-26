package com.asdtechlabs.whatshack.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.asdtechlabs.whatshack.contracts.BaseContract;
import com.asdtechlabs.whatshack.contracts.MessageContract;
import com.asdtechlabs.whatshack.contracts.UserContract;

import static android.provider.BaseColumns._ID;


public class ChatProvider extends ContentProvider {

    private static final String DATABASE_NAME = "whatshack.db";

    private static final int DATABASE_VERSION = 1;

    private static final String USERS_TABLE = "peers";

    private static final String MESSAGES_TABLE = "messages";

    private static final String AUTHORITY = BaseContract.AUTHORITY;

    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;

    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;

    private static final String PEER_CONTENT_PATH = UserContract.CONTENT_PATH;

    private static final String PEER_CONTENT_PATH_ITEM = UserContract.CONTENT_PATH_ITEM;

    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int PEERS_ALL_ROWS = 3;
    private static final int PEERS_SINGLE_ROW = 4;

    public static class DbHelper extends SQLiteOpenHelper {




        private static final String DATABASE_WhatsApp_User =
                "CREATE TABLE " + USERS_TABLE +"("+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+
                        UserContract.NAME+" TEXT NOT NULL UNIQUE,"+
                        UserContract.GROUPNAME+" TEXT "+ " )";
        private static final String DATABASE_WhatsApp_Message =
                "CREATE TABLE " + MESSAGES_TABLE + "(" + _ID + "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        MessageContract.MESSAGE_TEXT+"TEXT NOT NULL, " +
                        MessageContract.SENDER+ "TEXT NOT NULL, "+
                        MessageContract.GROUP_NAME + "TEXT " + " )";

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DATABASE_WhatsApp_User);
            sqLiteDatabase.execSQL(DATABASE_WhatsApp_Message);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE);
            sqLiteDatabase.execSQL(("DROP TABLE IF EXISTS " + USERS_TABLE));
            onCreate(sqLiteDatabase);
        }

        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

    }
    private DbHelper dbHelper;
    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;

    }

    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle query of all messages.
                return db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle query of all peers.
                return db.query(USERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
            case MESSAGES_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific message.
                if(uri!=null) {
                    selection = MessageContract._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(BaseContract.getId(uri))};
                    return db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                }
                throw new UnsupportedOperationException("Not yet implemented");
            case PEERS_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific peer.
                if(uri !=null)
                {
                    selection = UserContract._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(BaseContract.getId(uri))};
                    return db.query(USERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                }
                throw new UnsupportedOperationException("Not yet implemented");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new message.
                long messageRow = db.insert(MESSAGES_TABLE, null, contentValues);
                if (messageRow > 0) {
                    Uri instanceURI = MessageContract.CONTENT_URI(messageRow);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceURI, null);
                    return instanceURI;
                }
                // Make sure to notify any observers
                throw new UnsupportedOperationException("Not yet implemented");
            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new peer.
                long peerRow = db.insert(USERS_TABLE, null, contentValues);
                if (peerRow > 0) {
                    Uri instanceURI = UserContract.CONTENT_URI(peerRow);
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceURI, null);
                    return instanceURI;
                }
                // Make sure to notify any observers
                throw new UnsupportedOperationException("Not yet implemented");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Implement this to handle requests to delete one or more rows.

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {

            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle query of all messages.
                return db.delete(MESSAGES_TABLE, selection, selectionArgs);
            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle query of all peers.
                return db.delete(USERS_TABLE, selection, selectionArgs);
            default:
                throw new IllegalStateException("insert: bad case");

        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Implement this to handle requests to update one or more rows.

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {

            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle query of all messages.
                return db.update(MESSAGES_TABLE, values, selection, selectionArgs);
            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle query of all peers.
                return db.update(USERS_TABLE, values, selection, selectionArgs);
            case MESSAGES_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific message.
                if (uri != null) {
                    selection = MessageContract._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(BaseContract.getId(uri))};
                    return db.update(MESSAGES_TABLE, values, selection, selectionArgs);
                }
                throw new UnsupportedOperationException("Not yet implemented");
            case PEERS_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific peer.
                if (uri != null) {
                    selection = UserContract._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(BaseContract.getId(uri))};
                    return db.update(USERS_TABLE, values, selection, selectionArgs);
                }
                throw new UnsupportedOperationException("Not yet implemented");
            default:
                throw new IllegalStateException("insert: bad case");

        }
    }
}
