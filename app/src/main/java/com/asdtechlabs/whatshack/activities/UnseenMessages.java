package com.asdtechlabs.whatshack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.asdtechlabs.whatshack.R;
import com.asdtechlabs.whatshack.services.MyNotification;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnseenMessages extends AppCompatActivity {

    private AlertDialog enableNotificationListenerAlertDialog;
    String keyValue;
    TextView Unseen;
    ArrayList<String> unseenTexts;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    HashMap<String,ArrayList<String>> UnseenMessages = new HashMap<String,ArrayList<String>>();

//    private ImageChangeBroadcastReceiver imageChangeBroadcastReceiver;
//    private AlertDialog enableNotificationListenerAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unseen_messages);
        setTitle("WhatsHack");

        AdView adView = new AdView(MyApplication.context, getString(R.string.FB_Banner), AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();

        //List to display Contact Name and Chat in ListView
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
        Unseen = findViewById(R.id.unseenMessages);
        Unseen.setVisibility(View.GONE);
        // If the user did not turn the notification listener service on we prompt him to do so





        try {

            ObjectInputStream ois = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2 && MyNotification.file != null) {
                ois = new ObjectInputStream(new FileInputStream(MyNotification.file));


                try {
                    UnseenMessages = (HashMap<String, ArrayList<String>>) ois.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                int i = 0;

                if(ois == null){
                    Unseen.setVisibility(View.VISIBLE);
                }
                for (String key : UnseenMessages.keySet()) {
                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("listview_title", key);
                    keyValue = key;
                    for (String value : UnseenMessages.get(keyValue))
                    {
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
                        Intent intent = new Intent(UnseenMessages.this, UnseenMessageChat.class);
                        intent.putExtras(b);
                        startActivity(intent);
                    }

                };
                androidListView.setOnItemClickListener(itemClickListener);

            } else {
               Log.d("No Messsages","NOOOO MEssage");
               Unseen.setVisibility(View.VISIBLE);

            }


        } catch (IOException e) {
            e.printStackTrace();
        }




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
