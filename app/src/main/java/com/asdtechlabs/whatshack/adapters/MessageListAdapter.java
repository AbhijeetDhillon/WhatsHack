package com.asdtechlabs.whatshack.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asdtechlabs.whatshack.R;

import java.util.ArrayList;

/**
 * Created by Abhijeet on 7/4/2019.
 */

public class MessageListAdapter extends RecyclerView.Adapter {
    private ArrayList<String> mMessageList;

    public MessageListAdapter(ArrayList<String> messageList) {
        mMessageList = messageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_message_received, viewGroup, false);
        return new ReceivedMessageHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        String string = (String) mMessageList.get(i);
        String[] parts = string.split("\\u0009\\u0009\\u0009");
        String message = parts[0]; // 004
        String time = parts[1]; // 034556
        ((ReceivedMessageHolder) viewHolder).bind(message,time);
        Log.d("Progress","Bind called");
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();

    }


    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(String message, String time) {

            messageText.setText(message);
            timeText.setText(time);
            Log.d("Progress","Reached Bind");
        }
    }
}