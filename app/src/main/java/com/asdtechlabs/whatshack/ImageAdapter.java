package com.asdtechlabs.whatshack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter{
    public static File updated_file_path;
    static Context mContext;
    int Imageposition;
    static ArrayList<File> files;
    String type;
    File file_path;
    static Bitmap result;
    int height, width;
    ImageAdapter(Context context, int position, ArrayList files, String type, File path, int height, int width) {
        this.mContext = context;
        this.Imageposition = position;
        this.files = files;
        this.type = type;
        this.file_path = path;
        this.height = height;
        this.width = width;

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);

    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {


        //Declaring all Image Views
        String getfile = files.get(position).getAbsolutePath();
        ImageView imageView = new ImageView(mContext);
        ImageView img_tumbnail = new ImageView(mContext);
        ImageView combinedView = new ImageView(mContext);
        //Placement of Image Views
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        combinedView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        BitmapFactory.Options options = new BitmapFactory.Options();


        if (getfile.contains(".jpg") || getfile.contains(".png")) {
           // result = BitmapFactory.decodeFile(files.get(position).getAbsolutePath());

            try{
                Glide
                        .with(mContext)
                        .load(files.get(position))
                        .into(combinedView);

            }
            catch (Exception e)
            {
                Picasso.get().load(files.get(position)).into(combinedView);
            }
            //Github library code for gestures on ImageView
                try {
                    combinedView.setOnTouchListener(new ImageMatrixTouchHandler(combinedView.getContext()));
                }
                catch (Exception e)
                {
                    Toast toast = Toast.makeText(MyApplication.getAppContext(), "Error! Cannot Zoom Images", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }


            //return imageView;
        }

        else if (getfile.contains(".mp4")) {


            //Creating Thumbnail of Video
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(files.get(position).getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
            img_tumbnail.setImageBitmap(thumb);
            //Combining play button and thumbnail
            Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_button);
            Bitmap iconplay = Bitmap.createScaledBitmap(icon,80,80,true);
            combinedView.setDrawingCacheEnabled(true);
            combinedView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            try {
                result = Bitmap.createBitmap(thumb.getWidth(), thumb.getHeight(), thumb.getConfig());
            }
            catch (NullPointerException nullpointer) {

                try {
                    result = Bitmap.createBitmap(thumb.getWidth(), thumb.getHeight(), Bitmap.Config.ARGB_8888);
                }
                catch (NullPointerException ex)
                {
                    result = Bitmap.createScaledBitmap(thumb,width,height,true);
                }
                }


            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(thumb, 0f, 0f, null);
            canvas.drawBitmap(iconplay, (thumb.getWidth() - iconplay.getWidth())/2, (thumb.getHeight()-iconplay.getHeight())/2, null);

            //((ViewPager) container).addView(combinedView, 0);
            //Onclick for Playing Video
            combinedView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                  FullScreenPreview.play_video(mContext, position);
                }
            });

            combinedView.setImageBitmap(result);

        }
        updated_file_path = new File(files.get(position).getAbsolutePath());

        ((ViewPager) container).addView(combinedView, 0);
        return combinedView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

            ((ViewPager) container).removeView((ImageView) object);

    }

    @Override
    public int getCount() {
        return files.size();
    }


    public static File saveMedia(int position)
    {
        updated_file_path = new File(files.get(position).getAbsolutePath());
        return updated_file_path;
    }






}
