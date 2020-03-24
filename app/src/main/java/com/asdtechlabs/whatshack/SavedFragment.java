package com.asdtechlabs.whatshack;

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

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static android.content.ContentValues.TAG;


public class SavedFragment extends Fragment {

    RecyclerView recyclerView;
    ImageGalleryAdapter mAdapter;
    String dir = "WhatsApp Status";
    RelativeLayout layout;
    //private AdView mAdView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        recyclerView = view.findViewById(R.id.rv_saved);
        layout = view.findViewById(R.id.NoSavedLayout);
        mAdapter = new ImageGalleryAdapter(getContext(),getData());
        SpanCount spanCount = new SpanCount(getContext());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), spanCount.getSpanCount());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        AdView adView = new AdView(MyApplication.context, getString(R.string.FB_Banner), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
//        LinearLayout adContainer = (LinearLayout) view.findViewById(R.id.banner_container);
//
//        // Add the ad view to your activity layout
//        adContainer.addView(adView);
//
//        // Request an ad
//        adView.loadAd();
//

        return view;
    }

    private ArrayList<File> getData() {
        ArrayList<File> imageItems = new ArrayList<>();

        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/"+dir+"/");

        if (path.exists()) {

            Log.e("BlueSkyApps",""+ Arrays.toString(path.list()));
            String[] fileNames = path.list();

            if(fileNames.length!=0) {

                //Mapping
                int i=0;
                for (String fileName : fileNames) {

                    if(fileName.contains(".mp4") || fileName.contains(".jpg") || fileName.contains(".png")) {
                        imageItems.add(new File(path + "/" + fileName));
                        Log.d("Blueskyapps", "getData: "+ imageItems.get(i));
                    }
                    else Log.d(TAG, "No Videos found");

                }

                if(imageItems.size()==0){
                    Log.d("blueskyapps", "No Videos to display");
                    layout.setVisibility(View.VISIBLE);
                }
                else{
                    layout.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "getData: sorting occurs");
                    Collections.sort(imageItems, new Comparator<File>(){
                        public int compare(File f1, File f2)
                        {
                            return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
                        } });
                    return imageItems;
                }
            }else{
                Log.d("Blueskyapps", "Nothing saved to display");
                layout.setVisibility(View.VISIBLE);

            }
        }else{
            Log.d(TAG, "getData: Path is not created yet!");
            layout.setVisibility(View.VISIBLE);
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
