package com.asdtechlabs.whatshack;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;


public class FullScreenPreview extends AppCompatActivity {

    private static final String TAG = "asdTechlabs";
    VideoView videoView;
    String dir = "WhatsApp Status";
    File file_path;
    ArrayList<File> fileArrayList;
    Button prev, next;
    int height,width;
    int position;
    FloatingActionMenu materialDesignFAM;
    String type;
    ProgressDialog progress;

    static int currentposition;
    static Uri contentUri;
    static File updated_file_path;
    //InterstitialAd mInterstitialAd;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            //noinspection ConstantConditions
            getSupportActionBar().hide();
        } catch (Exception e) {
            Log.d(TAG, "onCreate: " + e);
        }

        setContentView(R.layout.activity_full_screen_preview);

        if(MainActivity.mInterstitialAd != null && MainActivity.mInterstitialAd.isAdLoaded()) {

            MainActivity.mInterstitialAd.show();
            MainActivity.counter++;
        }


        MainActivity.mInterstitialAd.setAdListener(new InterstitialAdListener() {
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







       com.github.clans.fab.FloatingActionButton fab_Share;
        com.github.clans.fab.FloatingActionButton fab_Download;
        file_path = (File) Objects.requireNonNull(getIntent().getExtras()).get("filePath");
        //noinspection unchecked
        fileArrayList = (ArrayList<File>) getIntent().getExtras().get("allFiles");
        //noinspection ConstantConditions
        position = (Integer) getIntent().getExtras().get("position");
        type = String.valueOf(file_path);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        ImageAdapter adapterView = new ImageAdapter(this, position, fileArrayList, type, file_path, width,height);
        mViewPager.setAdapter(adapterView);
        mViewPager.setCurrentItem(position);
        currentposition = mViewPager.getCurrentItem();
        updated_file_path = ImageAdapter.saveMedia(currentposition);
        if (MyApplication.firstTime) {
            Toast toast = Toast.makeText(getBaseContext(), "Swipe to surf between images/videos. Double Tap to Zoom", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            MyApplication.firstTime = false;
        }
        materialDesignFAM = findViewById(R.id.fab_main);
        fab_Share = findViewById(R.id.fab_Share);
        fab_Download = findViewById(R.id.fab_Download);


        materialDesignFAM.close(true);

        //FAB DOWNLOAD OR DELETE
        if (type.contains("WhatsApp/Media/.Statuses/") || type.contains("WhatsApp Business/Media/.Statuses/")) {
            fab_Download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    materialDesignFAM.close(true);
                    currentposition = mViewPager.getCurrentItem();
                    updated_file_path = ImageAdapter.saveMedia(currentposition);
                    saveMedia();
                }
            });
        } else {
            fab_Download.setImageResource(R.drawable.ic_delete);
            fab_Download.setLabelText("Delete");
            fab_Download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDesignFAM.close(true);
                    currentposition = mViewPager.getCurrentItem();
                    updated_file_path = ImageAdapter.saveMedia(currentposition);
                    deletemedia();

                }
            });
        }


        //FAB SHARE
        fab_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDesignFAM.close(true);
                currentposition = mViewPager.getCurrentItem();
                updated_file_path = ImageAdapter.saveMedia(currentposition);
                sharemedia();
            }
        });
    }

    private void saveMedia() {



        try {
            File rootPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), dir);
            if (!rootPath.exists()) {
                Boolean result = rootPath.mkdirs();
                if (!result) {
                    Toast.makeText(getBaseContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
            if (rootPath.exists()) {
                //Progress Bar
                materialDesignFAM.close(true);
                progress = new ProgressDialog(this);
                progress.setMessage(getResources().getString(R.string.progress));
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.show();
                new Thread() {
                    public void run() {
                        //QRCode generator
                        try {
                            String strFileName = updated_file_path.getName();
                            Log.d(TAG, "saveMedia: " + strFileName);

                            String destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + dir + "/" + strFileName;
                            File destination = new File(destinationPath);
                            Log.d(TAG, "saveMedia: " + destination);


                            if (!destination.exists()) {
                                if (updated_file_path.exists()) {

                                    InputStream in = new FileInputStream(updated_file_path);
                                    OutputStream out = new FileOutputStream(destination);

                                    byte[] buf = new byte[512];
                                    int len;

                                    while ((len = in.read(buf)) > 0) {
                                        out.write(buf, 0, len);
                                    }

                                    in.close();
                                    out.close();
                                    Thread.sleep(500);
                                    progress.dismiss();

                                    //Sending Media Changer Broadcast
                                    try {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                            Intent mediaScanIntent = new Intent(
                                                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                                            if(Build.VERSION.SDK_INT == 24){
//                                                contentUri = FileProvider.getUriForFile(FullScreenPreview.this,
//                                                        getApplicationContext().getPackageName() + ".provider",
//                                                        destination);
//                                            } else
//                                            {
//                                                contentUri = Uri.fromFile(destination);
//                                            }
                                            contentUri = Uri.fromFile(destination);
                                            mediaScanIntent.setData(contentUri);
                                            sendBroadcast(mediaScanIntent);
                                        } else {
                                            sendBroadcast(new Intent(
                                                    Intent.ACTION_MEDIA_MOUNTED,
                                                    Uri.parse("file://"
                                                            + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))));
                                        }

                                        Log.d(TAG, "run: Sending intent..");
                                    } catch (Exception e) {
                                        Log.d(TAG, "run: Sending intent failed.." + e);
                                    }
                                    //------------------------------

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast toast = Toast.makeText(getBaseContext(), "Saved Successfully!", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                        }
                                    });
                                } else {
                                    Log.v(TAG, "Copy file failed. Source file missing.");
                                }
                            } else {
                                Log.d(TAG, "run: File Already Exists!");
                                Thread.sleep(500);
                                progress.dismiss();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(getBaseContext(), "File Already Exists!", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                });
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }


                }.start();
            }
        } catch (Exception e) {
            Log.d(TAG, "saveMedia: Unable to access the storage");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//
//        if (mInterstitialAd.isLoaded() ) {
//            mInterstitialAd.show();
//        } else {
//            Log.d("TAG", "The interstitial wasn't loaded yet.");
//        }
    }

    public static void play_video(Context context, int position)
    {
        File updated_file_path = ImageAdapter.saveMedia(position);
        File file = new File(String.valueOf(updated_file_path));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        contentUri = Uri.fromFile(file);
        intent.setDataAndType(contentUri, "video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getAppContext().startActivity(intent);

    }

    public void deletemedia()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(FullScreenPreview.this);
        builder.setTitle("Warning")
                .setMessage("Are you sure want to delete?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Boolean result = updated_file_path.delete();

                        //Sending Media Changer Broadcast
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                Intent mediaScanIntent = new Intent(
                                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//
//                                            if (Build.VERSION.SDK_INT == 24) {
//                                                contentUri = FileProvider.getUriForFile(FullScreenPreview.this,
//                                                        getApplicationContext().getPackageName() + ".provider",
//                                                        updated_file_path);
//                                            } else {
//                                                contentUri = Uri.fromFile(updated_file_path);
//                                            }
                                contentUri = Uri.fromFile(updated_file_path);
                                mediaScanIntent.setData(contentUri);
                                sendBroadcast(mediaScanIntent);
                            } else {
                                sendBroadcast(new Intent(
                                        Intent.ACTION_MEDIA_MOUNTED,
                                        Uri.parse("file://"
                                                + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))));
                            }

                            Log.d(TAG, "run: Sending intent..");
                        } catch (Exception e) {
                            Log.d(TAG, "run: Sending intent failed.." + e);
                        }
                        //------------------------------

                        if (result) {
                            Toast toast = Toast.makeText(getBaseContext(), "Deleted Successfully", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            finish();
                            Intent intent = new Intent(FullScreenPreview.this, MainActivity.class);
                            intent.putExtra("tab", 2);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "onClick: Unable to delete");
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void sharemedia()
    {

        Uri safeURI = FileProvider.getUriForFile(getApplicationContext(), "com.asdtechlabs.whatshack.provider", updated_file_path);

        Log.d(TAG, "onClick: Share clicked");
        if (type.contains(".jpg")) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                //  shareIntent.setData(photoURI);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, safeURI);
                startActivity(shareIntent);
            } catch (Exception e) {
                Log.d(TAG, "onClick: Unable to share");
            }
        } else if (type.contains(".mp4")) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                //   shareIntent.setData(Uri.fromFile(file_path));
                shareIntent.setType("video/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, safeURI);
                startActivity(shareIntent);
            } catch (Exception e) {
                Log.d(TAG, "onClick: Unable to share");
            }
        }
    }
}

