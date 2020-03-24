package com.asdtechlabs.whatshack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Splash extends AppCompatActivity {

    static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_splash);



        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        adjustScreen(height, width);
    }


    public void gotoAPP(View View)
    {
        String url = "https://play.google.com/store/apps/details?id=com.fitsagefitness.yogaguru&referrer=utm_source%3DStatusSaver";

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    public void playApp(View View)
    {
        progressDialog = new ProgressDialog(Splash.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void adjustScreen(int height, int width)
    {
        ImageView logo = findViewById(R.id.logo);
        RelativeLayout MoreApps = findViewById(R.id.card_view);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, (int) (height*0.1), 0, (int) (height*0.01));
        params.height = (int) (0.22 * height);
        params.width = (int) (0.22 * height);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        logo.setLayoutParams(params);

        RelativeLayout.LayoutParams cd_params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        cd_params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        cd_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        cd_params.height = (int) (0.24 * height);
        cd_params.width = (int) (0.31 * height);
        cd_params.setMargins(0, (int) (height*0.01), 0, (int) (height*0.05));
        MoreApps.setLayoutParams(cd_params);
    }
}
