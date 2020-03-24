package com.asdtechlabs.whatshack;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.davidecirillo.multichoicerecyclerview.MultiChoiceAdapter;

import java.io.File;
import java.util.ArrayList;


public class SelectionAdapter extends MultiChoiceAdapter<SelectionAdapter.MyViewHolder> {

    private ArrayList<File> fileArrayList;

    private Context mContext;

    @Override
    public SelectionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.thumbnail_selection, parent, false);
        SelectionAdapter.MyViewHolder viewHolder = new SelectionAdapter.MyViewHolder(photoView);
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    SelectionAdapter(Context context, ArrayList<File> filePath) {
        mContext = context;
        fileArrayList = filePath;
    }

    @Override
    public void onBindViewHolder(SelectionAdapter.MyViewHolder holder, int position) {

        super.onBindViewHolder(holder, position);
        File filePath = fileArrayList.get(position);
        ImageView imageView = holder.mPhotoImageView;

        Glide
                .with(mContext)
                .load(filePath)
                .into(imageView);

    }

    @Override
    protected View.OnClickListener defaultItemViewClickListener(SelectionAdapter.MyViewHolder holder, int position) {
        return null;
    }

    @Override
    public int getItemCount() {
        return (fileArrayList.size());
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView mPhotoImageView;

        MyViewHolder(View itemView) {
            super(itemView);
                mPhotoImageView = itemView.findViewById(R.id.image_view);
        }
    }
}
