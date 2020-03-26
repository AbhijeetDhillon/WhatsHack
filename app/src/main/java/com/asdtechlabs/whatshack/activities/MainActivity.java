package com.asdtechlabs.whatshack.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.asdtechlabs.whatshack.MyApplication;
import com.asdtechlabs.whatshack.PrivacyPolicy;
import com.asdtechlabs.whatshack.R;
import com.asdtechlabs.whatshack.Splash;
import com.asdtechlabs.whatshack.UnseenMessages;
import com.asdtechlabs.whatshack.adapters.SectionsPagerAdapter;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static int counter = 0;
    private static final String TAG = "blueskyapps";
    private Boolean permissionCheck = false;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    SharedPreferences sharedPreferences;
    public static InterstitialAd mInterstitialAd;
    boolean isRefresh = true;
    static Uri contentUri;


    private AlertDialog enableNotificationListenerAlertDialog;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        if (Splash.progressDialog != null) {
            if (Splash.progressDialog.isShowing()) {
                Splash.progressDialog.dismiss();
                Splash.progressDialog = null;
            }

        }
        //Permission Seeking
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                permissionCheck = true;
                Log.d(TAG, "onCreate: Permission Granted");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            }
        }


        if (Build.VERSION.SDK_INT < 23) {
            permissionCheck = true;
        }

        //Check to see if Notification is enabled

        if (!isNotificationServiceEnabled()) {
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }

        //----------------------//


        if (permissionCheck) {

            sharedPreferences = getSharedPreferences("AutoSave", MODE_PRIVATE);


            //------------------------------------------
            create();
            setContentView(R.layout.activity_main);
            AdView adView = new AdView(MyApplication.context, getString(R.string.FB_Banner), AdSize.BANNER_HEIGHT_50);
            // Find the Ad Container
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

            // Add the ad view to your activity layout
            adContainer.addView(adView);

            // Request an ad
            adView.loadAd();
            setTitle("WhatsHack");
            //Load Facebook Ad Interstitial
            AudienceNetworkAds.initialize(this);
            mInterstitialAd = new InterstitialAd(this, "1313592302127751_1320807618072886");


            mInterstitialAd.setAdListener(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    // Interstitial ad displayed callback
                    Log.e(TAG, "Interstitial ad displayed.");
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    // Interstitial dismissed callback
                    Log.e(TAG, "Interstitial ad dismissed.");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Ad error callback
                    Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Interstitial ad is loaded and ready to be displayed
                    Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                    // Show the ad
                    ///mInterstitialAd.show();
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback
                    Log.d(TAG, "Interstitial ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                    Log.d(TAG, "Interstitial ad impression logged!");
                }
            });


            mInterstitialAd.loadAd();




            int index = getIntent().getIntExtra("tab", 0);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


            // Set up the ViewPager with the sections adapter.

            mViewPager = findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = findViewById(R.id.tabs);

            mViewPager.setCurrentItem(index);

            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


            setSupportActionBar(toolbar);

//            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
//            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.setItemIconTintList(null);

        }

    }


    private Boolean exit = false;

        @Override
        public void onBackPressed () {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (exit) {
                finish(); // finish activity
            } else {
                Toast.makeText(this, "Press Back again to Exit.",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);

            }
        }


    }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if(id == R.id.itemRefresh) {

                Intent intent = getIntent();
                finish();
                startActivity(intent);

                Toast toast = Toast.makeText(getBaseContext(), "Refreshed", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }

            return super.onOptionsItemSelected(item);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected (MenuItem item){
            // Handle navigation view item clicks here.


            int id = item.getItemId();



            if (id == R.id.statusSaver) {

            } else if (id == R.id.unseenMessages) {
                Intent intent = new Intent(MainActivity.this, UnseenMessages.class);
                startActivity(intent);

            } else if (id == R.id.deletedMessages) {
                Intent intent = new Intent(MainActivity.this, DeletedMessages.class);
                startActivity(intent);

            }  else if (id == R.id.nav_share) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Check out this app which lets you Save Photo and Video Status, Read Deleted Messages & Read Messages without Blue Tick: https://play.google.com/store/apps/details?id=com.asdtechlabs.whatshack");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            } else if (id == R.id.nav_send) {
                sendEmail();
            }
              else if (id == R.id.pv_policy) {
                Intent intent = new Intent(MainActivity.this, PrivacyPolicy.class);
                startActivity(intent);
    }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123:

                if (hasAllPermissionsGranted(grantResults)) {
                    // Permission Granted
                    Log.d(TAG, "onRequestPermissionsResult: Permission granted clicked");
                    finish();
                    startActivity(getIntent());
                } else {
                    // Permission Denied
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(this);
                    }
                    builder.setTitle("Permission Required!")
                            .setMessage("You need to allow access to storage")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    startActivity(getIntent());
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create()
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }







    public void create()
    {
        if (sharedPreferences.getBoolean("MigrateOld", true)) {

            //Migrating old data to new public directory

            File path = new File(Environment.getExternalStorageDirectory(), "WhatsApp Status");

            if (path.exists()) {
                Log.d(TAG, "onCreate: Old file found");
                final String[] fileNames = path.list();

                final ArrayList<File> files = new ArrayList<>();

                if (fileNames.length != 0) {

                    for (String filePath : fileNames) {
                        files.add(new File(filePath));
                    }
                    Log.d(TAG, "onCreate: " + files + files.size());


                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            int i = 0;
                            String TAG = "blueskyapps";
                            try {
                                String dir = "WhatsApp Status";
                                File rootPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), dir);
                                if (!rootPath.exists()) {
                                    Boolean result = rootPath.mkdirs();
                                    if (!result) {
                                        Log.d(TAG, "onCreate: Unable to create folder");
                                    }
                                }

                                while (i < files.size()) {

                                    File source = new File(Environment.getExternalStorageDirectory(), "WhatsApp Status/" + files.get(i));
                                    String strFileName = source.getName();
                                    String destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + dir + "/" + strFileName;
                                    File destination = new File(destinationPath);

                                    if (!destination.exists()) {

                                        if (source.exists()) {

                                            InputStream in = new FileInputStream(source);
                                            OutputStream out = new FileOutputStream(destination);

                                            byte[] buf = new byte[1024];
                                            int len;

                                            while ((len = in.read(buf)) > 0) {
                                                out.write(buf, 0, len);
                                            }

                                            in.close();
                                            out.close();

                                            Log.d(TAG, "onCreate: File saved!" + destination);

                                            //Sending Media Changer Broadcast
                                            try {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                    Intent mediaScanIntent = new Intent(
                                                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//
//                                                        if(Build.VERSION.SDK_INT == 24){
//                                                            contentUri = FileProvider.getUriForFile(Status.this,
//                                                                    getApplicationContext().getPackageName() + ".provider",
//                                                                    destination);
//                                                        } else
//                                                        {
//                                                            contentUri = Uri.fromFile(destination);
//                                                        }
                                                    contentUri = Uri.fromFile(destination);
                                                    mediaScanIntent.setData(contentUri);
                                                    sendBroadcast(mediaScanIntent);
                                                } else {
                                                    sendBroadcast(new Intent(
                                                            Intent.ACTION_MEDIA_MOUNTED,
                                                            Uri.parse("file://"
                                                                    + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))));
                                                }

                                                Log.d(TAG, "onCreate: Sending intent..");
                                            } catch (Exception e) {
                                                Log.d(TAG, "onCreate: Sending intent failed.." + e);
                                            }
                                            //------------------------------

                                            Boolean result = source.delete();
                                            if (result)
                                                Log.d(TAG, "onCreate: File Moved!");

                                        } else {
                                            Log.v(TAG, "onCreate: Copy file failed. Source file missing..." + source);
                                        }
                                    } else {
                                        Log.d(TAG, "onCreate: File Already Exists");
                                    }
                                    i++;
                                }
                            } catch (Exception e) {
                                Log.d(TAG, "onCreate: Something went wrong! " + e);
                            }
                        }
                    }).start();
                }

                try {
                    Boolean dirDelete = path.delete();
                    if (dirDelete)
                        Log.d(TAG, "onCreate: Migration Finished and old directory deleted!");
                } catch (Exception e) {
                    Log.d(TAG, "onCreate: Unable to delete Old directory!");
                }
            }

            if (!path.exists()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("MigrateOld", false);
                editor.apply();
                Log.d(TAG, "onCreate: OLD DIRECTORY DELETED SUCCESSFULLY");
            }
        }


    }

    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    protected void sendEmail() {
        Log.i("Send email", "");

        String[] TO = {"asdtechnolabs@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "WhatsHack - Query");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please type your message below along with your Smart Phone model and name. Thank You!");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
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


}
