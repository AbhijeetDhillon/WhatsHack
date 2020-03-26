package com.asdtechlabs.whatshack;


import android.content.Context;
import android.content.SharedPreferences;

public class SpanCount {

    private SharedPreferences sharedPreferences;

    public SpanCount(Context context){
        sharedPreferences = context.getSharedPreferences("span",Context.MODE_PRIVATE);
    }

    public int getSpanCount(){
        return sharedPreferences.getInt("count",2);
    }

    void setSpanCount(int count){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("count",count);
        editor.apply();
    }
}
