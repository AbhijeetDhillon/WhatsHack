package com.asdtechlabs.whatshack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

   TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        textView = findViewById(R.id.buttonHelp);
        textView.setText(R.string.helpMessage);

    }


}
