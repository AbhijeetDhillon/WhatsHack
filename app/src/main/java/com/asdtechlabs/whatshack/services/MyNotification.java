package com.asdtechlabs.whatshack.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.asdtechlabs.whatshack.R;
import com.asdtechlabs.whatshack.activities.DeletedMessages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Abhijeet on 6/25/2019.
 * */

@SuppressLint("OverrideAbstract")
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MyNotification extends NotificationListenerService {
    Context context;
    String title, text, package_name;
    public static String ContactName;
    long date;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> checklist = new ArrayList<>();
    ArrayList<String> tempWhatsappMessage = new ArrayList<String>();
    HashMap<String, ArrayList<String>> whatsappMessage = new HashMap<String, ArrayList<String>>();
    Map<Object, ArrayList<String>> multiMap = new HashMap<>();
    ArrayList<Integer> idlist = new ArrayList<Integer>();
    Calendar calender;
    SimpleDateFormat simpledateformat;
    String time;
    public static File file;
    int id;
    Boolean containsValue = false,isGroupChat = false;
    DateFormat df = new SimpleDateFormat("HH:mm");

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        // We can read notification while posted.
        if ((sbn.getNotification().flags & Notification.FLAG_GROUP_SUMMARY) != 0) {
            //Ignore the notification
            return;
        }

        package_name = sbn.getPackageName();

        if (package_name.contains("com.whatsapp")) {
            title = sbn.getNotification().extras.getString("android.title");
            text = sbn.getNotification().extras.getString("android.text");
            date = sbn.getPostTime();
            id = sbn.getId();
            time = getDate(date);
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(text) && !title.toLowerCase().equals("whatsapp") && !text.toLowerCase().contains("new message") && !title.contains(":")) {



                    readnotification(title, text);

                } else if (!TextUtils.isEmpty(title) && title.contains(":") && !TextUtils.isEmpty(text) && !title.toLowerCase().equals("whatsapp") && !text.toLowerCase().contains("new message"))


                {

                    StringBuilder sb = new StringBuilder();
                    StringBuilder sbname = new StringBuilder();
                    if (title.contains("(") && title.contains(")")) {
                        title = title.replaceAll("\\(.*?\\)", ":");
                    }


                    String[] str = title.split("(?!^)");
                    int i=0;
                    for(int asd = 0;asd<str.length;asd++)
                    {
                        if(str[asd].equals(":")) {
                        i=asd;
                        }
                    }


                    int j;

                    if(str[i-1].equals(" "))  // line of error
                    {
                        i=i-1;
                        j=i+3;
                    }
                    else
                    {
                        j= i+2;
                    }


                    for (int z = 0; z < i; z++) {
                        sb.append(str[z]);
                    }
                    String GroupName = String.valueOf(sb);
                    for (int k = j; k <str.length; k++) {
                        sbname.append(str[k]);
                    }
                    String ParticipantName = String.valueOf(sbname);

                    readnotification(GroupName, ParticipantName + ":\n" + text);


                }

            }
        }






    public void saveMessages() throws IOException {

        if(whatsappMessage!=null) {

            if (file != null) {
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
                outputStream.writeObject(whatsappMessage);
                outputStream.flush();
                outputStream.close();

            } else {
                file = new File(getDir("data", MODE_PRIVATE), "map");
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
                outputStream.writeObject(whatsappMessage);
                outputStream.flush();
                outputStream.close();
            }
        }
    }


    private String getDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        String dateString = formatter.format(new java.util.Date(Long.parseLong(String.valueOf(time))));
        return dateString;
    }


    private void addNotification(String title) {



        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                .setSmallIcon(R.drawable.deleted) // notification icon
                .setContentTitle("WhatsHAck- Deleted") // title for notification
                .setContentText(title+" deleted some message(s)")// message for notification
                .setAutoCancel(true); // clear notification after click

        Intent intent = new Intent(getApplicationContext(), DeletedMessages.class);
        intent.putExtra("ContactName",title);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void readnotification(String title, String text) {

        if (file != null) {
            ObjectInputStream ois = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                try {
                    ois = new ObjectInputStream(new FileInputStream(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    whatsappMessage = (HashMap<String, ArrayList<String>>) ois.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


            //check if sender already present in the list
            if (whatsappMessage.containsKey(title)) {

                //is sender present, add to the same list

                if (text.toLowerCase().contains("this message was deleted")) {
                    ContactName = title;

                    ArrayList<String> stringCheck = whatsappMessage.get(title);
                    String[] parts = stringCheck.get(stringCheck.size() - 1).split("\\u0009\\u0009\\u0009");
                    String message = parts[0];

                    if(!message.equals("**Some Message(s) were deleted**"))
                    {
                        list = whatsappMessage.get(title);
                        list.add("**Some Message(s) were deleted**" + getString(R.string.tab) + time);
                        addNotification(ContactName);
                        Log.d("HashMap:MyNotification", String.valueOf(whatsappMessage));
                        try {
                            saveMessages();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }

                else if (!tempWhatsappMessage.contains(text)) {
                    list = whatsappMessage.get(title);
                    list.add(text + getString(R.string.tab) + time);
                    tempWhatsappMessage.add(text);
                    Log.d("HashMap:MyNotification", String.valueOf(whatsappMessage));
                    try {
                        saveMessages();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (tempWhatsappMessage.contains(text)) {

                    ArrayList<String> stringCheck = whatsappMessage.get(title);
                    String[] parts = stringCheck.get(stringCheck.size() - 1).split("\\u0009\\u0009\\u0009");
                    String message = parts[0]; // 004
                    if (!message.equals(text))

                    {
                        list = whatsappMessage.get(title);
                        list.add(text + getString(R.string.tab) + time);
                        tempWhatsappMessage.add(text);
                        Log.d("HashMap:MyNotification", String.valueOf(whatsappMessage));
                        try {
                            saveMessages();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (tempWhatsappMessage.size() >= 100) {
                        for (int i = 0; i <= 20; i++) {
                            tempWhatsappMessage.remove(i);
                        }
                    }
                }
            }

            // create a new list
            else {
                int idate = (int) date;
                tempWhatsappMessage.add(text);
                ArrayList<String> list = new ArrayList<>();
                list.add(text + getString(R.string.tab) + time);
                whatsappMessage.put(title, list);
                Log.d("HashMap:MyNotification", String.valueOf(whatsappMessage));
                try {
                    saveMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }



    }



