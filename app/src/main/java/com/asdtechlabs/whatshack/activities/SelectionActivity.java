package com.asdtechlabs.whatshack.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.asdtechlabs.whatshack.R;
import com.asdtechlabs.whatshack.adapters.SelectionAdapter;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SelectionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SelectionAdapter myAdapter;
    String TAG = "blueskyapps";
    List<Integer> selection;
    FloatingActionMenu materialDesignFAM;
    ArrayList<File> files;
    ProgressDialog progress;
    String dir = "WhatsApp Status";
    String type;
    File file_path;
    public SelectionActivity() {
        file_path = null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        try{
            //noinspection unchecked,ConstantConditions
            files = (ArrayList<File>) getIntent().getExtras().get("allFilesPath");
            if (files != null) {
                type = String.valueOf(files.get(0));
            }
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e);
        }

        myAdapter = new SelectionAdapter(getBaseContext(), files);
        recyclerView = findViewById(R.id.rv_selectedImages);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getBaseContext(), 2);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(mLayoutManager);
        myAdapter.setSingleClickMode(true);

        //Progress loading
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.progressWait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(myAdapter);
                progress.dismiss();
            }
        });


        //FAB BUTTONS
        materialDesignFAM = findViewById(R.id.fab_Select_main);
        com.github.clans.fab.FloatingActionButton fab_Select_Download, fab_Select_help;
        com.github.clans.fab.FloatingActionButton fab_Share;
        fab_Select_Download = findViewById(R.id.fab_Select_Download);
        fab_Select_help = findViewById(R.id.fab_Select_Help);
        fab_Share = findViewById(R.id.fab_Share);
        //FAB DOWNLOAD OR DELETE
        if (type.contains("WhatsApp/Media/.Statuses/") || type.contains("WhatsApp Business/Media/.Statuses/")) {
            fab_Select_Download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDesignFAM.close(true);
                    if(myAdapter.getSelectedItemCount()>0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SelectionActivity.this);
                        builder.setTitle("Confirm")
                                .setMessage("Are you sure want to save " + myAdapter.getSelectedItemCount() + " items?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        saveAll();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                    }else{
                          Toast toast = Toast.makeText(getBaseContext(), "Please select items to save!", Toast.LENGTH_SHORT);
                          toast.setGravity(Gravity.CENTER, 0, 0);
                          toast.show();
                    }
                }
            });

        } else {
            fab_Select_Download.setImageResource(android.R.drawable.ic_menu_delete);
            fab_Select_Download.setLabelText("Delete");
            fab_Select_Download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDesignFAM.close(true);
                    if (myAdapter.getSelectedItemCount() > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SelectionActivity.this);
                        builder.setTitle("Warning")
                                .setMessage("Are you sure want to delete " + myAdapter.getSelectedItemCount() + " items?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DeleteAll();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                    }else{
                        Toast toast = Toast.makeText(getBaseContext(), "Please select items to delete!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });
        }

        fab_Select_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDesignFAM.close(true);
                displayHelp();
            }
        });

        //FAB SHARE
//        fab_Share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                materialDesignFAM.close(true);
//                //saveAll();
//                Uri safeURI = FileProvider.getUriForFile(getApplicationContext(), "com.blueskyapps.statusdownloader.provider",file_path) ;
//
//                Log.d(TAG, "onClick: Share clicked");
//                if (type.contains(".jpg")) {
//                    try {
//                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                        //  shareIntent.setData(photoURI);
//                        shareIntent.setType("image/*");
//                        shareIntent.putExtra(Intent.EXTRA_STREAM, safeURI);
//                        startActivity(shareIntent);
//                    } catch (Exception e) {
//                        Log.d(TAG, "onClick: Unable to share");
//                    }
//                } else if (type.contains(".mp4")) {
//                    try {
//                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                        //   shareIntent.setData(Uri.fromFile(file_path));
//                        shareIntent.setType("video/*");
//                        shareIntent.putExtra(Intent.EXTRA_STREAM, safeURI);
//                        startActivity(shareIntent);
//                    } catch (Exception e) {
//                        Log.d(TAG, "onClick: Unable to share");
//                    }
//                }
//            }
//        });

    }

    public void saveAll() {

        int count = myAdapter.getSelectedItemCount();
        selection = myAdapter.getSelectedItemList();

        Log.d(TAG, "saveAll: " + count + "\n\n" + selection);

        if (count > 0) {
            progress = new ProgressDialog(this);
            progress.setMessage(getResources().getString(R.string.progress));
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();

            for (int i = 0; i < count; i++) {

                File file_path = files.get(selection.get(i));

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

                        new Thread() {
                            public void run() {
                                try {
                                    String strFileName = file_path.getName();
                                    Log.d(TAG, "saveMedia: " + strFileName);

                                    String destinationPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + dir + "/" + strFileName;
                                    File destination = new File(destinationPath);
                                    Log.d(TAG, "saveMedia: " + destination);


                                    if (!destination.exists()) {
                                        if (file_path.exists()) {

                                            InputStream in = new FileInputStream(file_path);
                                            OutputStream out = new FileOutputStream(destination);

                                            byte[] buf = new byte[1024];
                                            int len;

                                            while ((len = in.read(buf)) > 0) {
                                                out.write(buf, 0, len);
                                            }
                                            in.close();
                                            out.close();

                                            //Sending Media Changer Broadcast
                                            try{
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                    Intent mediaScanIntent = new Intent(
                                                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                                    Uri contentUri = Uri.fromFile(destination);
                                                    mediaScanIntent.setData(contentUri);
                                                    sendBroadcast(mediaScanIntent);
                                                } else {
                                                    sendBroadcast(new Intent(
                                                            Intent.ACTION_MEDIA_MOUNTED,
                                                            Uri.parse("file://"
                                                                    + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))));
                                                }

                                                Log.d(TAG, "run: Sending intent..");
                                            }catch(Exception e){
                                                Log.d(TAG, "run: Sending intent failed.."+e);
                                            }
                                            //------------------------------

                                        } else {
                                            Log.v(TAG, "Copy file failed. Source file missing.");
                                        }
                                    } else {
                                        Log.d(TAG, "run: File Already Exists!");
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
            progress.dismiss();
            Toast toast = Toast.makeText(getBaseContext(), "Saved Successfully!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        finish();
    }

    public void DeleteAll() {

        int count = myAdapter.getSelectedItemCount();
        selection = myAdapter.getSelectedItemList();
        Log.d(TAG, "DeleteAll: " + count + "\n\n" + selection);

        if (count > 0) {
            progress = new ProgressDialog(this);
            progress.setMessage("Deleting...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
            Boolean result = false;

            for (int i = 0; i < count; i++) {
                final File file_path = files.get(selection.get(i));
                result = file_path.delete();

                //Sending Media Changer Broadcast
                try{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Intent mediaScanIntent = new Intent(
                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri contentUri = Uri.fromFile(file_path);
                        mediaScanIntent.setData(contentUri);
                        sendBroadcast(mediaScanIntent);
                    } else {
                        sendBroadcast(new Intent(
                                Intent.ACTION_MEDIA_MOUNTED,
                                Uri.parse("file://"
                                        + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))));
                    }

                    Log.d(TAG, "run: Sending intent..");
                }catch(Exception e){
                    Log.d(TAG, "run: Sending intent failed.."+e);
                }
                //------------------------------

            }
            if (result) {
                Toast toast = Toast.makeText(getBaseContext(), "Deleted Successfully", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            progress.dismiss();
        }

        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }

    public void displayHelp() {
        android.support.v7.app.AlertDialog.Builder builder;

        builder = new android.support.v7.app.AlertDialog.Builder(SelectionActivity.this);

        builder.setTitle("Help");
        builder.setMessage(R.string.helpMessageSelection);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.selectAll) {
            myAdapter.selectAll();
        }
        if (id == R.id.deselectAll) {
            myAdapter.deselectAll();
        }
        return true;
    }


}