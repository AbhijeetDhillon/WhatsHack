package com.asdtechlabs.whatshack;


import android.content.Context;
import android.content.SharedPreferences;

class SpanCount {

    private SharedPreferences sharedPreferences;

    SpanCount(Context context){
        sharedPreferences = context.getSharedPreferences("span",Context.MODE_PRIVATE);
    }

    int getSpanCount(){
        return sharedPreferences.getInt("count",2);
    }

    void setSpanCount(int count){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("count",count);
        editor.apply();
    }
}
