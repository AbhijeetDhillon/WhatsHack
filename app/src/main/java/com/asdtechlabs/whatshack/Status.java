package com.asdtechlabs.whatshack;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.facebook.ads.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Status extends AppCompatActivity {
    public static int counter = 0;
    private static final String TAG = "blueskyapps";
    private Boolean permissionCheck = false;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    SharedPreferences sharedPreferences;
    public static InterstitialAd mInterstitialAd;
    boolean isRefresh = true;
    static Uri contentUri;
   // public static InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AudienceNetworkAds.initialize(this);
        mInterstitialAd = new InterstitialAd(this, "759159841258072_767348930439163");
        mInterstitialAd.loadAd();

        if(Splash.progressDialog != null) {
            if(Splash.progressDialog.isShowing()) {
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

        //----------------------//


        if (permissionCheck) {

            sharedPreferences = getSharedPreferences("AutoSave", MODE_PRIVATE);



            //------------------------------------------
            create();
            setContentView(R.layout.activity_status);

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

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.itemRefresh:

                Intent intent = getIntent();
                finish();
                startActivity(intent);

                Toast toast = Toast.makeText(getBaseContext(), "Refreshed", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;

            case R.id.contactUS:
                sendEmail();
                break;

            case R.id.itemSettings:
                Intent intentPlayStore = new Intent(Status.this, SettingsActivity.class);
                startActivity(intentPlayStore);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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


    private Boolean exit = false;

    @Override
    public void onBackPressed() {
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
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Status Saver - Query");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please type your message below along with your Smart Phone model and name. Thank You!");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Status.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }



}
