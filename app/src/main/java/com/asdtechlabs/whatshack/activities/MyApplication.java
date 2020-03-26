package com.asdtechlabs.whatshack.activities;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.facebook.ads.AudienceNetworkAds;

public class MyApplication extends Application {

    public static Context context;
    public static Boolean firstTime = true;
    static int counter = 0;
    public void onCreate() {
        AudienceNetworkAds.initialize(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        super.onCreate();
        MyApplication.context = getApplicationContext();


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }


    public static Context getAppContext() {
        return MyApplication.context;
    }
}
