package com.asdtechlabs.whatshack;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;


class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder> {

    private ArrayList<File> fileArrayList;
    private Context mContext;

    @Override
    public ImageGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.thumbnail, parent, false);
        return new MyViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(ImageGalleryAdapter.MyViewHolder holder, int position){

        File filePath = fileArrayList.get(position);
        ImageView imageView = holder.mPhotoImageView;

        Glide
                .with(mContext)
                .load(filePath)
                .into(imageView);

    }


    ImageGalleryAdapter(Context context, ArrayList<File> filePath) {
        mContext = context;
        fileArrayList = filePath;
    }

    @Override
    public int getItemCount() {
        return (fileArrayList.size());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnLongClickListener{

        ImageView mPhotoImageView;

        MyViewHolder(View itemView) {
            super(itemView);

            mPhotoImageView = itemView.findViewById(R.id.image_view);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    File selectedPhoto = fileArrayList.get(position);
                    Intent intent = new Intent(mContext, FullScreenPreview.class);
                    intent.putExtra("filePath", selectedPhoto);
                    intent.putExtra("position", position);
                    intent.putExtra("allFiles", fileArrayList);
                    mContext.startActivity(intent);
                }
        }

        @Override
        public boolean onLongClick(View v) {
            Intent intent = new Intent(mContext,SelectionActivity.class);
            intent.putExtra("allFilesPath",fileArrayList);
            mContext.startActivity(intent);

            return false;
        }
    }
}
