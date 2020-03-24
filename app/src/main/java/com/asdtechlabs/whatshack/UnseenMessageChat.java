package com.asdtechlabs.whatshack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.util.ArrayList;

public class UnseenMessageChat extends AppCompatActivity {

    ArrayList<String> messageList;
    String Name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unseen_message_chat);

        AdView adView = new AdView(MyApplication.context, getString(R.string.FB_Banner), AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();


        Bundle b=this.getIntent().getExtras();
        assert b != null;
        messageList =  b.getStringArrayList("Chat");
        Name = b.getString("Name");
        setTitle(Name);
        RecyclerView mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        MessageListAdapter mMessageAdapter = new MessageListAdapter( messageList);
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.scrollToPosition(messageList.size()-1);
        mMessageRecycler.smoothScrollToPosition(messageList.size()-1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        mMessageRecycler.setLayoutManager(layoutManager);
        //mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onBackPressed() {

        if(MainActivity.mInterstitialAd != null && MainActivity.mInterstitialAd.isAdLoaded()) {
            MainActivity.mInterstitialAd.show();
        }
        else {
            Log.d("NOT Loaded","Ad did not load");
        }
        super.onBackPressed();

    }



}

