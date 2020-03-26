package com.asdtechlabs.whatshack.activities;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.asdtechlabs.whatshack.MyApplication;
import com.asdtechlabs.whatshack.R;
import com.asdtechlabs.whatshack.UnseenMessageChat;
import com.asdtechlabs.whatshack.services.MyNotification;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeletedMessages extends AppCompatActivity {


    private AlertDialog enableNotificationListenerAlertDialog;
    public static final String MY_PREFS_NAME = "DeletedChats";
    String keyValue,deletedtextName = null;
    Set<String> deletedtexts;
    Boolean deletePresent = true;
    List<HashMap<String, String>> aList;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    HashMap<String,ArrayList<String>> UnseenMessages = new HashMap<String,ArrayList<String>>();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_messages);

        AdView adView = new AdView(MyApplication.context, getString(R.string.FB_Banner), AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();


        TextView deletedMessages = findViewById(R.id.deletedMessages);
        deletedMessages.setVisibility(View.GONE);
        setTitle("WhatsHack");
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        deletedtexts = prefs.getStringSet("ChatsDeleted", null);

        //List to display Contact Name and Chat in ListView
        aList = new ArrayList<HashMap<String, String>>();
        Intent i = getIntent();
        deletedtextName = i.getStringExtra("ContactName");
        if (TextUtils.isEmpty(deletedtextName)) {

            if (TextUtils.isEmpty(MyNotification.ContactName)) {

                deletedMessages.setVisibility(View.VISIBLE);
                deletePresent = false;
            }

            else
            {
                deletedtextName = MyNotification.ContactName;
            }
        }

        else
        {
            deletePresent = false;
        }
        // If the user did not turn the notification listener service on we prompt him to do so
        if (!isNotificationServiceEnabled()) {
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }



        if(deletedtexts!=null)
        {


            if(deletedtexts.contains(deletedtextName))
            {
                deletedtexts.remove(deletedtextName);
                deletedtexts.add(deletedtextName);
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putStringSet("ChatsDeleted", (Set<String>) deletedtexts);
                editor.apply();
                getDeletedMesasages();
            }

            else if(!TextUtils.isEmpty(deletedtextName))
            {
                deletedtexts.add(deletedtextName);
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putStringSet("ChatsDeleted", (Set<String>) deletedtexts);
                editor.apply();
                getDeletedMesasages();
            }
            else
            {
                deletedMessages.setVisibility(View.VISIBLE);
            }


        }

        else
        {

            if(!TextUtils.isEmpty(deletedtextName))
            {
                deletedtexts = new HashSet<String>();
                deletedtexts.add(deletedtextName);
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putStringSet("ChatsDeleted", (Set<String>) deletedtexts);
                editor.apply();
                getDeletedMesasages();
            }
            else
            {
                 deletedMessages.setVisibility(View.VISIBLE);
        }
        }




    }

    public void getDeletedMesasages()
    {
        try {

            ObjectInputStream ois = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2 && MyNotification.file != null) {
                ois = new ObjectInputStream(new FileInputStream(MyNotification.file));

                try {
                    UnseenMessages = (HashMap<String, ArrayList<String>>) ois.readObject();


                    for (String key : deletedtexts) {
                        HashMap<String, String> hm = new HashMap<String, String>();
                        hm.put("listview_title", key);
                        keyValue = key;
                        for (String value : UnseenMessages.get(keyValue)) {
                            hm.put("listview_discription", value);
                        }
                        aList.add(hm);
                    }

                    Log.d("HashMap", String.valueOf(UnseenMessages));

                    String[] from = {"listview_title", "listview_discription"};
                    int[] to = {R.id.listview_item_title, R.id.listview_item_short_description};
                    SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_activity, from, to);
                    ListView androidListView = (ListView) findViewById(R.id.list_view);
                    androidListView.setAdapter(simpleAdapter);

                    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                            Bundle b = new Bundle();
                            b.putString("Name", aList.get(position).get("listview_title"));
                            b.putStringArrayList("Chat", UnseenMessages.get(aList.get(position).get("listview_title")));
                            Intent intent = new Intent(DeletedMessages.this, UnseenMessageChat.class);
                            intent.putExtras(b);
                            startActivity(intent);
                        }

                    };
                    androidListView.setOnItemClickListener(itemClickListener);


                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }



            } else {
                Log.d("No Messsages","NOOOO MEssage");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.notification_listener_service);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            finish();
            startActivity(getIntent());
        }
        return super.onOptionsItemSelected(item);
    }


}
