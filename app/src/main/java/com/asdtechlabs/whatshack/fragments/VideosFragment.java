package com.asdtechlabs.whatshack.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.asdtechlabs.whatshack.ImageGalleryAdapter;
import com.asdtechlabs.whatshack.MyApplication;
import com.asdtechlabs.whatshack.R;
import com.asdtechlabs.whatshack.SpanCount;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static android.content.ContentValues.TAG;


public class VideosFragment extends Fragment {


    RecyclerView recyclerView;
    com.asdtechlabs.whatshack.ImageGalleryAdapter mAdapter;
    RelativeLayout layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_videos,container,false);

        recyclerView = view.findViewById(R.id.rv_videos);
        layout = view.findViewById(R.id.NoVideosLayout);

        mAdapter = new ImageGalleryAdapter(getContext(),getData());

        SpanCount spanCount = new SpanCount(getContext());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), spanCount.getSpanCount());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        AdView adView = new AdView(MyApplication.context, getString(R.string.FB_Banner), AdSize.BANNER_HEIGHT_50);
        return view;
    }

    private ArrayList<File> getData() {
        ArrayList<File> imageItems = new ArrayList<>();
        String[] fileNames = new String[0];
        String [] fileNamesBusiness = new String[0];
        File path = new File(Environment.getExternalStorageDirectory(), "WhatsApp/Media/.Statuses/");
        File pathBusiness = new File(Environment.getExternalStorageDirectory(), "WhatsApp Business/Media/.Statuses/");
        if (path.exists() || pathBusiness.exists()) {

            Log.e("BlueSkyApps",""+ Arrays.toString(path.list()));
            if (path.exists())
            {
                fileNames = path.list();}

            if (pathBusiness.exists())
            {
                fileNamesBusiness = pathBusiness.list();

            }

            if (fileNames.length != 0 || fileNamesBusiness.length != 0)
            {
                layout.setVisibility(View.INVISIBLE);
                int i = 0;
                if(fileNames.length!=0) {



                    for (String fileName : fileNames) {
                        if (fileName.contains(".mp4")) {
                            imageItems.add(new File(path + "/" + fileName));
                            Log.d("Blueskyapps", "getData: " + imageItems.get(i));
                        } else Log.d(TAG, "No Videos found");
                    }
                }

                if (fileNamesBusiness.length!=0)
                {
                    for (String fileName : fileNamesBusiness) {
                        if(fileName.contains(".mp4")) {
                            imageItems.add(new File(pathBusiness + "/" + fileName));
                            Log.d("Blueskyapps", "getData: "+ imageItems.get(i));
                        }else Log.d(TAG, "No Videos found");
                    }

                }

                if(imageItems.size()==0){
                    Log.d("blueskyapps", "No Videos to display");
                    layout.setVisibility(View.VISIBLE);
                }else{
                    Log.d(TAG, "getData: sorting occurs");
                    Collections.sort(imageItems, new Comparator<File>(){
                        public int compare(File f1, File f2)
                        {
                            return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
                        } });
                    return imageItems;
                }
            }
            else{
                Log.d("blueskyapps", "No Videos to display");
                layout.setVisibility(View.VISIBLE);
            }
        }else{
            Toast.makeText(getContext(), "Unable to get files", Toast.LENGTH_LONG).show();
        }
        return imageItems;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            try {
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            }catch (Exception e){
                Log.d(TAG, "setUserVisibleHint: Error found");
            }
        }
    }

}
